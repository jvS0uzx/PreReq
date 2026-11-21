package br.ucsal.prereq.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.YearMonth;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import br.ucsal.prereq.model.EtapaPrazo;
import br.ucsal.prereq.model.GrupoRequerimento;
import br.ucsal.prereq.model.RegistroPrazo;
import br.ucsal.prereq.model.ResultadoPrazo;
import br.ucsal.prereq.model.Setor;
import br.ucsal.prereq.model.TipoRequerimento;

@DataJpaTest
class RegistroPrazoRepositoryTest {

    @Autowired
    private RegistroPrazoRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void deveTrazerSoOsRegistrosDaCategoriaComEtapasNaOrdemQuandoBuscarPorCategoria() {
        Setor financeiro = entityManager.persist(new Setor("Financeiro do teste", null));
        TipoRequerimento reembolso = categoria("REEMBOLSO DO TESTE");
        TipoRequerimento outra = categoria("OUTRA DO TESTE");
        RegistroPrazo registro = new RegistroPrazo(reembolso, ResultadoPrazo.DEFERIDO, 12, YearMonth.of(2026, 9));
        registro.adicionarEtapa(new EtapaPrazo(null, "SECRETARIA", 2, 1));
        registro.adicionarEtapa(new EtapaPrazo(financeiro, null, 10, 2));
        entityManager.persist(registro);
        entityManager.persist(new RegistroPrazo(outra, ResultadoPrazo.ENCERRADO, 3, YearMonth.of(2026, 8)));
        entityManager.flush();
        entityManager.clear();

        List<RegistroPrazo> encontrados = repository.findByCategoriaId(reembolso.getId());

        assertThat(encontrados).hasSize(1);
        assertThat(encontrados.get(0).getEtapas()).extracting(EtapaPrazo::getDias).containsExactly(2L, 10L);
        assertThat(encontrados.get(0).getEtapas().get(1).getSetor().getNome()).isEqualTo("Financeiro do teste");
    }

    @Test
    void deveOrdenarPeloNomeDaCategoriaSemRepetirRegistroQuandoListarTodos() {
        TipoRequerimento zeta = categoria("ZETA DO TESTE");
        TipoRequerimento alfa = categoria("ALFA DO TESTE");
        RegistroPrazo comEtapas = new RegistroPrazo(zeta, ResultadoPrazo.DEFERIDO, 9, YearMonth.of(2026, 9));
        comEtapas.adicionarEtapa(new EtapaPrazo(null, "SECRETARIA", 4, 1));
        comEtapas.adicionarEtapa(new EtapaPrazo(null, "COES", 5, 2));
        entityManager.persist(comEtapas);
        entityManager.persist(new RegistroPrazo(alfa, ResultadoPrazo.ENCERRADO, 3, YearMonth.of(2026, 8)));
        entityManager.flush();
        entityManager.clear();

        List<RegistroPrazo> todos = repository.findAllByOrderByCategoriaNomeNoPortalAscIdAsc();

        assertThat(todos).extracting(r -> r.getCategoria().getNomeNoPortal())
                .containsExactly("ALFA DO TESTE", "ZETA DO TESTE");
    }

    private TipoRequerimento categoria(String nome) {
        TipoRequerimento categoria = TipoRequerimento.novo(GrupoRequerimento.FINANCEIRO);
        categoria.setNomeNoPortal(nome);
        return entityManager.persist(categoria);
    }
}
