package br.ucsal.prereq.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PaginaErroTest {

    @LocalServerPort
    private int porta;

    @Test
    void deveExibirPagina404QuandoRotaNaoExistir() throws Exception {
        HttpRequest requisicao = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + porta + "/rota-inexistente"))
                .header("Accept", "text/html")
                .build();

        HttpResponse<String> resposta = HttpClient.newHttpClient()
                .send(requisicao, HttpResponse.BodyHandlers.ofString());

        assertThat(resposta.statusCode()).isEqualTo(404);
        assertThat(resposta.body()).contains("Erro 404");
    }
}
