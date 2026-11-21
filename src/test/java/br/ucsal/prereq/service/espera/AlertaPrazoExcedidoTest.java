package br.ucsal.prereq.service.espera;

import static br.ucsal.prereq.service.espera.SituacoesDeTeste.situacao;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import br.ucsal.prereq.model.GrupoRequerimento;

class AlertaPrazoExcedidoTest {

    private final ObservadorEspera observador = new AlertaPrazoExcedido();
    private final List<Alerta> alertas = new ArrayList<>();

    @Test
    void deveAlertarQuandoDiasPassaremDoP75() {
        observador.notificar(situacao(GrupoRequerimento.ACADEMICO, null, 33, 32.5), alertas);

        assertThat(alertas).containsExactly(
                new Alerta(NivelAlerta.ATENCAO, "Sua espera passou do usual; procure a Ouvidoria."));
    }

    @Test
    void deveFicarQuietoQuandoDiasForemIguaisAoP75() {
        observador.notificar(situacao(GrupoRequerimento.ACADEMICO, null, 32, 32.0), alertas);

        assertThat(alertas).isEmpty();
    }

    @Test
    void deveFicarQuietoQuandoNaoHouverPrazoDeReferencia() {
        observador.notificar(situacao(GrupoRequerimento.ACADEMICO, null, 400, null), alertas);

        assertThat(alertas).isEmpty();
    }
}
