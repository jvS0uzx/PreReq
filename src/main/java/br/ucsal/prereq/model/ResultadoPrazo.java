package br.ucsal.prereq.model;

public enum ResultadoPrazo {
    DEFERIDO("Deferido"),
    INDEFERIDO("Indeferido"),
    ENCERRADO("Encerrado sem deferido nem indeferido");

    private final String rotulo;

    ResultadoPrazo(String rotulo) {
        this.rotulo = rotulo;
    }

    public String getRotulo() {
        return rotulo;
    }
}
