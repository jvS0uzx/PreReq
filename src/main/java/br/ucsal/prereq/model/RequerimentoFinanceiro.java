package br.ucsal.prereq.model;

import java.util.List;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("FINANCEIRO")
public class RequerimentoFinanceiro extends TipoRequerimento {

    protected RequerimentoFinanceiro() {
    }

    @Override
    public GrupoRequerimento getGrupo() {
        return GrupoRequerimento.FINANCEIRO;
    }

    @Override
    public List<String> alertasDoGrupo() {
        return List.of("O Financeiro pode recalcular suas parcelas; o pedido pode ficar semanas nesse setor.");
    }
}
