package br.ucsal.prereq.service.espera;

import static br.ucsal.prereq.service.espera.SituacoesDeTeste.situacao;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.List;

import org.junit.jupiter.api.Test;

import br.ucsal.prereq.model.GrupoRequerimento;

class VerificadorEsperaTest {

    private final SituacaoEspera situacao = situacao(GrupoRequerimento.ACADEMICO, null, 10, null);

    @Test
    void deveNotificarTodosOsObservadoresQuandoVerificar() {
        ObservadorEspera primeiro = mock(ObservadorEspera.class);
        ObservadorEspera segundo = mock(ObservadorEspera.class);
        ObservadorEspera terceiro = mock(ObservadorEspera.class);
        VerificadorEspera verificador = new VerificadorEspera(List.of(primeiro, segundo, terceiro));

        verificador.verificar(situacao);

        verify(primeiro).notificar(eq(situacao), any());
        verify(segundo).notificar(eq(situacao), any());
        verify(terceiro).notificar(eq(situacao), any());
    }

    @Test
    void deveDevolverAlertasAcumuladosNaOrdemDosObservadoresQuandoVerificar() {
        VerificadorEspera verificador = new VerificadorEspera(List.of(
                observadorQueAvisa(NivelAlerta.ATENCAO, "primeiro"),
                mock(ObservadorEspera.class),
                observadorQueAvisa(NivelAlerta.INFO, "terceiro")));

        List<Alerta> alertas = verificador.verificar(situacao);

        assertThat(alertas).containsExactly(
                new Alerta(NivelAlerta.ATENCAO, "primeiro"),
                new Alerta(NivelAlerta.INFO, "terceiro"));
    }

    @Test
    void deveNotificarObservadorNovoQuandoRegistradoDepois() {
        VerificadorEspera verificador = new VerificadorEspera(List.of());
        ObservadorEspera novo = mock(ObservadorEspera.class);

        verificador.registrar(novo);
        verificador.verificar(situacao);

        verify(novo).notificar(eq(situacao), any());
    }

    @Test
    void deveDevolverListaVaziaQuandoNenhumObservadorAvisar() {
        VerificadorEspera verificador = new VerificadorEspera(List.of(mock(ObservadorEspera.class)));

        assertThat(verificador.verificar(situacao)).isEmpty();
    }

    private ObservadorEspera observadorQueAvisa(NivelAlerta nivel, String mensagem) {
        ObservadorEspera observador = mock(ObservadorEspera.class);
        doAnswer(chamada -> {
            List<Alerta> alertas = chamada.getArgument(1);
            alertas.add(new Alerta(nivel, mensagem));
            return null;
        }).when(observador).notificar(any(), any());
        return observador;
    }
}
