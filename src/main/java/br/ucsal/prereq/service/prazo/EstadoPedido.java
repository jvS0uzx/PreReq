package br.ucsal.prereq.service.prazo;

import java.time.LocalDate;

public interface EstadoPedido {

    EstadoPedido proximo(LeituraHistorico ctx, LocalDate data, String linha);

    boolean finalizado();

    default void encerrar(LeituraHistorico ctx) {
    }
}
