package br.ucsal.prereq.service.prazo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import br.ucsal.prereq.model.EtapaPrazo;
import br.ucsal.prereq.model.GrupoRequerimento;
import br.ucsal.prereq.model.RegistroPrazo;
import br.ucsal.prereq.model.ResultadoPrazo;
import br.ucsal.prereq.model.Setor;
import br.ucsal.prereq.model.TipoRequerimento;
import br.ucsal.prereq.repository.RegistroPrazoRepository;

@ExtendWith(MockitoExtension.class)
class PrazoReferenciaServiceTest {

    private static final Setor FINANCEIRO = new Setor("Financeiro", null);

    @Mock
    private RegistroPrazoRepository repository;

    @InjectMocks
    private PrazoReferenciaService service;

    private final TipoRequerimento reembolso = categoria(1L, "REEMBOLSO TODOS OS NIVEIS");
    private final TipoRequerimento historico = categoria(2L, "HISTÓRICO ESCOLAR - GRADUAÇÃO");

    @Test
    void deveDevolverVazioQuandoHouverSoDoisRegistros() {
        when(repository.findByCategoriaId(1L)).thenReturn(List.of(registro(reembolso, 10), registro(reembolso, 20)));

        assertThat(service.porCategoria(1L, new CalculoMediana())).isEmpty();
    }

    @Test
    void deveCalcularTotalEAmostraQuandoHouverTresRegistros() {
        when(repository.findByCategoriaId(1L)).thenReturn(
                List.of(registro(reembolso, 10), registro(reembolso, 30), registro(reembolso, 20)));

        Optional<PrazoReferencia> prazo = service.porCategoria(1L, new CalculoMediana());

        assertThat(prazo).isPresent();
        assertThat(prazo.get().calculo()).isEqualTo("Mediana");
        assertThat(prazo.get().totalDias()).isEqualTo(20.0);
        assertThat(prazo.get().amostra()).isEqualTo(3);
        assertThat(prazo.get().setores()).isEmpty();
    }

    @Test
    void deveMudarOResultadoSoTrocandoOCalculoQuandoPedirP75() {
        when(repository.findByCategoriaId(1L)).thenReturn(List.of(registro(reembolso, 10),
                registro(reembolso, 20), registro(reembolso, 30), registro(reembolso, 40), registro(reembolso, 50)));

        assertThat(service.porCategoria(1L, new CalculoMediana()).get().totalDias()).isEqualTo(30.0);
        assertThat(service.porCategoria(1L, new CalculoPercentil(75)).get().totalDias()).isEqualTo(40.0);
    }

    @Test
    void deveCalcularPorSetorSomandoEtapasRepetidasEContandoSoQuemPassouPeloSetor() {
        RegistroPrazo primeiro = registro(reembolso, 30);
        primeiro.adicionarEtapa(new EtapaPrazo(null, "SECRETARIA", 2, 1));
        primeiro.adicionarEtapa(new EtapaPrazo(FINANCEIRO, null, 10, 2));
        primeiro.adicionarEtapa(new EtapaPrazo(null, "SECRETARIA", 4, 3));
        RegistroPrazo segundo = registro(reembolso, 20);
        segundo.adicionarEtapa(new EtapaPrazo(FINANCEIRO, null, 20, 1));
        RegistroPrazo terceiro = registro(reembolso, 25);
        terceiro.adicionarEtapa(new EtapaPrazo(null, "SECRETARIA", 8, 1));
        terceiro.adicionarEtapa(new EtapaPrazo(FINANCEIRO, null, 12, 2));
        when(repository.findByCategoriaId(1L)).thenReturn(List.of(primeiro, segundo, terceiro));

        PrazoReferencia prazo = service.porCategoria(1L, new CalculoMediana()).orElseThrow();

        assertThat(prazo.setores()).containsExactly(
                new PrazoSetor("SECRETARIA", 7.0, 2),
                new PrazoSetor("Financeiro", 12.0, 3));
    }

    @Test
    void deveListarUmaLinhaPorCategoriaComOsDoisCalculosQuandoHouverRegistros() {
        when(repository.findAllByOrderByCategoriaNomeNoPortalAscIdAsc()).thenReturn(List.of(
                registro(historico, 4),
                registro(reembolso, 10), registro(reembolso, 20), registro(reembolso, 30), registro(reembolso, 40)));

        List<LinhaPrazo> linhas = service.listar(new CalculoMediana(), new CalculoPercentil(75));

        assertThat(linhas).hasSize(2);
        assertThat(linhas.get(0).categoria()).isSameAs(historico);
        assertThat(linhas.get(0).registros()).isEqualTo(1);
        assertThat(linhas.get(0).central()).isEmpty();
        assertThat(linhas.get(0).folga()).isEmpty();
        assertThat(linhas.get(1).categoria()).isSameAs(reembolso);
        assertThat(linhas.get(1).registros()).isEqualTo(4);
        assertThat(linhas.get(1).central().orElseThrow().totalDias()).isEqualTo(25.0);
        assertThat(linhas.get(1).folga().orElseThrow().totalDias()).isEqualTo(32.5);
    }

    private RegistroPrazo registro(TipoRequerimento categoria, long totalDias) {
        return new RegistroPrazo(categoria, ResultadoPrazo.DEFERIDO, totalDias, YearMonth.of(2026, 9));
    }

    private static TipoRequerimento categoria(Long id, String nome) {
        TipoRequerimento categoria = TipoRequerimento.novo(GrupoRequerimento.FINANCEIRO);
        categoria.setNomeNoPortal(nome);
        ReflectionTestUtils.setField(categoria, "id", id);
        return categoria;
    }
}
