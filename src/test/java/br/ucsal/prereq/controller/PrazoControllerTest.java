package br.ucsal.prereq.controller;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
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

import java.time.LocalDate;
import java.time.YearMonth;
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
import br.ucsal.prereq.dto.ConfirmacaoPrazoForm;
import br.ucsal.prereq.exception.RegraNegocioException;
import br.ucsal.prereq.model.GrupoRequerimento;
import br.ucsal.prereq.model.ResultadoPrazo;
import br.ucsal.prereq.model.TipoRequerimento;
import br.ucsal.prereq.service.CategoriaService;
import br.ucsal.prereq.service.prazo.EtapaLida;
import br.ucsal.prereq.service.prazo.PreviaPrazo;
import br.ucsal.prereq.service.prazo.RegistroPrazoService;

@WebMvcTest(PrazoController.class)
@Import(SecurityConfig.class)
class PrazoControllerTest {

    private static final String HISTORICO = "Histórico de etapas com o protocolo 998877";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RegistroPrazoService service;

    @MockitoBean
    private CategoriaService categoriaService;

    private TipoRequerimento categoria;

    @BeforeEach
    void prepararCategorias() {
        categoria = TipoRequerimento.novo(GrupoRequerimento.ACADEMICO);
        categoria.setNomeNoPortal("RENOVAÇÃO TRANCAMENTO - GRADUAÇÃO");
        ReflectionTestUtils.setField(categoria, "id", 3L);
        when(categoriaService.listarAtivasPorGrupo())
                .thenReturn(Map.of(GrupoRequerimento.ACADEMICO, List.of(categoria)));
    }

    @Test
    void deveExibirFormularioComCategoriasEAvisoQuandoAcessarContribuir() throws Exception {
        mockMvc.perform(get("/prazos/contribuir"))
                .andExpect(status().isOk())
                .andExpect(view().name("prazos/contribuir"))
                .andExpect(content().string(containsString("RENOVAÇÃO TRANCAMENTO - GRADUAÇÃO")))
                .andExpect(content().string(containsString(
                        "Guardamos só o tipo do requerimento e os dias em cada setor.")));
    }

    @Test
    void deveVoltarComErroQuandoCategoriaOuHistoricoFaltarem() throws Exception {
        mockMvc.perform(post("/prazos/contribuir").with(csrf()).param("historico", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("prazos/contribuir"))
                .andExpect(model().attributeHasFieldErrors("contribuicaoForm", "categoriaId", "historico"));

        verifyNoInteractions(service);
    }

    @Test
    void deveVoltarAoFormularioComMensagemDoParserQuandoHistoricoForInvalido() throws Exception {
        when(service.previsualizar(3L, HISTORICO)).thenThrow(
                new RegraNegocioException("Não reconhecemos o histórico. Copie o bloco inteiro."));

        mockMvc.perform(post("/prazos/contribuir").with(csrf())
                        .param("categoriaId", "3").param("historico", HISTORICO))
                .andExpect(status().isOk())
                .andExpect(view().name("prazos/contribuir"))
                .andExpect(model().attributeHasFieldErrors("contribuicaoForm", "historico"))
                .andExpect(content().string(containsString("Não reconhecemos o histórico. Copie o bloco inteiro.")));
    }

    @Test
    void deveMostrarPreviaSemOTextoColadoQuandoHistoricoForValido() throws Exception {
        when(service.previsualizar(3L, HISTORICO)).thenReturn(new PreviaPrazo(categoria,
                ResultadoPrazo.DEFERIDO, 27, YearMonth.of(2026, 9),
                List.of(new EtapaLida("REGISTRO ACADÊMICO", LocalDate.of(2026, 8, 28), LocalDate.of(2026, 8, 31), 3),
                        new EtapaLida("FINANCEIRO", LocalDate.of(2026, 8, 31), LocalDate.of(2026, 9, 24), 24))));

        mockMvc.perform(post("/prazos/contribuir").with(csrf())
                        .param("categoriaId", "3").param("historico", HISTORICO))
                .andExpect(status().isOk())
                .andExpect(view().name("prazos/previa"))
                .andExpect(content().string(containsString("REGISTRO ACADÊMICO")))
                .andExpect(content().string(containsString("name=\"etapas[1].dias\"")))
                .andExpect(content().string(containsString("value=\"2026-09\"")))
                .andExpect(content().string(containsString("09/2026")))
                .andExpect(content().string(not(containsString("998877"))));
    }

    @Test
    void deveAvisarQueSoOTotalSeraGuardadoQuandoPreviaNaoTiverSetor() throws Exception {
        when(service.previsualizar(3L, HISTORICO)).thenReturn(new PreviaPrazo(categoria,
                ResultadoPrazo.ENCERRADO, 7, YearMonth.of(2026, 7), List.of()));

        mockMvc.perform(post("/prazos/contribuir").with(csrf())
                        .param("categoriaId", "3").param("historico", HISTORICO))
                .andExpect(view().name("prazos/previa"))
                .andExpect(content().string(containsString("Nenhum setor foi identificado")));
    }

    @Test
    void deveSalvarERedirecionarQuandoConfirmar() throws Exception {
        mockMvc.perform(post("/prazos/contribuir/confirmar").with(csrf())
                        .param("categoriaId", "3")
                        .param("resultado", "DEFERIDO")
                        .param("totalDias", "27")
                        .param("mesConclusao", "2026-09")
                        .param("etapas[0].setor", "REGISTRO ACADÊMICO")
                        .param("etapas[0].dias", "3")
                        .param("etapas[1].setor", "FINANCEIRO")
                        .param("etapas[1].dias", "24"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/prazos/contribuir"))
                .andExpect(flash().attributeExists("sucesso"));

        ArgumentCaptor<ConfirmacaoPrazoForm> form = ArgumentCaptor.forClass(ConfirmacaoPrazoForm.class);
        verify(service).confirmar(form.capture());
        org.assertj.core.api.Assertions.assertThat(form.getValue().getEtapas()).hasSize(2);
        org.assertj.core.api.Assertions.assertThat(form.getValue().getEtapas().get(1).getDias()).isEqualTo(24);
    }

    @Test
    void deveRecusarSemSalvarQuandoCamposOcultosForemAdulterados() throws Exception {
        mockMvc.perform(post("/prazos/contribuir/confirmar").with(csrf())
                        .param("categoriaId", "3")
                        .param("resultado", "DEFERIDO")
                        .param("totalDias", "-4")
                        .param("mesConclusao", "setembro"))
                .andExpect(redirectedUrl("/prazos/contribuir"))
                .andExpect(flash().attributeExists("erro"));

        verifyNoInteractions(service);
    }

    @Test
    void deveMostrarErroDeNegocioQuandoConfirmacaoForRecusadaPeloService() throws Exception {
        when(service.confirmar(any())).thenThrow(new RegraNegocioException("O mês de conclusão não pode estar no futuro."));

        mockMvc.perform(post("/prazos/contribuir/confirmar").with(csrf())
                        .param("categoriaId", "3")
                        .param("resultado", "ENCERRADO")
                        .param("totalDias", "7")
                        .param("mesConclusao", "2026-07"))
                .andExpect(redirectedUrl("/prazos/contribuir"))
                .andExpect(flash().attribute("erro", "O mês de conclusão não pode estar no futuro."));
    }

    @Test
    void deveRecusarQuandoConfirmarSemCsrf() throws Exception {
        mockMvc.perform(post("/prazos/contribuir/confirmar").param("categoriaId", "3"))
                .andExpect(status().isForbidden());
    }
}
