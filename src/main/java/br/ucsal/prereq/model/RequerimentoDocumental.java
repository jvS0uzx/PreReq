package br.ucsal.prereq.model;

import java.util.List;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("DOCUMENTAL")
public class RequerimentoDocumental extends TipoRequerimento {

    protected RequerimentoDocumental() {
    }

    @Override
    public GrupoRequerimento getGrupo() {
        return GrupoRequerimento.DOCUMENTAL;
    }

    @Override
    public List<String> alertasDoGrupo() {
        return List.of();
    }
}
