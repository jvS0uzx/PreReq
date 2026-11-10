package br.ucsal.prereq.service.prazo;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import br.ucsal.prereq.exception.RegraNegocioException;

public class HistoricoParser {

    private static final String MENSAGEM_ERRO_RECONHECIMENTO = "Não reconhecemos o histórico. Copie o bloco inteiro.";

    private static final Pattern PADRAO_DATA_CONCLUSAO = Pattern.compile(
            "^Data de conclusão:\\s*(\\d{2}/\\d{2}/\\d{4})\\s+\\d{1,2}:\\d{2}:\\d{1,2}$",
            Pattern.CASE_INSENSITIVE
    );

    private static final DateTimeFormatter FORMATTER_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static ResultadoLeitura ler(String texto) {
        if (texto == null || texto.isBlank()) {
            throw new RegraNegocioException(MENSAGEM_ERRO_RECONHECIMENTO);
        }

        List<String> linhas = texto.lines()
                .map(String::trim)
                .filter(l -> !l.isEmpty())
                .toList();

        List<LocalDate> datasEncontradas = new ArrayList<>();
        List<RegistroEtapa> etapasParaProcessar = new ArrayList<>();

        for (int i = 0; i < linhas.size(); i++) {
            String linha = linhas.get(i);
            Matcher matcher = PADRAO_DATA_CONCLUSAO.matcher(linha);
            if (matcher.matches()) {
                LocalDate data = LocalDate.parse(matcher.group(1), FORMATTER_DATA);
                datasEncontradas.add(data);

                if (i + 1 < linhas.size()) {
                    String proximaLinha = linhas.get(i + 1);
                    if (!PADRAO_DATA_CONCLUSAO.matcher(proximaLinha).matches()) {
                        etapasParaProcessar.add(new RegistroEtapa(data, proximaLinha));
                        i++;
                    }
                }
            }
        }

        if (datasEncontradas.isEmpty()) {
            throw new RegraNegocioException(MENSAGEM_ERRO_RECONHECIMENTO);
        }

        LocalDate primeiraData = datasEncontradas.getFirst();
        LocalDate ultimaData = datasEncontradas.getLast();
        long totalDias = ChronoUnit.DAYS.between(primeiraData, ultimaData);

        LeituraHistorico ctx = new LeituraHistorico(primeiraData);
        for (RegistroEtapa reg : etapasParaProcessar) {
            ctx.processar(reg.data(), reg.etapa());
        }
        ctx.encerrar();

        ResultadoFinal resultado;
        if (ctx.getEstadoAtual() instanceof Deferido) {
            resultado = ResultadoFinal.DEFERIDO;
        } else if (ctx.getEstadoAtual() instanceof Indeferido) {
            resultado = ResultadoFinal.INDEFERIDO;
        } else {
            resultado = ResultadoFinal.ENCERRADO;
        }

        return new ResultadoLeitura(ctx.getEtapas(), resultado, totalDias);
    }

    private record RegistroEtapa(LocalDate data, String etapa) {}
}
