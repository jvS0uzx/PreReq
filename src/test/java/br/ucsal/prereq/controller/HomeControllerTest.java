package br.ucsal.prereq.controller;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import br.ucsal.prereq.config.SecurityConfig;

@WebMvcTest(HomeController.class)
@Import(SecurityConfig.class)
class HomeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void deveExibirMenuComTodasAsRotasQuandoAcessarInicio() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(content().string(allOf(
                        containsString("href=\"/triagem\""),
                        containsString("href=\"/categorias\""),
                        containsString("href=\"/prazos\""),
                        containsString("href=\"/espera\""),
                        containsString("href=\"/indeferimentos/novo\""),
                        containsString("href=\"/admin\""))));
    }

    @Test
    void deveCarregarCssLocalQuandoAcessarInicio() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(content().string(allOf(
                        containsString("href=\"/css/pico.min.css\""),
                        not(containsString("https://")))));
    }

    @Test
    void deveExibirAvisoDeSucessoQuandoHouverFlashSucesso() throws Exception {
        mockMvc.perform(get("/").flashAttr("sucesso", "Categoria salva."))
                .andExpect(content().string(containsString("class=\"aviso aviso-sucesso\" role=\"status\">Categoria salva.</p>")));
    }

    @Test
    void deveExibirAvisoDeErroQuandoHouverFlashErro() throws Exception {
        mockMvc.perform(get("/").flashAttr("erro", "Categoria não encontrada."))
                .andExpect(content().string(containsString("class=\"aviso aviso-erro\" role=\"alert\">Categoria não encontrada.</p>")));
    }

    @Test
    void deveOcultarAvisosQuandoNaoHouverFlash() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(content().string(not(containsString("class=\"aviso"))));
    }
}
