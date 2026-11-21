package br.ucsal.prereq.service.prazo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import org.junit.jupiter.api.Test;

class CalculoPercentilTest {

    private final CalculoPrazo p75 = new CalculoPercentil(75);

    @Test
    void deveDevolverValorDaPosicaoQuandoPercentilCairEmCimaDeUmElemento() {
        assertThat(p75.calcular(List.of(50L, 10L, 40L, 20L, 30L))).isEqualTo(40.0);
    }

    @Test
    void deveInterpolarQuandoPercentilCairEntreDoisElementos() {
        assertThat(p75.calcular(List.of(1L, 2L, 3L, 4L))).isEqualTo(3.25);
    }

    @Test
    void deveDevolverOProprioValorQuandoListaTiverUmElemento() {
        assertThat(p75.calcular(List.of(7L))).isEqualTo(7.0);
    }

    @Test
    void deveCoincidirComAMedianaQuandoPercentilFor50() {
        List<Long> dias = List.of(9L, 2L, 4L, 7L);

        assertThat(new CalculoPercentil(50).calcular(dias)).isEqualTo(new CalculoMediana().calcular(dias));
    }

    @Test
    void deveRecusarQuandoPercentilEstiverForaDeZeroACem() {
        assertThatThrownBy(() -> new CalculoPercentil(101)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void deveRecusarQuandoListaForVazia() {
        assertThatThrownBy(() -> p75.calcular(List.of())).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void deveInformarNomeDoCalculo() {
        assertThat(p75.nome()).isEqualTo("P75");
    }
}
