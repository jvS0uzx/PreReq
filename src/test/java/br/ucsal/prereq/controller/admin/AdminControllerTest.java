package br.ucsal.prereq.controller.admin;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.logout;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.stereotype.Controller;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import br.ucsal.prereq.config.SecurityConfig;

@WebMvcTest(AdminController.class)
@Import({ SecurityConfig.class, AdminControllerTest.RotaPublicaFalsa.class })
class AdminControllerTest {

    /** Simula a tela de triagem, que ainda não existe, para testar uma rota pública. */
    @Controller
    static class RotaPublicaFalsa {

        @GetMapping("/triagem")
        @ResponseBody
        String triagem() {
            return "triagem";
        }
    }

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
                .andExpect(view().name("admin/index"))
                .andExpect(content().string(allOf(
                        containsString("href=\"/admin/setores\""),
                        containsString("href=\"/admin/documentos\""),
                        containsString("href=\"/admin/categorias\""),
                        containsString("href=\"/admin/regras\""),
                        containsString("name=\"_csrf\""))));
    }

    @Test
    void deveAbrirTriagemQuandoAcessarSemLogin() throws Exception {
        mockMvc.perform(get("/triagem"))
                .andExpect(status().isOk())
                .andExpect(content().string("triagem"));
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
