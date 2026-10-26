package br.ucsal.prereq.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class TipoRequerimentoTest {

    @Test
    void deveCriarRequerimentoFinanceiroQuandoGrupoForFinanceiro() {
        TipoRequerimento tipo = TipoRequerimento.novo(GrupoRequerimento.FINANCEIRO);

        assertThat(tipo).isInstanceOf(RequerimentoFinanceiro.class);
    }

    @ParameterizedTest
    @EnumSource(GrupoRequerimento.class)
    void deveInformarOProprioGrupoQuandoCriadoPorEle(GrupoRequerimento grupo) {
        TipoRequerimento tipo = TipoRequerimento.novo(grupo);

        assertThat(tipo.getGrupo()).isEqualTo(grupo);
    }

    @Test
    void deveImpedirAlteracaoDosSetoresQuandoListaVemDoGetter() {
        TipoRequerimento tipo = TipoRequerimento.novo(GrupoRequerimento.ACADEMICO);
        tipo.setSetores(List.of(new Setor("Secretaria", null)));

        assertThatThrownBy(() -> tipo.getSetores().add(new Setor("Financeiro", null)))
                .isInstanceOf(UnsupportedOperationException.class);
    }
}
