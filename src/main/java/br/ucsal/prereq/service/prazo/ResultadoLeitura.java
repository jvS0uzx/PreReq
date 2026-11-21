package br.ucsal.prereq.service.prazo;

import java.time.LocalDate;
import java.util.List;

public record ResultadoLeitura(List<EtapaLida> etapas, ResultadoFinal resultado, long totalDias,
        LocalDate conclusao) {}
