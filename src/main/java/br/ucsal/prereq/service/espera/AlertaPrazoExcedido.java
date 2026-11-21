package br.ucsal.prereq.service.espera;

import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class AlertaPrazoExcedido implements ObservadorEspera {

    static final String MENSAGEM = "Sua espera passou do usual; procure a Ouvidoria.";

    @Override
    public void notificar(SituacaoEspera situacao, List<Alerta> alertas) {
        situacao.prazo()
                .filter(prazo -> situacao.diasCorridos() > prazo.totalDias())
                .ifPresent(prazo -> alertas.add(new Alerta(NivelAlerta.ATENCAO, MENSAGEM)));
    }
}
