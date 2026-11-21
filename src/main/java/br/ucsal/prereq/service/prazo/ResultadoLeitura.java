package br.ucsal.prereq.service.prazo;

import java.util.List;

public record ResultadoLeitura(List<EtapaLida> etapas, ResultadoFinal resultado, long totalDias) {}
