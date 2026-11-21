package br.ucsal.prereq.service.prazo;

import java.util.List;

public interface CalculoPrazo {

    double calcular(List<Long> dias);

    String nome();
}
