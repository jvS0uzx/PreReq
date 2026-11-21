package br.ucsal.prereq.service.prazo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LeituraHistorico {

    private EstadoPedido estadoAtual;
    private LocalDate dataEntradaEstado;
    private final List<EtapaLida> etapas = new ArrayList<>();

    public LeituraHistorico(LocalDate dataAbertura) {
        this.estadoAtual = new Aberto();
        this.dataEntradaEstado = dataAbertura;
    }

    public void processar(LocalDate data, String linha) {
        if (!estadoAtual.finalizado()) {
            estadoAtual = estadoAtual.proximo(this, data, linha);
        }
    }

    public void encerrar() {
        estadoAtual.encerrar(this);
    }

    public void registrarEtapa(EtapaLida etapa) {
        etapas.add(etapa);
    }

    public LocalDate getDataEntradaEstado() {
        return dataEntradaEstado;
    }

    public void setDataEntradaEstado(LocalDate data) {
        this.dataEntradaEstado = data;
    }

    public EstadoPedido getEstadoAtual() {
        return estadoAtual;
    }

    public List<EtapaLida> getEtapas() {
        return Collections.unmodifiableList(etapas);
    }
}
