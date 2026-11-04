package br.ucsal.prereq.model;

public enum GrupoRequerimento {
    ACADEMICO("Acadêmico"),
    FINANCEIRO("Financeiro"),
    ESTAGIO("Estágio"),
    DOCUMENTAL("Documental");

    private final String rotulo;

    GrupoRequerimento(String rotulo) {
        this.rotulo = rotulo;
    }

    public String getRotulo() {
        return rotulo;
    }
}
