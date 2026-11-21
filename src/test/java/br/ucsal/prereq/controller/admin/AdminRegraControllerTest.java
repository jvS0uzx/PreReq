package br.ucsal.prereq.controller.admin;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import br.ucsal.prereq.config.SecurityConfig;
import br.ucsal.prereq.dto.RegraForm;
import br.ucsal.prereq.exception.RegraNegocioException;
import br.ucsal.prereq.model.OrigemRegra;
import br.ucsal.prereq.model.RegraTriagem;
import br.ucsal.prereq.service.IndeferimentoService;
import br.ucsal.prereq.service.admin.RegraTriagemAdminService;
import br.ucsal.prereq.service.triagem.ResultadoTriagem;
import br.ucsal.prereq.service.triagem.TriagemService;

@WebMvcTest(AdminRegraController.class)
@Import(SecurityConfig.class)
class AdminRegraControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RegraTriagemAdminService service;

    @MockitoBean
    private TriagemService triagemService;

    @MockitoBean
    private IndeferimentoService indeferimentoService;

    @Test
    @WithMockUser
    void deveAbrirListaQuandoLogado() throws Exception {
        RegraTriagem regra = new RegraTriagem("trancar", OrigemRegra.MANUAL);
        regra.setCanalAlternativo("Ouvidoria");
        when(service.listar(false)).thenReturn(List.of(regra));

        mockMvc.perform(get("/admin/regras"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/regras/lista"))
                .andExpect(model().attribute("inativas", false))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Ouvidoria")));
    }

    @Test
    @WithMockUser
    void deveListarSomenteInativasQuandoParametroInformado() throws Exception {
        when(service.listar(true)).thenReturn(List.of());

        mockMvc.perform(get("/admin/regras").param("inativas", "true"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("inativas", true));
        verify(service).listar(true);
    }

    @Test
    void deveRedirecionarParaLoginQuandoNaoEstiverLogado() throws Exception {
        mockMvc.perform(get("/admin/regras"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    @WithMockUser
    void deveAbrirFormVazioQuandoNovo() throws Exception {
        when(service.novoForm()).thenReturn(new RegraForm());

        mockMvc.perform(get("/admin/regras/novo"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/regras/form"))
                .andExpect(model().attributeExists("form", "categorias"));
    }

    @Test
    @WithMockUser
    void deveAbrirFormComOrigemIndeferimentoQuandoVierDeIndeferimento() throws Exception {
        when(service.novoForm()).thenReturn(new RegraForm());

        mockMvc.perform(get("/admin/regras/novo").param("indeferimento", "5"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("form", org.hamcrest.Matchers.hasProperty("origem",
                        org.hamcrest.Matchers.is(OrigemRegra.INDEFERIMENTO))))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("indeferimento #5")));
    }

    @Test
    @WithMockUser
    void deveMostrarORelatoDoAlunoQuandoRegraVierDeIndeferimento() throws Exception {
        br.ucsal.prereq.model.TipoRequerimento escolhida = br.ucsal.prereq.model.TipoRequerimento
                .novo(br.ucsal.prereq.model.GrupoRequerimento.ESTAGIO);
        escolhida.setNomeNoPortal("SOLICITAÇÃO DE DISPENSA DE ESTÁGIO CURRICULAR - DIREITO");
        when(service.novoForm()).thenReturn(new RegraForm());
        when(indeferimentoService.buscar(5L)).thenReturn(new br.ucsal.prereq.model.Indeferimento(escolhida,
                "precisava encerrar meu estágio", "campo equivocado", java.time.LocalDate.of(2026, 11, 20)));

        mockMvc.perform(get("/admin/regras/novo").param("indeferimento", "5"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("precisava encerrar meu estágio")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("campo equivocado")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("DISPENSA DE ESTÁGIO")));
    }

    @Test
    @WithMockUser
    void deveVoltarAoFormComErroQuandoPalavrasChaveVazias() throws Exception {
        mockMvc.perform(post("/admin/regras").with(csrf()).param("palavrasChave", " "))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/regras/form"))
                .andExpect(model().attributeHasFieldErrors("form", "palavrasChave"));
    }

    @Test
    @WithMockUser
    void deveVoltarAoFormComErroQuandoPrioridadeVazia() throws Exception {
        mockMvc.perform(post("/admin/regras").with(csrf())
                        .param("palavrasChave", "trancar").param("canalAlternativo", "Ouvidoria")
                        .param("prioridade", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/regras/form"))
                .andExpect(model().attributeHasFieldErrorCode("form", "prioridade", "NotNull"));
        verify(service, never()).salvar(any());
    }

    @Test
    @WithMockUser
    void deveVoltarAoFormComMensagemQuandoCategoriaECanalJuntos() throws Exception {
        when(service.salvar(any(RegraForm.class)))
                .thenThrow(new RegraNegocioException("Informe a categoria ou o canal alternativo, não os dois."));

        mockMvc.perform(post("/admin/regras").with(csrf())
                        .param("palavrasChave", "trancar").param("categoriaId", "1")
                        .param("canalAlternativo", "Ouvidoria"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/regras/form"))
                .andExpect(model().attributeHasErrors("form"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("não os dois")));
    }

    @Test
    @WithMockUser
    void deveRedirecionarComSucessoQuandoSalvar() throws Exception {
        mockMvc.perform(post("/admin/regras").with(csrf())
                        .param("palavrasChave", "trancar").param("canalAlternativo", "Ouvidoria"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/regras"))
                .andExpect(flash().attribute("sucesso", "Regra salva."));
    }

    @Test
    @WithMockUser
    void deveRedirecionarComSucessoQuandoDesativar() throws Exception {
        mockMvc.perform(post("/admin/regras/1/desativar").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/regras"))
                .andExpect(flash().attribute("sucesso", "Regra desativada."));
        verify(service).desativar(1L);
    }

    @Test
    @WithMockUser
    void deveMostrarResultadoQuandoTestarFrase() throws Exception {
        RegraTriagem regra = new RegraTriagem("ouvidoria", OrigemRegra.MANUAL);
        when(triagemService.triar("falar com a ouvidoria"))
                .thenReturn(new ResultadoTriagem(null, "Canal da Ouvidoria", regra, "Procure o canal indicado.", false));

        mockMvc.perform(post("/admin/regras/testar").with(csrf()).param("frase", "falar com a ouvidoria"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/regras/lista"))
                .andExpect(model().attributeExists("resultado"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Canal da Ouvidoria")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Procure o canal indicado.")));
    }

    @Test
    @WithMockUser
    void deveMostrarMensagemPadraoQuandoTestarSemRegra() throws Exception {
        when(triagemService.triar("xyz"))
                .thenReturn(new ResultadoTriagem(null, null, null, "Não identificamos a categoria.", false));

        mockMvc.perform(post("/admin/regras/testar").with(csrf()).param("frase", "xyz"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Não identificamos a categoria.")));
    }
}
