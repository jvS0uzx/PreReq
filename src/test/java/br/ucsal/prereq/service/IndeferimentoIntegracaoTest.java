package br.ucsal.prereq.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.context.annotation.Import;

import br.ucsal.prereq.dto.IndeferimentoForm;
import br.ucsal.prereq.dto.RegraForm;
import br.ucsal.prereq.exception.RegraNegocioException;
import br.ucsal.prereq.model.GrupoRequerimento;
import br.ucsal.prereq.model.Indeferimento;
import br.ucsal.prereq.model.OrigemRegra;
import br.ucsal.prereq.model.StatusIndeferimento;
import br.ucsal.prereq.model.TipoRequerimento;
import br.ucsal.prereq.service.admin.RegraTriagemAdminService;
import br.ucsal.prereq.service.triagem.ResultadoTriagem;
import br.ucsal.prereq.service.triagem.TriagemService;

@DataJpaTest
@Import({ IndeferimentoService.class, RegraTriagemAdminService.class, TriagemService.class })
class IndeferimentoIntegracaoTest {

    private static final String RELATO = "precisava dar baixa no estagio zeta";

    @Autowired
    private IndeferimentoService service;

    @Autowired
    private RegraTriagemAdminService regraService;

    @Autowired
    private TriagemService triagemService;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void deveResponderAFraseDoRelatoComACategoriaCorretaQuandoIndeferimentoVirarRegra() {
        TipoRequerimento escolhida = categoria("CATEGORIA ERRADA DO TESTE");
        TipoRequerimento correta = categoria("CATEGORIA CERTA DO TESTE");
        Long id = service.registrar(relato(escolhida)).getId();
        assertThat(triagemService.triar(RELATO).categoria()).isNull();

        RegraForm regra = new RegraForm();
        regra.setPalavrasChave("baixa no estagio zeta");
        regra.setCategoriaId(correta.getId());
        regra.setOrigem(OrigemRegra.INDEFERIMENTO);
        regra.setIndeferimentoId(id);
        regraService.salvar(regra);
        entityManager.flush();
        entityManager.clear();

        ResultadoTriagem resultado = triagemService.triar(RELATO);
        Indeferimento convertido = service.buscar(id);
        assertThat(resultado.categoria().getNomeNoPortal()).isEqualTo("CATEGORIA CERTA DO TESTE");
        assertThat(resultado.regraAplicada().getOrigem()).isEqualTo(OrigemRegra.INDEFERIMENTO);
        assertThat(convertido.getStatus()).isEqualTo(StatusIndeferimento.CONVERTIDO);
        assertThat(convertido.getCategoriaCorreta().getNomeNoPortal()).isEqualTo("CATEGORIA CERTA DO TESTE");
        assertThat(service.listarPendentes()).isEmpty();
    }

    @Test
    void deveRecusarSegundaRegraQuandoIndeferimentoJaFoiDescartado() {
        TipoRequerimento escolhida = categoria("CATEGORIA ERRADA DO TESTE");
        Long id = service.registrar(relato(escolhida)).getId();
        service.descartar(id);

        RegraForm regra = new RegraForm();
        regra.setPalavrasChave("baixa no estagio zeta");
        regra.setCanalAlternativo("Coordenação do curso");
        regra.setIndeferimentoId(id);

        assertThatThrownBy(() -> regraService.salvar(regra)).isInstanceOf(RegraNegocioException.class);
        assertThat(triagemService.triar(RELATO).regraAplicada()).isNull();
    }

    private IndeferimentoForm relato(TipoRequerimento escolhida) {
        IndeferimentoForm form = new IndeferimentoForm();
        form.setCategoriaEscolhidaId(escolhida.getId());
        form.setNecessidade(RELATO);
        form.setMotivoInformado("campo equivocado");
        return form;
    }

    private TipoRequerimento categoria(String nome) {
        TipoRequerimento categoria = TipoRequerimento.novo(GrupoRequerimento.ESTAGIO);
        categoria.setNomeNoPortal(nome);
        return entityManager.persist(categoria);
    }
}
