package br.ucsal.prereq.service.espera;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import br.ucsal.prereq.dto.EsperaForm;
import br.ucsal.prereq.exception.RecursoNaoEncontradoException;
import br.ucsal.prereq.exception.RegraNegocioException;
import br.ucsal.prereq.model.GrupoRequerimento;
import br.ucsal.prereq.model.Setor;
import br.ucsal.prereq.model.TipoRequerimento;
import br.ucsal.prereq.repository.SetorRepository;
import br.ucsal.prereq.repository.TipoRequerimentoRepository;
import br.ucsal.prereq.service.prazo.CalculoPrazo;
import br.ucsal.prereq.service.prazo.PrazoReferencia;
import br.ucsal.prereq.service.prazo.PrazoReferenciaService;

@ExtendWith(MockitoExtension.class)
class EsperaServiceTest {

    @Mock
    private TipoRequerimentoRepository categoriaRepository;

    @Mock
    private SetorRepository setorRepository;

    @Mock
    private PrazoReferenciaService prazoService;

    private EsperaService service;

    private TipoRequerimento reembolso;

    @BeforeEach
    void montarServiceComObservadoresReais() {
        VerificadorEspera verificador = new VerificadorEspera(List.of(
                new AlertaPrazoExcedido(), new AlertaSetorFinanceiro(), new AlertaDadosInsuficientes()));
        service = new EsperaService(categoriaRepository, setorRepository, prazoService, verificador);
        reembolso = TipoRequerimento.novo(GrupoRequerimento.FINANCEIRO);
        reembolso.setNomeNoPortal("REEMBOLSO TODOS OS NIVEIS");
        ReflectionTestUtils.setField(reembolso, "id", 2L);
    }

    @Test
    void deveContarDiasCorridosEUsarOP75QuandoVerificar() {
        when(categoriaRepository.findById(2L)).thenReturn(Optional.of(reembolso));
        when(prazoService.porCategoria(eq(2L), any(CalculoPrazo.class))).thenAnswer(chamada -> {
            CalculoPrazo calculo = chamada.getArgument(1);
            return Optional.of(new PrazoReferencia(calculo.nome(), 30.0, List.of(), 4));
        });

        ResultadoEspera resultado = service.verificar(form(2L, LocalDate.now().minusDays(12), null));

        assertThat(resultado.situacao().diasCorridos()).isEqualTo(12);
        assertThat(resultado.situacao().prazo().orElseThrow().calculo()).isEqualTo("P75");
        assertThat(resultado.situacao().setorAtual()).isEmpty();
        assertThat(resultado.alertas()).isEmpty();
    }

    @Test
    void deveJuntarAlertaDePrazoEDoFinanceiroQuandoEsperaPassarDoUsualNoFinanceiro() {
        when(categoriaRepository.findById(2L)).thenReturn(Optional.of(reembolso));
        when(setorRepository.findById(7L)).thenReturn(Optional.of(new Setor("Financeiro", null)));
        when(prazoService.porCategoria(eq(2L), any(CalculoPrazo.class)))
                .thenReturn(Optional.of(new PrazoReferencia("P75", 30.0, List.of(), 4)));

        ResultadoEspera resultado = service.verificar(form(2L, LocalDate.now().minusDays(45), 7L));

        assertThat(resultado.alertas()).extracting(Alerta::nivel)
                .containsExactly(NivelAlerta.ATENCAO, NivelAlerta.INFO);
        assertThat(resultado.alertas().get(0).mensagem()).contains("Ouvidoria");
        assertThat(resultado.alertas().get(1).mensagem()).contains("recalcula as parcelas");
    }

    @Test
    void deveAvisarQueFaltamDadosQuandoCategoriaNaoTiverPrazo() {
        when(categoriaRepository.findById(2L)).thenReturn(Optional.of(reembolso));
        when(prazoService.porCategoria(eq(2L), any(CalculoPrazo.class))).thenReturn(Optional.empty());

        ResultadoEspera resultado = service.verificar(form(2L, LocalDate.now(), null));

        assertThat(resultado.situacao().diasCorridos()).isZero();
        assertThat(resultado.alertas()).extracting(Alerta::mensagem)
                .containsExactly("Ainda não temos dados; contribua quando seu pedido fechar.");
    }

    @Test
    void deveRecusarQuandoDataDeAberturaEstiverNoFuturo() {
        assertThatThrownBy(() -> service.verificar(form(2L, LocalDate.now().plusDays(1), null)))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("futuro");
        verifyNoInteractions(prazoService);
    }

    @Test
    void deveLancarNaoEncontradoQuandoCategoriaNaoExistir() {
        when(categoriaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.verificar(form(99L, LocalDate.now(), null)))
                .isInstanceOf(RecursoNaoEncontradoException.class);
    }

    @Test
    void deveLancarNaoEncontradoQuandoSetorInformadoNaoExistir() {
        when(categoriaRepository.findById(2L)).thenReturn(Optional.of(reembolso));
        when(setorRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.verificar(form(2L, LocalDate.now(), 99L)))
                .isInstanceOf(RecursoNaoEncontradoException.class);
    }

    private EsperaForm form(Long categoriaId, LocalDate dataAbertura, Long setorId) {
        EsperaForm form = new EsperaForm();
        form.setCategoriaId(categoriaId);
        form.setDataAbertura(dataAbertura);
        form.setSetorId(setorId);
        return form;
    }
}
