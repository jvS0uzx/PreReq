package br.ucsal.prereq.service.prazo;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class EmAnaliseSetor implements EstadoPedido {

    private final String setor;
    private final LocalDate conclusao;

    public EmAnaliseSetor(String setor, LocalDate conclusao) {
        this.setor = setor;
        this.conclusao = conclusao;
    }

    public String getSetor() {
        return setor;
    }

    @Override
    public EstadoPedido proximo(LeituraHistorico ctx, LocalDate data, String linha) {
        if (linha == null) {
            return this;
        }

        if (setor.equals(identificarSetor(linha))) {
            return new EmAnaliseSetor(setor, data);
        }

        encerrar(ctx);
        return new Aberto().proximo(ctx, data, linha);
    }

    @Override
    public boolean finalizado() {
        return false;
    }

    @Override
    public void encerrar(LeituraHistorico ctx) {
        LocalDate entrada = ctx.getDataEntradaEstado();
        long dias = ChronoUnit.DAYS.between(entrada, conclusao);
        ctx.registrarEtapa(new EtapaLida(setor, entrada, conclusao, dias));
        ctx.setDataEntradaEstado(conclusao);
    }

    static String identificarSetor(String linha) {
        if (linha == null) {
            return null;
        }

        String texto = linha.trim().toUpperCase();
        if (texto.contains("REGISTRO ACADÊMICO") || texto.contains("REGISTRO ACADEMICO")) {
            return "REGISTRO ACADÊMICO";
        }
        if (texto.contains("SECRETARIA")) {
            return "SECRETARIA";
        }
        if (texto.contains("COORDENAÇÃO") || texto.contains("COORDENACAO")) {
            return "COORDENAÇÃO";
        }
        if (texto.contains("BOLSA") || texto.contains("PROUNI") || texto.contains("FIES")) {
            return "PROUNI/FIES";
        }
        if (texto.contains("CONTAS A RECEBER")) {
            return "CONTAS A RECEBER";
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
