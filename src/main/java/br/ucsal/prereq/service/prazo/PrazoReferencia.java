package br.ucsal.prereq.service.prazo;

import java.util.List;

public record PrazoReferencia(String calculo, double totalDias, List<PrazoSetor> setores, int amostra) {}
