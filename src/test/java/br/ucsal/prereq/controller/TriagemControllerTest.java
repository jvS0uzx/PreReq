package br.ucsal.prereq.controller;

import br.ucsal.prereq.config.SecurityConfig;
import br.ucsal.prereq.model.GrupoRequerimento;
import br.ucsal.prereq.model.OrigemRegra;
import br.ucsal.prereq.model.RegraTriagem;
import br.ucsal.prereq.model.TipoRequerimento;
import br.ucsal.prereq.service.triagem.ResultadoTriagem;
import br.ucsal.prereq.service.triagem.TriagemService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(TriagemController.class)
@Import(SecurityConfig.class)
class TriagemControllerTest {

    private static final String DESCRICAO = "quero trancar uma disciplina";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TriagemService service;

    @Test
    void deveExibirFormularioQuandoAcessarTriagem() throws Exception {
        mockMvc.perform(get("/triagem"))
                .andExpect(status().isOk())
                .andExpect(view().name("triagem/formulario"))
                .andExpect(model().attributeExists("triagemForm"));
    }

    @Test
    void devePreencherTextareaQuandoReceberDescricaoPorParametro() throws Exception {
        mockMvc.perform(get("/triagem").param("descricao", "Preciso encerrar meu estágio"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Preciso encerrar meu estágio")));
    }

    @Test
    void deveVoltarComErroQuandoFormularioVazio() throws Exception {
        mockMvc.perform(post("/triagem").with(csrf()).param("descricao", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("triagem/formulario"))
                .andExpect(model().attributeHasFieldErrors("triagemForm", "descricao"));

        verifyNoInteractions(service);
    }

    @Test
    void deveVoltarComErroQuandoDescricaoCurta() throws Exception {
        mockMvc.perform(post("/triagem").with(csrf()).param("descricao", "curta"))
                .andExpect(view().name("triagem/formulario"))
                .andExpect(model().attributeHasFieldErrors("triagemForm", "descricao"));
    }

    @Test
    void deveMostrarCategoriaQuandoFormularioValido() throws Exception {
        TipoRequerimento categoria = TipoRequerimento.novo(GrupoRequerimento.ACADEMICO);
        categoria.setNomeNoPortal("TRANCAMENTO DE MATRÍCULA EM DISCIPLINA");
        ReflectionTestUtils.setField(categoria, "id", 7L);
        RegraTriagem regra = new RegraTriagem("trancar", OrigemRegra.MANUAL);
        regra.setDataRevisao(LocalDate.of(2026, 11, 4));
        when(service.triar(DESCRICAO)).thenReturn(
                new ResultadoTriagem(categoria, null, regra, "Para trancar uma disciplina", true));

        mockMvc.perform(post("/triagem").with(csrf()).param("descricao", DESCRICAO))
                .andExpect(status().isOk())
                .andExpect(view().name("triagem/resultado"))
                .andExpect(content().string(containsString("TRANCAMENTO DE MATRÍCULA EM DISCIPLINA")))
                .andExpect(content().string(containsString("Para trancar uma disciplina")))
                .andExpect(content().string(containsString("04/11/2026")))
                .andExpect(content().string(containsString("/categorias/7")));
    }

    @Test
    void deveMostrarCanalAlternativoQuandoNaoHouverCategoria() throws Exception {
        RegraTriagem regra = new RegraTriagem("reclamacao", OrigemRegra.MANUAL);
        when(service.triar("quero fazer uma reclamação")).thenReturn(
                new ResultadoTriagem(null, "Ouvidoria", regra, "Reclamações vão para a Ouvidoria", false));

        mockMvc.perform(post("/triagem").with(csrf()).param("descricao", "quero fazer uma reclamação"))
                .andExpect(status().isOk())
                .andExpect(view().name("triagem/resultado"))
                .andExpect(content().string(containsString("Ouvidoria")))
                .andExpect(content().string(org.hamcrest.Matchers.not(containsString("Ver checklist"))));
    }

    @Test
    void deveOferecerLinkParaDescreverDeOutroJeitoQuandoMostrarResultado() throws Exception {
        when(service.triar(DESCRICAO)).thenReturn(
                new ResultadoTriagem(null, null, null, "Não identificamos a categoria.", false));

        mockMvc.perform(post("/triagem").with(csrf()).param("descricao", DESCRICAO))
                .andExpect(content().string(containsString("Descreva de outro jeito")))
                .andExpect(content().string(containsString("/triagem?descricao=")));
    }
}
