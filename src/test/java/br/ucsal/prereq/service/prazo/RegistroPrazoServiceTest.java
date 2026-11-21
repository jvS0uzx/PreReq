package br.ucsal.prereq.service.prazo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import br.ucsal.prereq.dto.ConfirmacaoPrazoForm;
import br.ucsal.prereq.dto.EtapaPrazoForm;
import br.ucsal.prereq.exception.RecursoNaoEncontradoException;
import br.ucsal.prereq.exception.RegraNegocioException;
import br.ucsal.prereq.model.EtapaPrazo;
import br.ucsal.prereq.model.GrupoRequerimento;
import br.ucsal.prereq.model.RegistroPrazo;
import br.ucsal.prereq.model.ResultadoPrazo;
import br.ucsal.prereq.model.Setor;
import br.ucsal.prereq.model.TipoRequerimento;
import br.ucsal.prereq.repository.RegistroPrazoRepository;
import br.ucsal.prereq.repository.SetorRepository;
import br.ucsal.prereq.repository.TipoRequerimentoRepository;

@ExtendWith(MockitoExtension.class)
class RegistroPrazoServiceTest {

    private static final String HISTORICO = """
            Histórico de etapas
            Data de conclusão:28/08/2026 10:00:00
            ABERTURA DO ATENDIMENTO
            Data de conclusão:31/08/2026 10:00:00
            ANÁLISE DO REGISTRO ACADÊMICO - protocolo 998877 de Fulano de Tal
            Data de conclusão:24/09/2026 10:00:00
            ANÁLISE DA SECRETARIA
            Data de conclusão:24/09/2026 10:00:1
            DEFERIDO
            """;

    @Mock
    private RegistroPrazoRepository repository;

    @Mock
    private TipoRequerimentoRepository categoriaRepository;

    @Mock
    private SetorRepository setorRepository;

    @InjectMocks
    private RegistroPrazoService service;

