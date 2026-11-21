package br.ucsal.prereq.service.prazo;

import java.util.List;

public class CalculoPercentil implements CalculoPrazo {

    private final int percentil;

    public CalculoPercentil(int percentil) {
        if (percentil < 0 || percentil > 100) {
            throw new IllegalArgumentException("O percentil deve ficar entre 0 e 100.");
        }
        this.percentil = percentil;
    }

    @Override
    public double calcular(List<Long> dias) {
        if (dias.isEmpty()) {
            throw new IllegalArgumentException("O percentil precisa de pelo menos um valor.");
        }
        List<Long> ordenados = dias.stream().sorted().toList();
        double posicao = percentil / 100.0 * (ordenados.size() - 1);
        int abaixo = (int) Math.floor(posicao);
        int acima = (int) Math.ceil(posicao);
        double fracao = posicao - abaixo;
        return ordenados.get(abaixo) + fracao * (ordenados.get(acima) - ordenados.get(abaixo));
    }

    @Override
    public String nome() {
        return "P" + percentil;
    }
}
