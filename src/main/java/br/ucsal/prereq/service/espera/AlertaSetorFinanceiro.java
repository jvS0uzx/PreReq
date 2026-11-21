package br.ucsal.prereq.service.espera;

import java.util.List;

import org.springframework.stereotype.Component;

import br.ucsal.prereq.model.GrupoRequerimento;

@Component
public class AlertaSetorFinanceiro implements ObservadorEspera {

    static final String SETOR = "Financeiro";
    static final String MENSAGEM = "Seu pedido está no Financeiro, que recalcula as parcelas antes de concluir. "
            + "Essa etapa costuma ser a mais demorada e pode levar semanas.";

    @Override
    public void notificar(SituacaoEspera situacao, List<Alerta> alertas) {
        boolean noFinanceiro = situacao.setorAtual()
                .map(setor -> SETOR.equalsIgnoreCase(setor.getNome()))
                .orElse(false);
        if (noFinanceiro && situacao.categoria().getGrupo() == GrupoRequerimento.FINANCEIRO) {
            alertas.add(new Alerta(NivelAlerta.INFO, MENSAGEM));
        }
    }
}
