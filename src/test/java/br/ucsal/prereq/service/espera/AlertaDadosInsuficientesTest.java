package br.ucsal.prereq.service.espera;

import static br.ucsal.prereq.service.espera.SituacoesDeTeste.situacao;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import br.ucsal.prereq.model.GrupoRequerimento;

class AlertaDadosInsuficientesTest {

    private final ObservadorEspera observador = new AlertaDadosInsuficientes();
    private final List<Alerta> alertas = new ArrayList<>();

    @Test
    void deveConvidarAContribuirQuandoNaoHouverPrazoDeReferencia() {
        observador.notificar(situacao(GrupoRequerimento.DOCUMENTAL, null, 5, null), alertas);

        assertThat(alertas).containsExactly(
                new Alerta(NivelAlerta.INFO, "Ainda não temos dados; contribua quando seu pedido fechar."));
    }

    @Test
    void deveFicarQuietoQuandoHouverPrazoDeReferencia() {
        observador.notificar(situacao(GrupoRequerimento.DOCUMENTAL, null, 5, 9.0), alertas);

        assertThat(alertas).isEmpty();
    }
}
