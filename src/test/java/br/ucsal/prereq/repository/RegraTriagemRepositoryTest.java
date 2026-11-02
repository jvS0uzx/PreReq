package br.ucsal.prereq.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import br.ucsal.prereq.model.GrupoRequerimento;
import br.ucsal.prereq.model.OrigemRegra;
import br.ucsal.prereq.model.RegraTriagem;
import br.ucsal.prereq.model.TipoRequerimento;

@DataJpaTest
class RegraTriagemRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private RegraTriagemRepository repository;

    @Test
    void deveListarSomenteRegrasAtivasQuandoBuscarAtivas() {
        salvar("trancar,trancamento", true, null);
        salvar("estagio,dispensa", false, null);

        List<RegraTriagem> ativas = repository.findByAtivaTrue();

        assertThat(ativas).extracting(RegraTriagem::getPalavrasChave).containsExactly("trancar,trancamento");
    }

    @Test
    void deveTrazerCategoriaDaRegraQuandoBuscarAtivas() {
        TipoRequerimento trancamento = TipoRequerimento.novo(GrupoRequerimento.ACADEMICO);
        trancamento.setNomeNoPortal("Trancamento de matrícula");
        entityManager.persist(trancamento);
        salvar("trancar,trancamento", true, trancamento);
        entityManager.flush();
        entityManager.clear();

        RegraTriagem regra = repository.findByAtivaTrue().get(0);

        assertThat(regra.getCategoria().getNomeNoPortal()).isEqualTo("Trancamento de matrícula");
    }

    private void salvar(String palavrasChave, boolean ativa, TipoRequerimento categoria) {
        RegraTriagem regra = new RegraTriagem(palavrasChave, OrigemRegra.MANUAL);
        regra.setAtiva(ativa);
        regra.setCategoria(categoria);
        entityManager.persist(regra);
    }
}
