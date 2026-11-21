package br.ucsal.prereq.service.espera;

import java.util.List;

public interface ObservadorEspera {

    void notificar(SituacaoEspera situacao, List<Alerta> alertas);
}
