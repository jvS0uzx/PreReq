package br.ucsal.prereq.service.prazo;

import java.time.LocalDate;

public class Deferido implements EstadoPedido {

    @Override
    public EstadoPedido proximo(LeituraHistorico ctx, LocalDate data, String linha) {
        return this;
    }

    @Override
    public boolean finalizado() {
        return true;
    }
}
