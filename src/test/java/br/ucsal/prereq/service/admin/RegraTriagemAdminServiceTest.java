package br.ucsal.prereq.service.admin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.ucsal.prereq.dto.RegraForm;
import br.ucsal.prereq.exception.RegraNegocioException;
import br.ucsal.prereq.model.GrupoRequerimento;
import br.ucsal.prereq.model.OrigemRegra;
import br.ucsal.prereq.model.RegraTriagem;
import br.ucsal.prereq.model.TipoRequerimento;
import br.ucsal.prereq.repository.RegraTriagemRepository;
import br.ucsal.prereq.repository.TipoRequerimentoRepository;
import br.ucsal.prereq.service.IndeferimentoService;

@ExtendWith(MockitoExtension.class)
class RegraTriagemAdminServiceTest {

    @Mock
    private RegraTriagemRepository repository;

    @Mock
    private TipoRequerimentoRepository categoriaRepository;

    @Mock
    private IndeferimentoService indeferimentoService;

    @InjectMocks
    private RegraTriagemAdminService service;

    @Test
    void deveRecusarQuandoCategoriaECanalPreenchidos() {
        RegraForm form = form("trancar");
        form.setCategoriaId(1L);
        form.setCanalAlternativo("Ouvidoria");

        assertThatThrownBy(() -> service.salvar(form))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("não os dois");
        verify(repository, never()).save(any());
    }

    @Test
    void deveMarcarIndeferimentoComoConvertidoQuandoSalvarRegraVindaDeIndeferimento() {
        RegraForm form = form("encerrar estagio");
        form.setCanalAlternativo("Coordenação do curso");
        form.setOrigem(OrigemRegra.INDEFERIMENTO);
        form.setIndeferimentoId(8L);
        when(repository.save(any(RegraTriagem.class))).thenAnswer(i -> i.getArgument(0));

        service.salvar(form);

        verify(indeferimentoService).marcarConvertido(8L, null);
    }

    @Test
    void deveDeixarIndeferimentosEmPazQuandoRegraForManual() {
        RegraForm form = form("encerrar estagio");
        form.setCanalAlternativo("Coordenação do curso");
        when(repository.save(any(RegraTriagem.class))).thenAnswer(i -> i.getArgument(0));

        service.salvar(form);

        org.mockito.Mockito.verifyNoInteractions(indeferimentoService);
    }

    @Test
    void deveRecusarQuandoNenhumDestinoPreenchido() {
        RegraForm form = form("trancar");
        form.setCanalAlternativo("   ");

        assertThatThrownBy(() -> service.salvar(form))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("categoria ou o canal");
        verify(repository, never()).save(any());
    }

    @Test
    void deveNormalizarCadaTermoMantendoVirgulasQuandoSalvar() {
        RegraForm form = form(" Trancar, TRANCAMENTO ,, trancar; Matrícula ");
        form.setCanalAlternativo("Ouvidoria");
        when(repository.save(any(RegraTriagem.class))).thenAnswer(i -> i.getArgument(0));

        RegraTriagem salva = service.salvar(form);

        assertThat(salva.getPalavrasChave()).isEqualTo("trancar, trancamento, trancar matricula");
    }

    @Test
    void deveRecusarQuandoNaoSobrarTermo() {
        RegraForm form = form(" , ; ,");
        form.setCanalAlternativo("Ouvidoria");

        assertThatThrownBy(() -> service.salvar(form)).isInstanceOf(RegraNegocioException.class);
        verify(repository, never()).save(any());
    }

    @Test
    void deveGravarDataRevisaoEOrigemManualQuandoNovaSemOrigem() {
        RegraForm form = form("trancar");
        form.setCanalAlternativo(" Ouvidoria ");
        when(repository.save(any(RegraTriagem.class))).thenAnswer(i -> i.getArgument(0));

        RegraTriagem salva = service.salvar(form);

        assertThat(salva.getDataRevisao()).isEqualTo(LocalDate.now());
        assertThat(salva.getOrigem()).isEqualTo(OrigemRegra.MANUAL);
        assertThat(salva.getCanalAlternativo()).isEqualTo("Ouvidoria");
        assertThat(salva.getCategoria()).isNull();
        assertThat(salva.isAtiva()).isTrue();
    }

    @Test
    void deveManterOrigemIndeferimentoQuandoInformada() {
        RegraForm form = form("trancar");
        form.setCanalAlternativo("Ouvidoria");
        form.setOrigem(OrigemRegra.INDEFERIMENTO);
        when(repository.save(any(RegraTriagem.class))).thenAnswer(i -> i.getArgument(0));

        RegraTriagem salva = service.salvar(form);

        assertThat(salva.getOrigem()).isEqualTo(OrigemRegra.INDEFERIMENTO);
    }

    @Test
    void deveVincularCategoriaQuandoInformada() {
        TipoRequerimento categoria = TipoRequerimento.novo(GrupoRequerimento.ACADEMICO);
        RegraForm form = form("trancar");
        form.setCategoriaId(3L);
        when(categoriaRepository.findById(3L)).thenReturn(Optional.of(categoria));
        when(repository.save(any(RegraTriagem.class))).thenAnswer(i -> i.getArgument(0));

        RegraTriagem salva = service.salvar(form);

        assertThat(salva.getCategoria()).isSameAs(categoria);
        assertThat(salva.getCanalAlternativo()).isNull();
    }

    @Test
    void deveAtualizarRegraExistenteMantendoOrigemQuandoEditar() {
        RegraTriagem existente = new RegraTriagem("antiga", OrigemRegra.INDEFERIMENTO);
        RegraForm form = form("nova");
        form.setId(7L);
        form.setCanalAlternativo("Ouvidoria");
        when(repository.findById(7L)).thenReturn(Optional.of(existente));
        when(repository.save(any(RegraTriagem.class))).thenAnswer(i -> i.getArgument(0));

        RegraTriagem salva = service.salvar(form);

        assertThat(salva).isSameAs(existente);
        assertThat(salva.getPalavrasChave()).isEqualTo("nova");
        assertThat(salva.getOrigem()).isEqualTo(OrigemRegra.INDEFERIMENTO);
    }

    @Test
    void deveMarcarComoInativaSemExcluirQuandoDesativar() {
        RegraTriagem regra = new RegraTriagem("trancar", OrigemRegra.MANUAL);
        when(repository.findById(1L)).thenReturn(Optional.of(regra));

        service.desativar(1L);

        ArgumentCaptor<RegraTriagem> captor = ArgumentCaptor.forClass(RegraTriagem.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().isAtiva()).isFalse();
        verify(repository, never()).delete(any());
        verify(repository, never()).deleteById(any());
    }

    private RegraForm form(String palavras) {
        RegraForm form = new RegraForm();
        form.setPalavrasChave(palavras);
        return form;
    }
}
