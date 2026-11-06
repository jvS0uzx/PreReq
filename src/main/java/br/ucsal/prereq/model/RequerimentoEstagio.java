package br.ucsal.prereq.model;

import java.util.List;

import br.ucsal.prereq.dto.DadosSolicitacao;
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

    @Override
    public String montarModeloTexto(DadosSolicitacao d) {
        String textoBase = super.montarModeloTexto(d);
        String regraEstagio = "Empresa concedente: " + d.getEmpresaEstagio() + "\n" +
                              "Solicito também a baixa de eventuais estágios anteriores (se aplicável).\n\nAtenciosamente,";
        return textoBase.replace("Atenciosamente,", regraEstagio);
    }
}
