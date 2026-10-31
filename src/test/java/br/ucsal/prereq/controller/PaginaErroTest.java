package br.ucsal.prereq.controller;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import jakarta.servlet.RequestDispatcher;

@SpringBootTest
@AutoConfigureMockMvc
class PaginaErroTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void deveExibirPagina404ComLayoutQuandoRotaNaoExistir() throws Exception {
        mockMvc.perform(get("/error")
                        .requestAttr(RequestDispatcher.ERROR_STATUS_CODE, 404)
                        .requestAttr(RequestDispatcher.ERROR_REQUEST_URI, "/rota-inexistente")
                        .accept(MediaType.TEXT_HTML))
                .andExpect(status().isNotFound())
                .andExpect(content().string(allOf(
                        containsString("Erro 404"),
                        containsString("href=\"/triagem\""))));
    }

    @Test
    void deveExibirPagina500ComLayoutQuandoOcorrerErroInterno() throws Exception {
        mockMvc.perform(get("/error")
                        .requestAttr(RequestDispatcher.ERROR_STATUS_CODE, 500)
                        .requestAttr(RequestDispatcher.ERROR_REQUEST_URI, "/qualquer")
                        .accept(MediaType.TEXT_HTML))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(allOf(
                        containsString("Erro 500"),
                        containsString("href=\"/triagem\""))));
    }
}
