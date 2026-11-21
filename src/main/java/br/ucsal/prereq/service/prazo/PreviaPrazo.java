package br.ucsal.prereq.service.prazo;

import java.time.YearMonth;
import java.util.List;

import br.ucsal.prereq.model.ResultadoPrazo;
import br.ucsal.prereq.model.TipoRequerimento;

public record PreviaPrazo(TipoRequerimento categoria, ResultadoPrazo resultado, long totalDias,
        YearMonth mesConclusao, List<EtapaLida> etapas) {}
