package br.ucsal.prereq.service.prazo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import org.junit.jupiter.api.Test;

class CalculoMedianaTest {

    private final CalculoPrazo calculo = new CalculoMediana();

    @Test
    void deveDevolverValorDoMeioQuandoListaForImpar() {
        assertThat(calculo.calcular(List.of(5L, 1L, 3L))).isEqualTo(3.0);
    }

    @Test
    void deveDevolverMediaDosDoisDoMeioQuandoListaForPar() {
        assertThat(calculo.calcular(List.of(4L, 1L, 3L, 2L))).isEqualTo(2.5);
    }

    @Test
    void deveDevolverOProprioValorQuandoListaTiverUmElemento() {
        assertThat(calculo.calcular(List.of(7L))).isEqualTo(7.0);
    }

    @Test
    void deveRecusarQuandoListaForVazia() {
        assertThatThrownBy(() -> calculo.calcular(List.of())).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void deveInformarNomeDoCalculo() {
        assertThat(calculo.nome()).isEqualTo("Mediana");
    }
}
