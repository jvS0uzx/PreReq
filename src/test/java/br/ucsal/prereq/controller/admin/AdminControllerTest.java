package br.ucsal.prereq.controller.admin;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.logout;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import br.ucsal.prereq.config.SecurityConfig;

@WebMvcTest(AdminController.class)
@Import(SecurityConfig.class)
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void deveRedirecionarParaLoginQuandoAcessarAdminSemLogin() throws Exception {
        mockMvc.perform(get("/admin"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    @WithMockUser
    void deveExibirPainelQuandoAcessarAdminComLogin() throws Exception {
        mockMvc.perform(get("/admin"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/index"));
    }

    @Test
    void deveAutenticarQuandoUsarCredenciaisDoAmbiente() throws Exception {
        mockMvc.perform(formLogin().user("admin").password("senha-de-teste"))
                .andExpect(authenticated().withUsername("admin"));
    }

    @Test
    void deveRecusarLoginQuandoSenhaEstiverErrada() throws Exception {
        mockMvc.perform(formLogin().user("admin").password("errada"))
                .andExpect(unauthenticated());
    }

    @Test
    void deveIrParaPainelQuandoEntrarPelaTelaDeLogin() throws Exception {
        mockMvc.perform(formLogin().user("admin").password("senha-de-teste"))
                .andExpect(redirectedUrl("/admin"));
    }

    @Test
    @WithMockUser
    void deveRecusarPostQuandoNaoHouverTokenCsrf() throws Exception {
        mockMvc.perform(post("/admin"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser
    void deveVoltarParaInicioQuandoFizerLogout() throws Exception {
        mockMvc.perform(logout())
                .andExpect(redirectedUrl("/"));
    }
}
