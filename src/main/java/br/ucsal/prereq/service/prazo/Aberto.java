package br.ucsal.prereq.service.prazo;

import java.time.LocalDate;

public class Aberto implements EstadoPedido {

    @Override
    public EstadoPedido proximo(LeituraHistorico ctx, LocalDate data, String linha) {
        if (linha == null) {
            return this;
        }

        String texto = linha.trim().toUpperCase();

        if (texto.equals("ABERTURA DO ATENDIMENTO")) {
            return this;
        }

        if (texto.equals("DEFERIDO")) {
            return new Deferido();
        }

        if (texto.equals("INDEFERIDO")) {
            return new Indeferido();
        }

        String setor = EmAnaliseSetor.identificarSetor(texto);
        if (setor != null) {
            return new EmAnaliseSetor(setor);
        }

        return this;
    }

    @Override
    public boolean finalizado() {
        return false;
    }
}
