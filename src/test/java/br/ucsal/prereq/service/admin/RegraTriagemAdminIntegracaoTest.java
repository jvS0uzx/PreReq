package br.ucsal.prereq.service.admin;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

import br.ucsal.prereq.dto.RegraForm;
import br.ucsal.prereq.service.IndeferimentoService;
import br.ucsal.prereq.service.triagem.ResultadoTriagem;
import br.ucsal.prereq.service.triagem.TriagemService;

@DataJpaTest
@Import({ RegraTriagemAdminService.class, TriagemService.class, IndeferimentoService.class })
class RegraTriagemAdminIntegracaoTest {

    @Autowired
    private RegraTriagemAdminService service;

    @Autowired
    private TriagemService triagemService;

    @Test
    void deveMudarResultadoDaTriagemQuandoSalvarNovaRegra() {
        assertThat(triagemService.triar("preciso do setor zeta").regraAplicada()).isNull();

        RegraForm form = new RegraForm();
        form.setPalavrasChave("Setor Zeta, reclamação");
        form.setCanalAlternativo("Ouvidoria da universidade");
        service.salvar(form);

        ResultadoTriagem resultado = triagemService.triar("preciso do setor zeta");

        assertThat(resultado.regraAplicada()).isNotNull();
        assertThat(resultado.canalAlternativo()).isEqualTo("Ouvidoria da universidade");
    }

    @Test
    void deveIgnorarRegraQuandoDesativada() {
        RegraForm form = new RegraForm();
        form.setPalavrasChave("zeta");
        form.setCanalAlternativo("Ouvidoria da universidade");
        Long id = service.salvar(form).getId();

        service.desativar(id);

        assertThat(triagemService.triar("setor zeta").regraAplicada()).isNull();
    }
}
