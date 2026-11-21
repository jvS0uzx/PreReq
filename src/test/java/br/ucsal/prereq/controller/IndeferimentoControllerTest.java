package br.ucsal.prereq.controller;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
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
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import br.ucsal.prereq.config.SecurityConfig;
import br.ucsal.prereq.dto.IndeferimentoForm;
import br.ucsal.prereq.model.GrupoRequerimento;
import br.ucsal.prereq.model.TipoRequerimento;
import br.ucsal.prereq.service.CategoriaService;
import br.ucsal.prereq.service.IndeferimentoService;

@WebMvcTest(IndeferimentoController.class)
@Import(SecurityConfig.class)
class IndeferimentoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IndeferimentoService service;

    @MockitoBean
    private CategoriaService categoriaService;

    @BeforeEach
    void prepararCategorias() {
        TipoRequerimento categoria = TipoRequerimento.novo(GrupoRequerimento.ESTAGIO);
        categoria.setNomeNoPortal("SOLICITAÇÃO DE DISPENSA DE ESTÁGIO CURRICULAR - DIREITO");
        ReflectionTestUtils.setField(categoria, "id", 4L);
        when(categoriaService.listarAtivasPorGrupo())
                .thenReturn(Map.of(GrupoRequerimento.ESTAGIO, List.of(categoria)));
    }

    @Test
    void deveExibirFormularioComAvisoDePrivacidadeQuandoAcessarNovo() throws Exception {
        mockMvc.perform(get("/indeferimentos/novo"))
                .andExpect(status().isOk())
                .andExpect(view().name("indeferimentos/novo"))
                .andExpect(content().string(containsString("Não escreva seu nome, RA ou número do protocolo")))
                .andExpect(content().string(containsString("SOLICITAÇÃO DE DISPENSA DE ESTÁGIO CURRICULAR - DIREITO")));
    }

    @Test
    void deveVoltarComErrosQuandoFormularioVazio() throws Exception {
        mockMvc.perform(post("/indeferimentos/novo").with(csrf())
                        .param("necessidade", "curto").param("motivoInformado", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("indeferimentos/novo"))
                .andExpect(model().attributeHasFieldErrors("indeferimentoForm",
                        "categoriaEscolhidaId", "necessidade", "motivoInformado"));

        verifyNoInteractions(service);
    }

    @Test
    void deveRegistrarERedirecionarQuandoFormularioValido() throws Exception {
        mockMvc.perform(post("/indeferimentos/novo").with(csrf())
                        .param("categoriaEscolhidaId", "4")
                        .param("necessidade", "precisava encerrar meu estágio")
                        .param("motivoInformado", "campo equivocado"))
                .andExpect(redirectedUrl("/indeferimentos/novo"))
                .andExpect(flash().attributeExists("sucesso"));

        ArgumentCaptor<IndeferimentoForm> form = ArgumentCaptor.forClass(IndeferimentoForm.class);
        verify(service).registrar(form.capture());
        org.assertj.core.api.Assertions.assertThat(form.getValue().getNecessidade())
                .isEqualTo("precisava encerrar meu estágio");
    }

    @Test
    void deveRecusarQuandoEnviarSemCsrf() throws Exception {
        mockMvc.perform(post("/indeferimentos/novo").param("categoriaEscolhidaId", "4"))
                .andExpect(status().isForbidden());

        verify(service, org.mockito.Mockito.never()).registrar(any());
    }
}
