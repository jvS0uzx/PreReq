package br.ucsal.prereq.service.espera;

import java.util.List;

public record ResultadoEspera(SituacaoEspera situacao, List<Alerta> alertas) {}
