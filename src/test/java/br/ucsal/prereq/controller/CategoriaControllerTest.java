package br.ucsal.prereq.controller;

import br.ucsal.prereq.config.SecurityConfig;
import br.ucsal.prereq.exception.RecursoNaoEncontradoException;
import br.ucsal.prereq.model.GrupoRequerimento;
import br.ucsal.prereq.model.TipoRequerimento;
import br.ucsal.prereq.service.CategoriaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(CategoriaController.class)
@Import(SecurityConfig.class)
class CategoriaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CategoriaService service;

    @Test
    void deveExibirListaAgrupadaQuandoNaoHouverBusca() throws Exception {
        when(service.listarAtivasPorGrupo()).thenReturn(Collections.emptyMap());

        mockMvc.perform(get("/categorias"))
                .andExpect(status().isOk())
                .andExpect(view().name("categorias/lista"))
                .andExpect(model().attributeExists("categoriasAgrupadas"));
    }

    @Test
    void deveExibirResultadoDaBuscaQuandoHouverTermo() throws Exception {
        when(service.buscarPorNome("histórico")).thenReturn(List.of(categoria("HISTÓRICO ESCOLAR - GRADUAÇÃO")));

        mockMvc.perform(get("/categorias").param("q", "histórico"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("termoBusca", "histórico"))
                .andExpect(content().string(containsString("HISTÓRICO ESCOLAR - GRADUAÇÃO")));
    }

    @Test
    void deveAvisarQueFaltamSetoresQuandoCategoriaNaoTiverSetor() throws Exception {
        when(service.buscar(1L)).thenReturn(categoria("HISTÓRICO ESCOLAR - GRADUAÇÃO"));

        mockMvc.perform(get("/categorias/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("categorias/detalhe"))
                .andExpect(content().string(containsString("Os setores desta categoria ainda não foram levantados.")));
    }

    @Test
    void deveRetornar404QuandoCategoriaNaoExistir() throws Exception {
        when(service.buscar(999999L)).thenThrow(new RecursoNaoEncontradoException("Categoria não encontrada"));

        mockMvc.perform(get("/categorias/999999"))
                .andExpect(status().isNotFound());
    }

    private TipoRequerimento categoria(String nomeNoPortal) {
        TipoRequerimento categoria = TipoRequerimento.novo(GrupoRequerimento.DOCUMENTAL);
        categoria.setNomeNoPortal(nomeNoPortal);
        categoria.setDataRevisao(LocalDate.of(2026, 11, 4));
        return categoria;
    }
}
