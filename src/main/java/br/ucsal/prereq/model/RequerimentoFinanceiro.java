package br.ucsal.prereq.model;

import java.util.List;

import br.ucsal.prereq.dto.DadosSolicitacao;
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

    @Override
    public String montarModeloTexto(DadosSolicitacao d) {
        String textoBase = super.montarModeloTexto(d);
        String regraFinanceiro = "Declaro estar ciente de que o processamento deste requerimento pode gerar recálculo de parcelas.\n\nAtenciosamente,";
        return textoBase.replace("Atenciosamente,", regraFinanceiro);
    }
}
