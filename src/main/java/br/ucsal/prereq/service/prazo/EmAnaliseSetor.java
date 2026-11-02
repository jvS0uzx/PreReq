package br.ucsal.prereq.service.prazo;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class EmAnaliseSetor implements EstadoPedido {

    private final String setor;

    public EmAnaliseSetor(String setor) {
        this.setor = setor;
    }

    public String getSetor() {
        return setor;
    }

    @Override
    public EstadoPedido proximo(LeituraHistorico ctx, LocalDate data, String linha) {
        if (linha == null) {
            return this;
        }

        String texto = linha.trim().toUpperCase();

        if (texto.equals("DEFERIDO")) {
            fecharEtapa(ctx, data);
            return new Deferido();
        }

        if (texto.equals("INDEFERIDO")) {
            fecharEtapa(ctx, data);
            return new Indeferido();
        }

        String novoSetor = identificarSetor(texto);
        if (novoSetor != null && !novoSetor.equals(this.setor)) {
            fecharEtapa(ctx, data);
            ctx.setDataEntradaEstado(data);
            return new EmAnaliseSetor(novoSetor);
        }

        if (texto.equals("ENCERRAMENTO DO ATENDIMENTO")) {
            fecharEtapa(ctx, data);
            return this;
        }

        return this;
    }

    @Override
    public boolean finalizado() {
        return false;
    }

    private void fecharEtapa(LeituraHistorico ctx, LocalDate dataSaida) {
        long dias = ChronoUnit.DAYS.between(ctx.getDataEntradaEstado(), dataSaida);
        ctx.registrarEtapa(new EtapaLida(this.setor, ctx.getDataEntradaEstado(), dataSaida, dias));
    }

    static String identificarSetor(String linha) {
        if (linha == null) {
            return null;
        }

        String texto = linha.trim().toUpperCase();
        if (texto.contains("SECRETARIA")) {
            return "SECRETARIA";
        }
        if (texto.contains("COORDENAÇÃO") || texto.contains("COORDENACAO")) {
            return "COORDENAÇÃO";
        }
        if (texto.contains("BOLSA") || texto.contains("PROUNI") || texto.contains("FIES")) {
            return "PROUNI/FIES";
        }
        if (texto.contains("FINANCEIR")) {
            return "FINANCEIRO";
        }
        if (texto.contains("COES")) {
            return "COES";
        }

        return null;
    }
}
