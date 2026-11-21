package br.ucsal.prereq.service.prazo;

import java.util.List;

public class CalculoMediana implements CalculoPrazo {

    @Override
    public double calcular(List<Long> dias) {
        if (dias.isEmpty()) {
            throw new IllegalArgumentException("A mediana precisa de pelo menos um valor.");
        }
        List<Long> ordenados = dias.stream().sorted().toList();
        int meio = ordenados.size() / 2;
        if (ordenados.size() % 2 == 1) {
            return ordenados.get(meio);
        }
        return (ordenados.get(meio - 1) + ordenados.get(meio)) / 2.0;
    }

    @Override
    public String nome() {
        return "Mediana";
    }
}
