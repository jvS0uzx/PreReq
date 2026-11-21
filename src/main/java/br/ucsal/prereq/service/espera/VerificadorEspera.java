package br.ucsal.prereq.service.espera;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class VerificadorEspera {

    private final List<ObservadorEspera> observadores = new ArrayList<>();

    public VerificadorEspera(List<ObservadorEspera> observadores) {
        observadores.forEach(this::registrar);
    }

    public void registrar(ObservadorEspera observador) {
        observadores.add(observador);
    }

    public List<Alerta> verificar(SituacaoEspera situacao) {
        List<Alerta> alertas = new ArrayList<>();
        for (ObservadorEspera observador : observadores) {
            observador.notificar(situacao, alertas);
        }
        return List.copyOf(alertas);
    }
}
