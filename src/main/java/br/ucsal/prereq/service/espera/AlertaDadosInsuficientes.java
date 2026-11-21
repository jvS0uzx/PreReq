package br.ucsal.prereq.service.espera;

import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class AlertaDadosInsuficientes implements ObservadorEspera {

    static final String MENSAGEM = "Ainda não temos dados; contribua quando seu pedido fechar.";

    @Override
    public void notificar(SituacaoEspera situacao, List<Alerta> alertas) {
        if (situacao.prazo().isEmpty()) {
            alertas.add(new Alerta(NivelAlerta.INFO, MENSAGEM));
        }
    }
}
