package br.ucsal.prereq.controller.admin;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import br.ucsal.prereq.config.SecurityConfig;
import br.ucsal.prereq.exception.RecursoNaoEncontradoException;
import br.ucsal.prereq.exception.RegraNegocioException;
import br.ucsal.prereq.model.GrupoRequerimento;
import br.ucsal.prereq.model.Indeferimento;
import br.ucsal.prereq.model.TipoRequerimento;
import br.ucsal.prereq.service.IndeferimentoService;

@WebMvcTest(AdminIndeferimentoController.class)
@Import(SecurityConfig.class)
class AdminIndeferimentoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IndeferimentoService service;

    @Test
    void deveMandarParaOLoginQuandoNaoEstiverLogado() throws Exception {
        mockMvc.perform(get("/admin/indeferimentos"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser
    void deveListarPendentesComAcoesQuandoLogado() throws Exception {
        when(service.listarPendentes()).thenReturn(List.of(indeferimento()));

        mockMvc.perform(get("/admin/indeferimentos"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/indeferimentos/lista"))
                .andExpect(content().string(containsString("precisava encerrar meu estágio")))
                .andExpect(content().string(containsString("campo equivocado")))
                .andExpect(content().string(containsString("20/11/2026")))
                .andExpect(content().string(containsString("/admin/indeferimentos/8/converter")))
                .andExpect(content().string(containsString("/admin/indeferimentos/8/descartar")));
    }

    @Test
    @WithMockUser
    void deveAvisarQuandoNaoHouverPendentes() throws Exception {
        when(service.listarPendentes()).thenReturn(List.of());

        mockMvc.perform(get("/admin/indeferimentos"))
                .andExpect(content().string(containsString("Nenhum indeferimento pendente.")));
    }

    @Test
    @WithMockUser
    void deveRedirecionarParaNovaRegraQuandoConverter() throws Exception {
        when(service.buscar(8L)).thenReturn(indeferimento());

        mockMvc.perform(post("/admin/indeferimentos/8/converter").with(csrf()))
                .andExpect(redirectedUrl("/admin/regras/novo?indeferimento=8"));
    }

    @Test
    @WithMockUser
    void deveRetornar404QuandoConverterIndeferimentoInexistente() throws Exception {
        when(service.buscar(99L)).thenThrow(new RecursoNaoEncontradoException("Indeferimento não encontrado"));

        mockMvc.perform(post("/admin/indeferimentos/99/converter").with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void deveDescartarEVoltarParaListaQuandoDescartar() throws Exception {
        mockMvc.perform(post("/admin/indeferimentos/8/descartar").with(csrf()))
                .andExpect(redirectedUrl("/admin/indeferimentos"))
                .andExpect(flash().attribute("sucesso", "Indeferimento descartado."));

        verify(service).descartar(8L);
    }

    @Test
    @WithMockUser
    void deveMostrarErroQuandoDescartarIndeferimentoJaTratado() throws Exception {
        doThrow(new RegraNegocioException("Este indeferimento já foi tratado.")).when(service).descartar(8L);

        mockMvc.perform(post("/admin/indeferimentos/8/descartar").with(csrf()))
                .andExpect(redirectedUrl("/admin/indeferimentos"))
                .andExpect(flash().attribute("erro", "Este indeferimento já foi tratado."));
    }

    private Indeferimento indeferimento() {
        TipoRequerimento categoria = TipoRequerimento.novo(GrupoRequerimento.ESTAGIO);
        categoria.setNomeNoPortal("SOLICITAÇÃO DE DISPENSA DE ESTÁGIO CURRICULAR - DIREITO");
        Indeferimento indeferimento = new Indeferimento(categoria, "precisava encerrar meu estágio",
                "campo equivocado", LocalDate.of(2026, 11, 20));
        ReflectionTestUtils.setField(indeferimento, "id", 8L);
        return indeferimento;
    }
}
