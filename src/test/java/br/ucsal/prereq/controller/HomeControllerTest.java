package br.ucsal.prereq.controller;

import static org.hamcrest.Matchers.containsString;
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
    void deveAbrirInicioQuandoNaoEstiverLogado() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"));
    }

    @Test
    void deveExibirLinkDaTriagemQuandoAcessarInicio() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(content().string(containsString("href=\"/triagem\"")));
    }

    @Test
    void deveUsarCssLocalQuandoAcessarInicio() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(content().string(containsString("/css/pico.min.css")));
    }

    @Test
    void deveExibirMensagemDeSucessoQuandoHouverFlash() throws Exception {
        mockMvc.perform(get("/").flashAttr("sucesso", "Categoria salva."))
                .andExpect(content().string(containsString("Categoria salva.")));
    }

    @Test
    void deveExibirMensagemDeErroQuandoHouverFlash() throws Exception {
        mockMvc.perform(get("/").flashAttr("erro", "Categoria não encontrada."))
                .andExpect(content().string(containsString("Categoria não encontrada.")));
    }
}
