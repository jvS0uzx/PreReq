package br.ucsal.prereq.controller;

import br.ucsal.prereq.exception.RecursoNaoEncontradoException;
import br.ucsal.prereq.service.CategoriaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(CategoriaController.class)
@Import(CategoriaControllerTest.Config.class)
class CategoriaControllerTest {

    static class Config {
        @Bean
        public CategoriaService categoriaService() {
            return mock(CategoriaService.class);
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CategoriaService service;

    @Test
    void deveCarregarPaginaDeLista() throws Exception {
        when(service.listarAtivasPorGrupo()).thenReturn(Collections.emptyMap());

        mockMvc.perform(get("/categorias"))
                .andExpect(status().isOk())
                .andExpect(view().name("categorias/lista"))
                .andExpect(model().attributeExists("categoriasAgrupadas"));
    }

    @Test
    void deveRetornarErro404ParaCategoriaInexistente() throws Exception {
        when(service.buscar(999999L)).thenThrow(new RecursoNaoEncontradoException("Categoria não encontrada"));

        mockMvc.perform(get("/categorias/999999"))
                .andExpect(status().isNotFound());
    }
}
