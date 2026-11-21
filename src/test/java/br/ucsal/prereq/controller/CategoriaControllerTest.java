package br.ucsal.prereq.controller;

import br.ucsal.prereq.config.SecurityConfig;
import br.ucsal.prereq.exception.RecursoNaoEncontradoException;
import br.ucsal.prereq.model.GrupoRequerimento;
import br.ucsal.prereq.model.TipoRequerimento;
import br.ucsal.prereq.service.CategoriaService;
import br.ucsal.prereq.service.prazo.CalculoPrazo;
import br.ucsal.prereq.service.prazo.PrazoReferencia;
import br.ucsal.prereq.service.prazo.PrazoReferenciaService;
import br.ucsal.prereq.service.prazo.PrazoSetor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

    @MockitoBean
    private PrazoReferenciaService prazoService;

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
    void deveAvisarQueFaltamDadosDePrazoQuandoCategoriaTiverPoucosRegistros() throws Exception {
        when(service.buscar(1L)).thenReturn(categoria("HISTÓRICO ESCOLAR - GRADUAÇÃO"));
        when(prazoService.porCategoria(eq(1L), any(CalculoPrazo.class))).thenReturn(Optional.empty());

        mockMvc.perform(get("/categorias/1"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Ainda não temos dados suficientes para esta categoria.")))
                .andExpect(content().string(containsString("/prazos/contribuir")));
    }

    @Test
    void deveMostrarMedianaEP75PorSetorQuandoCategoriaTiverPrazoDeReferencia() throws Exception {
        when(service.buscar(1L)).thenReturn(categoria("HISTÓRICO ESCOLAR - GRADUAÇÃO"));
        when(prazoService.porCategoria(eq(1L), any(CalculoPrazo.class))).thenAnswer(chamada -> {
            CalculoPrazo calculo = chamada.getArgument(1);
            boolean mediana = calculo.nome().equals("Mediana");
            return Optional.of(new PrazoReferencia(calculo.nome(), mediana ? 20.0 : 31.5,
                    List.of(new PrazoSetor("Financeiro", mediana ? 12.0 : 18.4, 3)), 3));
        });

        mockMvc.perform(get("/categorias/1"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("20,0")))
                .andExpect(content().string(containsString("31,5")))
                .andExpect(content().string(containsString("Financeiro")))
                .andExpect(content().string(containsString("12,0")))
                .andExpect(content().string(containsString("18,4")));
    }

    @Test
    void deveRetornar404QuandoCategoriaNaoExistir() throws Exception {
        when(service.buscar(999999L)).thenThrow(new RecursoNaoEncontradoException("Categoria não encontrada"));

        mockMvc.perform(get("/categorias/999999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveRetornarTextoGeradoQuandoFormularioForValido() throws Exception {
        when(service.buscar(1L)).thenReturn(categoria("HISTÓRICO ESCOLAR - GRADUAÇÃO"));

        mockMvc.perform(post("/categorias/1/modelo")
                        .with(csrf())
                        .param("curso", "Engenharia de Software")
                        .param("semestre", "2026.2")
                        .param("motivo", "Preciso para o estágio"))
                .andExpect(status().isOk())
                .andExpect(view().name("categorias/detalhe"))
                .andExpect(model().attributeExists("textoGerado"))
                .andExpect(content().string(containsString("Preciso para o estágio")));
    }

    @Test
    void deveRetornarErrosDeValidacaoQuandoFormularioForInvalido() throws Exception {
        when(service.buscar(1L)).thenReturn(categoria("HISTÓRICO ESCOLAR - GRADUAÇÃO"));

        mockMvc.perform(post("/categorias/1/modelo")
                        .with(csrf())
                        .param("curso", "")
                        .param("semestre", "2026.2")
                        .param("motivo", "Teste"))
                .andExpect(status().isOk())
                .andExpect(view().name("categorias/detalhe"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrors("dadosSolicitacao", "curso"))
                .andExpect(model().attributeDoesNotExist("textoGerado"));
    }

    private TipoRequerimento categoria(String nomeNoPortal) {
        TipoRequerimento categoria = TipoRequerimento.novo(GrupoRequerimento.DOCUMENTAL);
        categoria.setNomeNoPortal(nomeNoPortal);
        categoria.setDataRevisao(LocalDate.of(2026, 11, 4));
        return categoria;
    }
}
