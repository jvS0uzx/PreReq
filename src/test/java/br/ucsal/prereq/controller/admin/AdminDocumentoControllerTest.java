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
import br.ucsal.prereq.dto.DocumentoForm;
import br.ucsal.prereq.exception.RegraNegocioException;
import br.ucsal.prereq.model.Documento;
import br.ucsal.prereq.service.DocumentoService;

@WebMvcTest(AdminDocumentoController.class)
@Import(SecurityConfig.class)
class AdminDocumentoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DocumentoService service;

    @Test
    @WithMockUser
    void deveAbrirListaQuandoLogado() throws Exception {
        when(service.listar()).thenReturn(List.of(new Documento("RG", "Identidade", "Órgão emissor")));

        mockMvc.perform(get("/admin/documentos"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/documentos/lista"))
                .andExpect(model().attributeExists("documentos"));
    }

    @Test
    void deveRedirecionarParaLoginQuandoNaoEstiverLogado() throws Exception {
        mockMvc.perform(get("/admin/documentos"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    @WithMockUser
    void deveVoltarAoFormComErroQuandoNomeVazio() throws Exception {
        mockMvc.perform(post("/admin/documentos").with(csrf()).param("nome", " "))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/documentos/form"))
                .andExpect(model().attributeHasFieldErrors("form", "nome"));
    }

    @Test
    @WithMockUser
    void deveVoltarAoFormComErroNoNomeQuandoNomeDuplicado() throws Exception {
        when(service.salvar(any(DocumentoForm.class)))
                .thenThrow(new RegraNegocioException("Já existe um documento com esse nome."));

        mockMvc.perform(post("/admin/documentos").with(csrf()).param("nome", "RG"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/documentos/form"))
                .andExpect(model().attributeHasFieldErrors("form", "nome"));
    }

    @Test
    @WithMockUser
    void deveRedirecionarComSucessoQuandoSalvar() throws Exception {
        mockMvc.perform(post("/admin/documentos").with(csrf()).param("nome", "RG"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/documentos"))
                .andExpect(flash().attribute("sucesso", "Documento salvo."));
    }

    @Test
    @WithMockUser
    void deveRedirecionarComErroQuandoExcluirDocumentoEmUso() throws Exception {
        doThrow(new RegraNegocioException("Este documento é usado pela categoria X."))
                .when(service).excluir(1L);

        mockMvc.perform(post("/admin/documentos/1/excluir").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/documentos"))
                .andExpect(flash().attribute("erro", "Este documento é usado pela categoria X."));
    }
}
