package br.ucsal.prereq.model;

import br.ucsal.prereq.dto.DadosSolicitacao;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class TipoRequerimentoTest {

    @Test
    void deveGerarTextoPadraoParaDocumental() {
        TipoRequerimento req = TipoRequerimento.novo(GrupoRequerimento.DOCUMENTAL);
        req.setNomeNoPortal("Histórico Escolar");

        DadosSolicitacao dados = new DadosSolicitacao();
        dados.setCurso("Engenharia de Software");
        dados.setSemestre("2026.2");
        dados.setMotivo("Transferência");

        String texto = req.montarModeloTexto(dados);

        assertTrue(texto.contains("Histórico Escolar"));
        assertTrue(texto.contains("Transferência"));
        assertTrue(texto.contains("Engenharia de Software"));
        assertTrue(texto.contains("Atenciosamente,"));
    }

    @Test
    void deveIncluirRegraFinanceiro() {
        TipoRequerimento req = TipoRequerimento.novo(GrupoRequerimento.FINANCEIRO);
        req.setNomeNoPortal("Reembolso");

        DadosSolicitacao dados = new DadosSolicitacao();
        dados.setCurso("Engenharia de Software");
        dados.setSemestre("2026.2");
        dados.setMotivo("Cobrança indevida");

        String texto = req.montarModeloTexto(dados);

        assertTrue(texto.contains("gerar recálculo de parcelas"));
    }

    @Test
    void deveIncluirRegraAcademico() {
        TipoRequerimento req = TipoRequerimento.novo(GrupoRequerimento.ACADEMICO);
        req.setNomeNoPortal("Revisão de Nota");

        DadosSolicitacao dados = new DadosSolicitacao();
        dados.setCurso("Engenharia de Software");
        dados.setSemestre("2026.2");
        dados.setMotivo("Erro na soma");
        dados.setDisciplina("Sistemas Distribuídos");

        String texto = req.montarModeloTexto(dados);

        assertTrue(texto.contains("Disciplina relacionada: Sistemas Distribuídos"));
    }

    @Test
    void deveIncluirRegraEstagio() {
        TipoRequerimento req = TipoRequerimento.novo(GrupoRequerimento.ESTAGIO);
        req.setNomeNoPortal("Assinatura de Contrato");

        DadosSolicitacao dados = new DadosSolicitacao();
        dados.setCurso("Engenharia de Software");
        dados.setSemestre("2026.2");
        dados.setMotivo("Novo estágio");
        dados.setEmpresaEstagio("Sebrae Bahia");

        String texto = req.montarModeloTexto(dados);

        assertTrue(texto.contains("Empresa concedente: Sebrae Bahia"));
        assertTrue(texto.contains("baixa de eventuais estágios anteriores"));
    }
}
