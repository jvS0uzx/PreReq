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
    void deveProcessarAmostra02AcademicoDeferido() throws Exception {
        String texto = carregarAmostra("amostra-02.txt");

        ResultadoLeitura resultado = HistoricoParser.ler(texto);

        assertEquals(ResultadoFinal.DEFERIDO, resultado.resultado());
        assertEquals(10, resultado.totalDias());
        assertEquals(2, resultado.etapas().size());

        EtapaLida etapa1 = resultado.etapas().get(0);
        assertEquals("SECRETARIA", etapa1.setor());
        assertEquals(LocalDate.of(2026, 2, 3), etapa1.entrada());
        assertEquals(LocalDate.of(2026, 2, 11), etapa1.saida());
        assertEquals(8, etapa1.dias());

        EtapaLida etapa2 = resultado.etapas().get(1);
        assertEquals("COORDENAÇÃO", etapa2.setor());
        assertEquals(LocalDate.of(2026, 2, 11), etapa2.entrada());
        assertEquals(LocalDate.of(2026, 2, 13), etapa2.saida());
        assertEquals(2, etapa2.dias());
    }

    @Test
    void deveProcessarAmostra03AcademicoIndeferido() throws Exception {
        String texto = carregarAmostra("amostra-03.txt");

        ResultadoLeitura resultado = HistoricoParser.ler(texto);

        assertEquals(ResultadoFinal.INDEFERIDO, resultado.resultado());
        assertEquals(5, resultado.totalDias());
        assertEquals(2, resultado.etapas().size());

        EtapaLida etapa1 = resultado.etapas().get(0);
        assertEquals("SECRETARIA", etapa1.setor());
        assertEquals(LocalDate.of(2026, 3, 17), etapa1.entrada());
        assertEquals(LocalDate.of(2026, 3, 22), etapa1.saida());
        assertEquals(5, etapa1.dias());

        EtapaLida etapa2 = resultado.etapas().get(1);
        assertEquals("COORDENAÇÃO", etapa2.setor());
        assertEquals(LocalDate.of(2026, 3, 22), etapa2.entrada());
        assertEquals(LocalDate.of(2026, 3, 22), etapa2.saida());
        assertEquals(0, etapa2.dias());
    }

    @Test
    void deveProcessarAmostra04FinanceiroDeferido() throws Exception {
        String texto = carregarAmostra("amostra-04.txt");

        ResultadoLeitura resultado = HistoricoParser.ler(texto);

        assertEquals(ResultadoFinal.DEFERIDO, resultado.resultado());
        assertEquals(15, resultado.totalDias());
        assertEquals(2, resultado.etapas().size());

        EtapaLida etapa1 = resultado.etapas().get(0);
        assertEquals("SECRETARIA", etapa1.setor());
        assertEquals(LocalDate.of(2026, 1, 10), etapa1.entrada());
        assertEquals(LocalDate.of(2026, 1, 16), etapa1.saida());
        assertEquals(6, etapa1.dias());

        EtapaLida etapa2 = resultado.etapas().get(1);
        assertEquals("FINANCEIRO", etapa2.setor());
        assertEquals(LocalDate.of(2026, 1, 16), etapa2.entrada());
        assertEquals(LocalDate.of(2026, 1, 25), etapa2.saida());
        assertEquals(9, etapa2.dias());
    }

    @Test
    void deveProcessarAmostra05FinanceiroComBolsaIndeferido() throws Exception {
        String texto = carregarAmostra("amostra-05.txt");

        ResultadoLeitura resultado = HistoricoParser.ler(texto);

        assertEquals(ResultadoFinal.INDEFERIDO, resultado.resultado());
        assertEquals(20, resultado.totalDias());
        assertEquals(3, resultado.etapas().size());

        EtapaLida etapa1 = resultado.etapas().get(0);
        assertEquals("SECRETARIA", etapa1.setor());
        assertEquals(LocalDate.of(2026, 2, 5), etapa1.entrada());
        assertEquals(LocalDate.of(2026, 2, 12), etapa1.saida());
        assertEquals(7, etapa1.dias());

        EtapaLida etapa2 = resultado.etapas().get(1);
        assertEquals("FINANCEIRO", etapa2.setor());
        assertEquals(LocalDate.of(2026, 2, 12), etapa2.entrada());
        assertEquals(LocalDate.of(2026, 2, 20), etapa2.saida());
        assertEquals(8, etapa2.dias());

        EtapaLida etapa3 = resultado.etapas().get(2);
        assertEquals("PROUNI/FIES", etapa3.setor());
        assertEquals(LocalDate.of(2026, 2, 20), etapa3.entrada());
        assertEquals(LocalDate.of(2026, 2, 25), etapa3.saida());
        assertEquals(5, etapa3.dias());
    }

    @Test
    void deveProcessarAmostra06EstagioDeferido() throws Exception {
        String texto = carregarAmostra("amostra-06.txt");

        ResultadoLeitura resultado = HistoricoParser.ler(texto);

        assertEquals(ResultadoFinal.DEFERIDO, resultado.resultado());
        assertEquals(8, resultado.totalDias());
        assertEquals(2, resultado.etapas().size());

        EtapaLida etapa1 = resultado.etapas().get(0);
        assertEquals("SECRETARIA", etapa1.setor());
        assertEquals(LocalDate.of(2026, 1, 22), etapa1.entrada());
        assertEquals(LocalDate.of(2026, 1, 28), etapa1.saida());
        assertEquals(6, etapa1.dias());

        EtapaLida etapa2 = resultado.etapas().get(1);
        assertEquals("COES", etapa2.setor());
        assertEquals(LocalDate.of(2026, 1, 28), etapa2.entrada());
        assertEquals(LocalDate.of(2026, 1, 30), etapa2.saida());
        assertEquals(2, etapa2.dias());
    }

    @Test
    void deveLancarExcecaoQuandoTextoForNulo() {
        RegraNegocioException ex = assertThrows(
                RegraNegocioException.class,
                () -> HistoricoParser.ler(null)
        );
        assertEquals("Não reconhecemos o histórico. Copie o bloco inteiro.", ex.getMessage());
    }

    @Test
    void deveLancarExcecaoQuandoTextoForVazio() {
        RegraNegocioException ex = assertThrows(
                RegraNegocioException.class,
                () -> HistoricoParser.ler("   ")
        );
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
                () -> HistoricoParser.ler(textoInvalido)
        );
        assertEquals("Não reconhecemos o histórico. Copie o bloco inteiro.", ex.getMessage());
    }
}
