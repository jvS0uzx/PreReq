package br.ucsal.prereq.controller.admin;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
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
import br.ucsal.prereq.dto.SetorForm;
import br.ucsal.prereq.exception.RegraNegocioException;
import br.ucsal.prereq.model.Setor;
import br.ucsal.prereq.service.SetorService;

@WebMvcTest(AdminSetorController.class)
@Import(SecurityConfig.class)
class AdminSetorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SetorService service;

    @Test
    @WithMockUser
    void deveAbrirListaQuandoLogado() throws Exception {
        when(service.listar()).thenReturn(List.of(new Setor("Secretaria", "Atendimento")));

        mockMvc.perform(get("/admin/setores"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/setores/lista"))
                .andExpect(model().attributeExists("setores"));
    }

    @Test
    void deveRedirecionarParaLoginQuandoNaoEstiverLogado() throws Exception {
        mockMvc.perform(get("/admin/setores"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    @WithMockUser
    void deveVoltarAoFormComErroQuandoNomeVazio() throws Exception {
        mockMvc.perform(post("/admin/setores").with(csrf()).param("nome", " "))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/setores/form"))
                .andExpect(model().attributeHasFieldErrors("form", "nome"));
    }

    @Test
    @WithMockUser
    void deveVoltarAoFormComErroNoNomeQuandoNomeDuplicado() throws Exception {
        when(service.salvar(any(SetorForm.class)))
                .thenThrow(new RegraNegocioException("Já existe um setor com esse nome."));

        mockMvc.perform(post("/admin/setores").with(csrf()).param("nome", "Secretaria"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/setores/form"))
                .andExpect(model().attributeHasFieldErrors("form", "nome"));
    }

    @Test
    @WithMockUser
    void deveRedirecionarComSucessoQuandoSalvar() throws Exception {
        mockMvc.perform(post("/admin/setores").with(csrf()).param("nome", "Secretaria"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/setores"))
                .andExpect(flash().attribute("sucesso", "Setor salvo."));
    }

    @Test
    @WithMockUser
    void deveRedirecionarComErroQuandoExcluirSetorEmUso() throws Exception {
        doThrow(new RegraNegocioException("Este setor é usado pela categoria X."))
                .when(service).excluir(1L);

        mockMvc.perform(post("/admin/setores/1/excluir").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/setores"))
                .andExpect(flash().attribute("erro", "Este setor é usado pela categoria X."));
    }
}
