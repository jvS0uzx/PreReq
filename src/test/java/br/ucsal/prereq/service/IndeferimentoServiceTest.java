package br.ucsal.prereq.service;

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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.ucsal.prereq.dto.IndeferimentoForm;
import br.ucsal.prereq.exception.RecursoNaoEncontradoException;
import br.ucsal.prereq.exception.RegraNegocioException;
import br.ucsal.prereq.model.GrupoRequerimento;
import br.ucsal.prereq.model.Indeferimento;
import br.ucsal.prereq.model.StatusIndeferimento;
import br.ucsal.prereq.model.TipoRequerimento;
import br.ucsal.prereq.repository.IndeferimentoRepository;
import br.ucsal.prereq.repository.TipoRequerimentoRepository;

@ExtendWith(MockitoExtension.class)
class IndeferimentoServiceTest {

    @Mock
    private IndeferimentoRepository repository;

    @Mock
    private TipoRequerimentoRepository categoriaRepository;

    @InjectMocks
    private IndeferimentoService service;

    private final TipoRequerimento dispensa = categoria("SOLICITAÇÃO DE DISPENSA DE ESTÁGIO CURRICULAR - DIREITO");

    @Test
    void deveNascerPendenteComDataDeHojeQuandoRegistrar() {
        IndeferimentoForm form = new IndeferimentoForm();
        form.setCategoriaEscolhidaId(4L);
        form.setNecessidade("  precisava encerrar meu estágio  ");
        form.setMotivoInformado(" campo equivocado ");
        when(categoriaRepository.findById(4L)).thenReturn(Optional.of(dispensa));
        when(repository.save(any(Indeferimento.class))).thenAnswer(i -> i.getArgument(0));

        Indeferimento salvo = service.registrar(form);

        assertThat(salvo.getStatus()).isEqualTo(StatusIndeferimento.PENDENTE);
        assertThat(salvo.getDataRegistro()).isEqualTo(LocalDate.now());
        assertThat(salvo.getCategoriaEscolhida()).isSameAs(dispensa);
        assertThat(salvo.getOQueOAlunoPrecisava()).isEqualTo("precisava encerrar meu estágio");
        assertThat(salvo.getMotivoInformado()).isEqualTo("campo equivocado");
        assertThat(salvo.getCategoriaCorreta()).isNull();
    }

    @Test
    void deveLancarNaoEncontradoQuandoCategoriaEscolhidaNaoExistir() {
        IndeferimentoForm form = new IndeferimentoForm();
        form.setCategoriaEscolhidaId(99L);
        when(categoriaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.registrar(form)).isInstanceOf(RecursoNaoEncontradoException.class);
        verify(repository, never()).save(any());
    }

    @Test
    void deveMudarParaDescartadoQuandoDescartarPendente() {
        Indeferimento pendente = indeferimento();
        when(repository.findWithCategoriaEscolhidaById(1L)).thenReturn(Optional.of(pendente));

        service.descartar(1L);

        assertThat(pendente.getStatus()).isEqualTo(StatusIndeferimento.DESCARTADO);
        verify(repository).save(pendente);
    }

    @Test
    void deveMudarParaConvertidoEGuardarCategoriaCorretaQuandoMarcarConvertido() {
        Indeferimento pendente = indeferimento();
        TipoRequerimento correta = categoria("ASSINATURA DE CONTRATO DE ESTÁGIO - GRADUAÇÃO");
        when(repository.findWithCategoriaEscolhidaById(1L)).thenReturn(Optional.of(pendente));

        service.marcarConvertido(1L, correta);

        assertThat(pendente.getStatus()).isEqualTo(StatusIndeferimento.CONVERTIDO);
        assertThat(pendente.getCategoriaCorreta()).isSameAs(correta);
        verify(repository).save(pendente);
    }

    @Test
    void deveAceitarConversaoSemCategoriaQuandoRegraApontarCanalAlternativo() {
        Indeferimento pendente = indeferimento();
        when(repository.findWithCategoriaEscolhidaById(1L)).thenReturn(Optional.of(pendente));

        service.marcarConvertido(1L, null);

        assertThat(pendente.getStatus()).isEqualTo(StatusIndeferimento.CONVERTIDO);
        assertThat(pendente.getCategoriaCorreta()).isNull();
    }

    @Test
    void deveRecusarQuandoDescartarIndeferimentoJaConvertido() {
        Indeferimento convertido = indeferimento();
        convertido.setStatus(StatusIndeferimento.CONVERTIDO);
        when(repository.findWithCategoriaEscolhidaById(1L)).thenReturn(Optional.of(convertido));

        assertThatThrownBy(() -> service.descartar(1L))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("já foi tratado");
        assertThat(convertido.getStatus()).isEqualTo(StatusIndeferimento.CONVERTIDO);
        verify(repository, never()).save(any());
    }

    @Test
    void deveRecusarQuandoConverterIndeferimentoJaDescartado() {
        Indeferimento descartado = indeferimento();
        descartado.setStatus(StatusIndeferimento.DESCARTADO);
        when(repository.findWithCategoriaEscolhidaById(1L)).thenReturn(Optional.of(descartado));

        assertThatThrownBy(() -> service.marcarConvertido(1L, dispensa))
                .isInstanceOf(RegraNegocioException.class);
        assertThat(descartado.getStatus()).isEqualTo(StatusIndeferimento.DESCARTADO);
    }

    @Test
    void deveLancarNaoEncontradoQuandoIndeferimentoNaoExistir() {
        when(repository.findWithCategoriaEscolhidaById(7L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.descartar(7L)).isInstanceOf(RecursoNaoEncontradoException.class);
    }

    private Indeferimento indeferimento() {
        return new Indeferimento(dispensa, "precisava encerrar meu estágio", "campo equivocado",
                LocalDate.of(2026, 11, 20));
    }

    private static TipoRequerimento categoria(String nome) {
        TipoRequerimento categoria = TipoRequerimento.novo(GrupoRequerimento.ESTAGIO);
        categoria.setNomeNoPortal(nome);
        return categoria;
    }
}
