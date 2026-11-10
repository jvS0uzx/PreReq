package br.ucsal.prereq.service.prazo;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

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
    void deveEncerrarSemEtapasQuandoLerAmostra01() throws Exception {
        String texto = carregarAmostra("amostra-01.txt");

        ResultadoLeitura resultado = HistoricoParser.ler(texto);

        assertEquals(ResultadoFinal.ENCERRADO, resultado.resultado());
        assertEquals(17, resultado.totalDias());
        assertTrue(resultado.etapas().isEmpty());
    }

    @Test
    void deveContarCadaSetorAteASuaConclusaoQuandoPedidoForDeferido() {
        String texto = """
                Histórico de etapas
                Data de conclusão:01/02/2026 10:00:00
                ABERTURA DO ATENDIMENTO
                Data de conclusão:03/02/2026 9:15:7
                ANÁLISE DA SECRETARIA
                Data de conclusão:05/02/2026 14:30:0
                ANÁLISE DA COORDENAÇÃO
                Data de conclusão:11/02/2026 08:00:00
                DEFERIDO
                Data de conclusão:11/02/2026 08:00:5
                """;

        ResultadoLeitura resultado = HistoricoParser.ler(texto);

        assertEquals(ResultadoFinal.DEFERIDO, resultado.resultado());
        assertEquals(List.of(
                new EtapaLida("SECRETARIA", LocalDate.of(2026, 2, 1), LocalDate.of(2026, 2, 3), 2),
                new EtapaLida("COORDENAÇÃO", LocalDate.of(2026, 2, 3), LocalDate.of(2026, 2, 5), 2)),
                resultado.etapas());
    }

    @Test
    void deveIgnorarLinhasSeguintesQuandoPedidoForIndeferido() {
        String texto = """
                Histórico de etapas
                Data de conclusão:02/03/2026 08:00:00
                ABERTURA DO ATENDIMENTO
                Data de conclusão:06/03/2026 10:00:00
                ANÁLISE DO FINANCEIRO
                Data de conclusão:09/03/2026 10:00:00
                INDEFERIDO
                Data de conclusão:12/03/2026 10:00:00
                ANÁLISE DA SECRETARIA
                Data de conclusão:12/03/2026 10:00:1
                """;

        ResultadoLeitura resultado = HistoricoParser.ler(texto);

        assertEquals(ResultadoFinal.INDEFERIDO, resultado.resultado());
        assertEquals(List.of(
                new EtapaLida("FINANCEIRO", LocalDate.of(2026, 3, 2), LocalDate.of(2026, 3, 6), 4)),
                resultado.etapas());
    }

    @Test
    void deveFecharSetorNaSuaConclusaoQuandoAtendimentoForEncerrado() {
        String texto = """
                Histórico de etapas
                Data de conclusão:01/02/2026 10:00:00
                ABERTURA DO ATENDIMENTO
                Data de conclusão:04/02/2026 10:00:00
                ANÁLISE DA SECRETARIA
                Data de conclusão:09/02/2026 10:00:00
                ENCERRAMENTO DO ATENDIMENTO
                Data de conclusão:09/02/2026 10:00:9
                """;

        ResultadoLeitura resultado = HistoricoParser.ler(texto);

        assertEquals(ResultadoFinal.ENCERRADO, resultado.resultado());
        assertEquals(List.of(
                new EtapaLida("SECRETARIA", LocalDate.of(2026, 2, 1), LocalDate.of(2026, 2, 4), 3)),
                resultado.etapas());
    }

    @Test
    void deveJuntarEtapasSeguidasQuandoMesmoSetorSeRepetir() {
        String texto = """
                Histórico de etapas
                Data de conclusão:01/02/2026 10:00:00
                ABERTURA DO ATENDIMENTO
                Data de conclusão:03/02/2026 10:00:00
                ANÁLISE DA SECRETARIA
                Data de conclusão:06/02/2026 10:00:00
                CONFERÊNCIA DA SECRETARIA
                Data de conclusão:08/02/2026 10:00:00
                ANÁLISE DA COORDENAÇÃO
                Data de conclusão:09/02/2026 10:00:00
                DEFERIDO
                """;

        ResultadoLeitura resultado = HistoricoParser.ler(texto);

        assertEquals(List.of(
                new EtapaLida("SECRETARIA", LocalDate.of(2026, 2, 1), LocalDate.of(2026, 2, 6), 5),
                new EtapaLida("COORDENAÇÃO", LocalDate.of(2026, 2, 6), LocalDate.of(2026, 2, 8), 2)),
                resultado.etapas());
    }

    @Test
    void deveFecharUltimoSetorQuandoTextoTerminarSemResultado() {
        String texto = """
                Histórico de etapas
                Data de conclusão:01/02/2026 10:00:00
                ABERTURA DO ATENDIMENTO
                Data de conclusão:04/02/2026 10:00:00
                ANÁLISE DA SECRETARIA
                Data de conclusão:04/02/2026 10:00:1
                """;

        ResultadoLeitura resultado = HistoricoParser.ler(texto);

        assertEquals(List.of(
                new EtapaLida("SECRETARIA", LocalDate.of(2026, 2, 1), LocalDate.of(2026, 2, 4), 3)),
                resultado.etapas());
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