    @Test
    void deveMontarPreviaQuandoHistoricoForValido() {
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria()));

        PreviaPrazo previa = service.previsualizar(1L, HISTORICO);

        assertThat(previa.categoria().getNomeNoPortal()).isEqualTo("RENOVAÇÃO TRANCAMENTO - GRADUAÇÃO");
        assertThat(previa.resultado()).isEqualTo(ResultadoPrazo.DEFERIDO);
        assertThat(previa.totalDias()).isEqualTo(27);
        assertThat(previa.mesConclusao()).isEqualTo(YearMonth.of(2026, 9));
        assertThat(previa.etapas()).extracting(EtapaLida::setor, EtapaLida::dias)
                .containsExactly(
                        org.assertj.core.groups.Tuple.tuple("REGISTRO ACADÊMICO", 3L),
                        org.assertj.core.groups.Tuple.tuple("SECRETARIA", 24L));
        verify(repository, never()).save(any());
    }

    @Test
    void deveRepassarMensagemDoParserQuandoHistoricoForInvalido() {
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria()));

        assertThatThrownBy(() -> service.previsualizar(1L, "texto sem nenhuma data"))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessage("Não reconhecemos o histórico. Copie o bloco inteiro.");
    }

    @Test
    void deveLancarNaoEncontradoQuandoCategoriaNaoExistir() {
        when(categoriaRepository.findById(9L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.previsualizar(9L, HISTORICO))
                .isInstanceOf(RecursoNaoEncontradoException.class);
    }

    @Test
    void deveSalvarSemNenhumTrechoDoTextoOriginalQuandoConfirmar() {
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria()));
        when(setorRepository.findFirstByNomeStartingWithIgnoreCaseOrderByNome("REGISTRO ACADÊMICO"))
                .thenReturn(Optional.of(new Setor("Registro Acadêmico", null)));
        when(setorRepository.findFirstByNomeStartingWithIgnoreCaseOrderByNome("SECRETARIA"))
                .thenReturn(Optional.empty());
        when(repository.save(any(RegistroPrazo.class))).thenAnswer(i -> i.getArgument(0));

        service.confirmar(formDaPrevia(service.previsualizar(1L, HISTORICO)));

        ArgumentCaptor<RegistroPrazo> salvo = ArgumentCaptor.forClass(RegistroPrazo.class);
        verify(repository).save(salvo.capture());
        RegistroPrazo registro = salvo.getValue();
        List<String> textosSalvos = new ArrayList<>();
        textosSalvos.add(registro.getResultado().name());
        textosSalvos.add(registro.getMesConclusao().toString());
        for (EtapaPrazo etapa : registro.getEtapas()) {
            textosSalvos.add(String.valueOf(etapa.getSetorNaoCadastrado()));
            textosSalvos.add(etapa.getSetor() == null ? "" : etapa.getSetor().getNome());
        }
        assertThat(textosSalvos).noneMatch(texto -> texto.contains("998877")
                || texto.contains("Fulano")
                || texto.contains("Data de conclusão")
                || texto.contains("31/08"));
        assertThat(registro.getMesConclusao()).isEqualTo(YearMonth.of(2026, 9));
    }

    @Test
    void deveLigarSetorCadastradoEGuardarNomeDoQueNaoExisteQuandoConfirmar() {
        Setor registroAcademico = new Setor("Registro Acadêmico", null);
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria()));
        when(setorRepository.findFirstByNomeStartingWithIgnoreCaseOrderByNome("REGISTRO ACADÊMICO"))
                .thenReturn(Optional.of(registroAcademico));
        when(setorRepository.findFirstByNomeStartingWithIgnoreCaseOrderByNome("SECRETARIA"))
                .thenReturn(Optional.empty());
        when(repository.save(any(RegistroPrazo.class))).thenAnswer(i -> i.getArgument(0));

        RegistroPrazo registro = service.confirmar(form(ResultadoPrazo.DEFERIDO, 27, "2026-09",
                etapa("REGISTRO ACADÊMICO", 3), etapa("SECRETARIA", 24)));

        assertThat(registro.getTotalDias()).isEqualTo(27);
        assertThat(registro.getEtapas()).hasSize(2);
        assertThat(registro.getEtapas().get(0).getSetor()).isSameAs(registroAcademico);
        assertThat(registro.getEtapas().get(0).getSetorNaoCadastrado()).isNull();
        assertThat(registro.getEtapas().get(0).getOrdem()).isEqualTo(1);
        assertThat(registro.getEtapas().get(1).getSetor()).isNull();
        assertThat(registro.getEtapas().get(1).getSetorNaoCadastrado()).isEqualTo("SECRETARIA");
        assertThat(registro.getEtapas().get(1).getDias()).isEqualTo(24);
        assertThat(registro.getEtapas().get(1).getOrdem()).isEqualTo(2);
    }

    @Test
    void deveRecusarQuandoMesDeConclusaoEstiverNoFuturo() {
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria()));
        String mesQueVem = YearMonth.now().plusMonths(1).toString();

        assertThatThrownBy(() -> service.confirmar(form(ResultadoPrazo.ENCERRADO, 5, mesQueVem)))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("futuro");
        verify(repository, never()).save(any());
    }

    @Test
    void deveRecusarQuandoSomaDasEtapasPassarDoTotal() {
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria()));

        assertThatThrownBy(() -> service.confirmar(form(ResultadoPrazo.DEFERIDO, 5, "2026-09",
                etapa("FINANCEIRO", 4), etapa("SECRETARIA", 4))))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("total");
        verify(repository, never()).save(any());
    }

    private ConfirmacaoPrazoForm formDaPrevia(PreviaPrazo previa) {
        EtapaPrazoForm[] etapas = previa.etapas().stream()
                .map(etapa -> etapa(etapa.setor(), (int) etapa.dias()))
                .toArray(EtapaPrazoForm[]::new);
        return form(previa.resultado(), (int) previa.totalDias(), previa.mesConclusao().toString(), etapas);
    }

    private ConfirmacaoPrazoForm form(ResultadoPrazo resultado, int totalDias, String mes, EtapaPrazoForm... etapas) {
        ConfirmacaoPrazoForm form = new ConfirmacaoPrazoForm();
        form.setCategoriaId(1L);
        form.setResultado(resultado);
        form.setTotalDias(totalDias);
        form.setMesConclusao(mes);
        form.setEtapas(new ArrayList<>(List.of(etapas)));
        return form;
    }

    private EtapaPrazoForm etapa(String setor, int dias) {
        EtapaPrazoForm etapa = new EtapaPrazoForm();
        etapa.setSetor(setor);
        etapa.setDias(dias);
        return etapa;
    }

    private TipoRequerimento categoria() {
        TipoRequerimento categoria = TipoRequerimento.novo(GrupoRequerimento.ACADEMICO);
        categoria.setNomeNoPortal("RENOVAÇÃO TRANCAMENTO - GRADUAÇÃO");
        ReflectionTestUtils.setField(categoria, "id", 1L);
        return categoria;
    }
}
