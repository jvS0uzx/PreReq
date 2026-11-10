package br.ucsal.prereq.service.prazo;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import br.ucsal.prereq.exception.RegraNegocioException;

class HistoricoParserTest {

    private String carregarAmostra(String nomeArquivo) throws IOException {
        String caminho = "historicos/" + nomeArquivo;
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(caminho)) {
            if (is == null) {
                throw new IllegalArgumentException("Arquivo não encontrado no classpath: " + caminho);
            }
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    @Test
    void deveProcessarAmostra01EncerradoSemSetores() throws Exception {
        String texto = carregarAmostra("amostra-01.txt");

        ResultadoLeitura resultado = HistoricoParser.ler(texto);

        assertEquals(ResultadoFinal.ENCERRADO, resultado.resultado());
        assertEquals(17, resultado.totalDias());
        assertTrue(resultado.etapas().isEmpty());
    }

    @Test
    void deveLancarExcecaoQuandoTextoForNulo() {
        RegraNegocioException ex = assertThrows(
                RegraNegocioException.class,
                () -> HistoricoParser.ler(null));
        assertEquals("Não reconhecemos o histórico. Copie o bloco inteiro.", ex.getMessage());
    }

    @Test
    void deveLancarExcecaoQuandoTextoForVazio() {
        RegraNegocioException ex = assertThrows(
                RegraNegocioException.class,
                () -> HistoricoParser.ler("   "));
        assertEquals("Não reconhecemos o histórico. Copie o bloco inteiro.", ex.getMessage());
    }

    @Test
    void deveLancarExcecaoQuandoTextoNaoPossuirDatas() {
        String textoInvalido = """
                Histórico de etapas
                ABERTURA DO ATENDIMENTO
                ANÁLISE DA SECRETARIA
                DEFERIDO
                """;

        RegraNegocioException ex = assertThrows(
                RegraNegocioException.class,
                () -> HistoricoParser.ler(textoInvalido));
        assertEquals("Não reconhecemos o histórico. Copie o bloco inteiro.", ex.getMessage());
    }
}
