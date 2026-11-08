package br.ucsal.prereq.model;

import java.util.List;

import br.ucsal.prereq.dto.DadosSolicitacao;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("ACADEMICO")
public class RequerimentoAcademico extends TipoRequerimento {

    protected RequerimentoAcademico() {
    }

    @Override
    public GrupoRequerimento getGrupo() {
        return GrupoRequerimento.ACADEMICO;
    }

    @Override
    public List<String> alertasDoGrupo() {
        return List.of("Se você é bolsista ProUni/FIES, o pedido também passa pela análise da bolsa.");
    }

    @Override
    protected String obterRegraEspecifica(DadosSolicitacao d) {
        if (d.getDisciplina() != null && !d.getDisciplina().trim().isEmpty()) {
            return "Disciplina relacionada: " + d.getDisciplina();
        }
        return "";
    }
}
