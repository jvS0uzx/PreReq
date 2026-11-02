package br.ucsal.prereq.service.prazo;

import java.time.LocalDate;

public record EtapaLida(String setor, LocalDate entrada, LocalDate saida, long dias) {}
