package br.ucsal.prereq.controller.admin;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
import br.ucsal.prereq.dto.CategoriaForm;
import br.ucsal.prereq.exception.RegraNegocioException;
import br.ucsal.prereq.model.GrupoRequerimento;
import br.ucsal.prereq.model.TipoRequerimento;
import br.ucsal.prereq.service.admin.CategoriaAdminService;

@WebMvcTest(AdminCategoriaController.class)
@Import(SecurityConfig.class)
class AdminCategoriaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CategoriaAdminService service;

    @Test
    @WithMockUser
    void deveAbrirListaQuandoLogado() throws Exception {
        TipoRequerimento categoria = TipoRequerimento.novo(GrupoRequerimento.ACADEMICO);
        categoria.setNomeNoPortal("Trancamento");
        when(service.listar()).thenReturn(List.of(categoria));

        mockMvc.perform(get("/admin/categorias"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/categorias/lista"))
                .andExpect(model().attributeExists("categorias"));
    }

    @Test
    void deveRedirecionarParaLoginQuandoNaoEstiverLogado() throws Exception {
        mockMvc.perform(get("/admin/categorias"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    @WithMockUser
    void deveAbrirFormVazioQuandoNovo() throws Exception {
        when(service.novoForm()).thenReturn(new CategoriaForm());

        mockMvc.perform(get("/admin/categorias/novo"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/categorias/form"))
                .andExpect(model().attributeExists("form", "grupos", "todosSetores", "todosDocumentos"));
    }

    @Test
    @WithMockUser
    void deveVoltarAoFormComErroQuandoNomeVazio() throws Exception {
        mockMvc.perform(post("/admin/categorias").with(csrf())
                        .param("grupo", "ACADEMICO").param("nomeNoPortal", " "))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/categorias/form"))
                .andExpect(model().attributeHasFieldErrors("form", "nomeNoPortal"));
    }

    @Test
    @WithMockUser
    void deveVoltarAoFormComErroNoGrupoQuandoNovaSemGrupo() throws Exception {
        mockMvc.perform(post("/admin/categorias").with(csrf()).param("nomeNoPortal", "Trancamento"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/categorias/form"))
                .andExpect(model().attributeHasFieldErrors("form", "grupo"));
    }

    @Test
    @WithMockUser
    void deveVoltarAoFormComErroNoNomeQuandoNomeDuplicado() throws Exception {
        when(service.salvar(any(CategoriaForm.class)))
                .thenThrow(new RegraNegocioException("Já existe uma categoria com esse nome no portal."));

        mockMvc.perform(post("/admin/categorias").with(csrf())
                        .param("grupo", "ACADEMICO").param("nomeNoPortal", "Trancamento"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/categorias/form"))
                .andExpect(model().attributeHasFieldErrors("form", "nomeNoPortal"));
    }

    @Test
    @WithMockUser
    void deveVoltarAoFormComErroQuandoDoisSetoresTiveremAMesmaOrdem() throws Exception {
        mockMvc.perform(post("/admin/categorias").with(csrf())
                        .param("grupo", "ACADEMICO").param("nomeNoPortal", "Trancamento")
                        .param("setores[0].setorId", "1").param("setores[0].ordem", "1")
                        .param("setores[1].setorId", "2").param("setores[1].ordem", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/categorias/form"))
                .andExpect(model().attributeHasFieldErrors("form", "ordensDistintas"));
        verify(service, never()).salvar(any());
    }

    @Test
    @WithMockUser
    void deveRedirecionarComSucessoQuandoSalvar() throws Exception {
        mockMvc.perform(post("/admin/categorias").with(csrf())
                        .param("grupo", "ACADEMICO").param("nomeNoPortal", "Trancamento")
                        .param("setores[0].setorId", "1").param("setores[0].ordem", "2"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/categorias"))
                .andExpect(flash().attribute("sucesso", "Categoria salva."));
    }

    @Test
    @WithMockUser
    void deveRedirecionarComSucessoQuandoDesativar() throws Exception {
        mockMvc.perform(post("/admin/categorias/1/desativar").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/categorias"))
                .andExpect(flash().attribute("sucesso", "Categoria desativada."));
        verify(service).desativar(1L);
    }
}
