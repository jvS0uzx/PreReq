package br.ucsal.prereq.service.prazo;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.YearMonth;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.context.annotation.Import;

import br.ucsal.prereq.dto.ConfirmacaoPrazoForm;
import br.ucsal.prereq.dto.EtapaPrazoForm;
import br.ucsal.prereq.model.GrupoRequerimento;
import br.ucsal.prereq.model.RegistroPrazo;
import br.ucsal.prereq.model.ResultadoPrazo;
import br.ucsal.prereq.model.Setor;
import br.ucsal.prereq.model.TipoRequerimento;
import br.ucsal.prereq.repository.RegistroPrazoRepository;

@DataJpaTest
@Import(RegistroPrazoService.class)
class RegistroPrazoIntegracaoTest {

    @Autowired
    private RegistroPrazoService service;

    @Autowired
    private RegistroPrazoRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void deveGravarEtapasNaOrdemELigarSetorPeloInicioDoNomeQuandoConfirmar() {
        entityManager.persist(new Setor("Coordenação do curso", null));
        entityManager.persist(new Setor("Financeiro", null));
        TipoRequerimento categoria = TipoRequerimento.novo(GrupoRequerimento.ACADEMICO);
        categoria.setNomeNoPortal("CATEGORIA DO TESTE DE PRAZO");
        entityManager.persist(categoria);

        ConfirmacaoPrazoForm form = new ConfirmacaoPrazoForm();
        form.setCategoriaId(categoria.getId());
        form.setResultado(ResultadoPrazo.DEFERIDO);
        form.setTotalDias(12);
        form.setMesConclusao("2026-09");
        form.setEtapas(List.of(etapa("COORDENAÇÃO", 2), etapa("COES", 4), etapa("FINANCEIRO", 6)));

        Long id = service.confirmar(form).getId();
        entityManager.flush();
        entityManager.clear();

        RegistroPrazo salvo = repository.findById(id).orElseThrow();
        assertThat(salvo.getMesConclusao()).isEqualTo(YearMonth.of(2026, 9));
        assertThat(salvo.getEtapas()).hasSize(3);
        assertThat(salvo.getEtapas().get(0).getSetor().getNome()).isEqualTo("Coordenação do curso");
        assertThat(salvo.getEtapas().get(1).getSetor()).isNull();
        assertThat(salvo.getEtapas().get(1).getSetorNaoCadastrado()).isEqualTo("COES");
        assertThat(salvo.getEtapas().get(2).getSetor().getNome()).isEqualTo("Financeiro");
        assertThat(salvo.getEtapas().get(2).getDias()).isEqualTo(6);
    }

    private EtapaPrazoForm etapa(String setor, int dias) {
        EtapaPrazoForm etapa = new EtapaPrazoForm();
        etapa.setSetor(setor);
        etapa.setDias(dias);
        return etapa;
    }
}
