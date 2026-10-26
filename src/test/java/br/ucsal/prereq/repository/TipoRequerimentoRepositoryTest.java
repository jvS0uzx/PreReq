package br.ucsal.prereq.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import br.ucsal.prereq.model.Documento;
import br.ucsal.prereq.model.GrupoRequerimento;
import br.ucsal.prereq.model.RequerimentoFinanceiro;
import br.ucsal.prereq.model.Setor;
import br.ucsal.prereq.model.TipoRequerimento;

@DataJpaTest
class TipoRequerimentoRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TipoRequerimentoRepository repository;

    @Test
    void deveManterOrdemDosSetoresQuandoLerRequerimentoSalvo() {
        Setor secretaria = entityManager.persist(new Setor("Secretaria", null));
        Setor financeiro = entityManager.persist(new Setor("Financeiro", null));
        Documento boleto = entityManager.persist(new Documento("Boleto quitado", null, null));
        Documento declaracao = entityManager.persist(new Documento("Declaração de matrícula", null, null));

        TipoRequerimento tipo = TipoRequerimento.novo(GrupoRequerimento.FINANCEIRO);
        tipo.setNomeNoPortal("Revisão de mensalidade");
        tipo.setDocumentos(List.of(boleto, declaracao));
        tipo.setSetores(List.of(financeiro, secretaria));
        repository.save(tipo);
        entityManager.flush();
        entityManager.clear();

        TipoRequerimento lido = repository.findByNomeNoPortal("Revisão de mensalidade").orElseThrow();

        assertThat(lido).isInstanceOf(RequerimentoFinanceiro.class);
        assertThat(lido.getDocumentos()).hasSize(2);
        assertThat(lido.getSetores()).extracting(Setor::getNome).containsExactly("Financeiro", "Secretaria");
    }

    @Test
    void deveListarSomenteAtivosEmOrdemAlfabeticaQuandoBuscarAtivos() {
        salvar("Trancamento de matrícula", true);
        salvar("Declaração de vínculo", true);
        salvar("Atestado antigo", false);

        List<TipoRequerimento> ativos = repository.findByAtivoTrueOrderByNomeNoPortal();

        assertThat(ativos).extracting(TipoRequerimento::getNomeNoPortal)
                .containsExactly("Declaração de vínculo", "Trancamento de matrícula");
    }

    private void salvar(String nomeNoPortal, boolean ativo) {
        TipoRequerimento tipo = TipoRequerimento.novo(GrupoRequerimento.DOCUMENTAL);
        tipo.setNomeNoPortal(nomeNoPortal);
        tipo.setAtivo(ativo);
        entityManager.persist(tipo);
    }
}
