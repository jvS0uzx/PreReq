package br.ucsal.prereq.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class TipoRequerimentoTest {

    @Test
    void deveCriarRequerimentoFinanceiroQuandoGrupoForFinanceiro() {
        TipoRequerimento tipo = TipoRequerimento.novo(GrupoRequerimento.FINANCEIRO);

        assertThat(tipo).isInstanceOf(RequerimentoFinanceiro.class);
    }

    @Test
    void deveInformarGrupoAcademicoQuandoCriarRequerimentoAcademico() {
        TipoRequerimento tipo = TipoRequerimento.novo(GrupoRequerimento.ACADEMICO);

        assertThat(tipo.getGrupo()).isEqualTo(GrupoRequerimento.FINANCEIRO);
    }
}
