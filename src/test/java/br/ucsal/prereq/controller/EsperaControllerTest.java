package br.ucsal.prereq.controller;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import br.ucsal.prereq.config.SecurityConfig;
import br.ucsal.prereq.model.GrupoRequerimento;
import br.ucsal.prereq.model.Setor;
import br.ucsal.prereq.model.TipoRequerimento;
import br.ucsal.prereq.service.CategoriaService;
import br.ucsal.prereq.service.SetorService;
import br.ucsal.prereq.service.espera.Alerta;
import br.ucsal.prereq.service.espera.EsperaService;
import br.ucsal.prereq.service.espera.NivelAlerta;
import br.ucsal.prereq.service.espera.ResultadoEspera;
import br.ucsal.prereq.service.espera.SituacaoEspera;
import br.ucsal.prereq.service.prazo.PrazoReferencia;

@WebMvcTest(EsperaController.class)
@Import(SecurityConfig.class)
class EsperaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EsperaService service;

    @MockitoBean
    private CategoriaService categoriaService;

    @MockitoBean
    private SetorService setorService;

    private TipoRequerimento reembolso;
    private Setor financeiro;

    @BeforeEach
    void prepararListas() {
        reembolso = TipoRequerimento.novo(GrupoRequerimento.FINANCEIRO);
        reembolso.setNomeNoPortal("REEMBOLSO TODOS OS NIVEIS");
        ReflectionTestUtils.setField(reembolso, "id", 2L);
        financeiro = new Setor("Financeiro", null);
        ReflectionTestUtils.setField(financeiro, "id", 7L);
        when(categoriaService.listarAtivasPorGrupo())
                .thenReturn(Map.of(GrupoRequerimento.FINANCEIRO, List.of(reembolso)));
        when(setorService.listar()).thenReturn(List.of(financeiro));
    }

    @Test
    void deveExibirFormularioComCategoriasESetoresQuandoAcessarEspera() throws Exception {
        mockMvc.perform(get("/espera"))
                .andExpect(status().isOk())
                .andExpect(view().name("espera/formulario"))
                .andExpect(content().string(containsString("REEMBOLSO TODOS OS NIVEIS")))
                .andExpect(content().string(containsString("Financeiro")))
                .andExpect(content().string(containsString("Nada do que você informar aqui é guardado.")));
    }

    @Test
    void deveVoltarComErrosQuandoFaltarCategoriaEData() throws Exception {
        mockMvc.perform(post("/espera").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("espera/formulario"))
                .andExpect(model().attributeHasFieldErrors("esperaForm", "categoriaId", "dataAbertura"));

        verifyNoInteractions(service);
    }

    @Test
    void deveVoltarComErroQuandoDataDeAberturaEstiverNoFuturo() throws Exception {
        mockMvc.perform(post("/espera").with(csrf())
                        .param("categoriaId", "2")
                        .param("dataAbertura", LocalDate.now().plusDays(3).toString()))
                .andExpect(view().name("espera/formulario"))
                .andExpect(model().attributeHasFieldErrors("esperaForm", "dataAbertura"))
                .andExpect(content().string(containsString("A data de abertura não pode estar no futuro.")));

        verifyNoInteractions(service);
    }

    @Test
    void deveMostrarAlertasQuandoEsperaPassarDoUsual() throws Exception {
        SituacaoEspera situacao = new SituacaoEspera(reembolso, LocalDate.of(2026, 10, 1), Optional.of(financeiro),
                45, Optional.of(new PrazoReferencia("P75", 32.5, List.of(), 4)));
        when(service.verificar(any())).thenReturn(new ResultadoEspera(situacao, List.of(
                new Alerta(NivelAlerta.ATENCAO, "Sua espera passou do usual; procure a Ouvidoria."),
                new Alerta(NivelAlerta.INFO, "Seu pedido está no Financeiro, que recalcula as parcelas."))));

        mockMvc.perform(post("/espera").with(csrf())
                        .param("categoriaId", "2").param("dataAbertura", "2026-10-01").param("setorId", "7"))
                .andExpect(status().isOk())
                .andExpect(view().name("espera/resultado"))
                .andExpect(content().string(containsString("REEMBOLSO TODOS OS NIVEIS")))
                .andExpect(content().string(containsString("01/10/2026")))
                .andExpect(content().string(containsString("32,5")))
                .andExpect(content().string(containsString("Sua espera passou do usual; procure a Ouvidoria.")))
                .andExpect(content().string(containsString("aviso aviso-atencao")))
                .andExpect(content().string(containsString("recalcula as parcelas")))
                .andExpect(content().string(not(containsString("dentro do usual"))));
    }

    @Test
    void deveDizerQueEstaDentroDoUsualQuandoNaoHouverAlerta() throws Exception {
        SituacaoEspera situacao = new SituacaoEspera(reembolso, LocalDate.of(2026, 11, 10), Optional.empty(),
                11, Optional.of(new PrazoReferencia("P75", 32.5, List.of(), 4)));
        when(service.verificar(any())).thenReturn(new ResultadoEspera(situacao, List.of()));

        mockMvc.perform(post("/espera").with(csrf())
                        .param("categoriaId", "2").param("dataAbertura", "2026-11-10"))
                .andExpect(view().name("espera/resultado"))
                .andExpect(content().string(containsString("Sua espera está dentro do usual para esta categoria.")))
                .andExpect(content().string(not(containsString("Setor atual"))));
    }
}
