package br.ucsal.prereq.service.espera;

import static br.ucsal.prereq.service.espera.SituacoesDeTeste.situacao;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import br.ucsal.prereq.model.GrupoRequerimento;

class AlertaSetorFinanceiroTest {

    private final ObservadorEspera observador = new AlertaSetorFinanceiro();
    private final List<Alerta> alertas = new ArrayList<>();

    @Test
    void deveExplicarORecalculoQuandoPedidoFinanceiroEstiverNoFinanceiro() {
        observador.notificar(situacao(GrupoRequerimento.FINANCEIRO, "FINANCEIRO", 12, 30.0), alertas);

        assertThat(alertas).hasSize(1);
        assertThat(alertas.get(0).nivel()).isEqualTo(NivelAlerta.INFO);
        assertThat(alertas.get(0).mensagem()).contains("recalcula as parcelas");
    }

    @Test
    void deveFicarQuietoQuandoCategoriaNaoForDoGrupoFinanceiro() {
        observador.notificar(situacao(GrupoRequerimento.ACADEMICO, "Financeiro", 12, 30.0), alertas);

        assertThat(alertas).isEmpty();
    }

    @Test
    void deveFicarQuietoQuandoPedidoEstiverEmOutroSetor() {
        observador.notificar(situacao(GrupoRequerimento.FINANCEIRO, "Contas a Receber", 12, 30.0), alertas);

        assertThat(alertas).isEmpty();
    }

    @Test
    void deveFicarQuietoQuandoSetorAtualNaoForInformado() {
        observador.notificar(situacao(GrupoRequerimento.FINANCEIRO, null, 12, 30.0), alertas);

        assertThat(alertas).isEmpty();
    }
}
