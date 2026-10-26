package br.ucsal.prereq.model;

import java.util.List;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("ESTAGIO")
public class RequerimentoEstagio extends TipoRequerimento {

    protected RequerimentoEstagio() {
    }

    @Override
    public GrupoRequerimento getGrupo() {
        return GrupoRequerimento.ESTAGIO;
    }

    @Override
    public List<String> alertasDoGrupo() {
        return List.of("Confira se a categoria serve para o seu curso; algumas são exclusivas de Direito.");
    }
}
