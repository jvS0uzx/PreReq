package br.ucsal.prereq.service.triagem;

import br.ucsal.prereq.model.GrupoRequerimento;
import br.ucsal.prereq.model.OrigemRegra;
import br.ucsal.prereq.model.RegraTriagem;
import br.ucsal.prereq.model.TipoRequerimento;
import br.ucsal.prereq.repository.RegraTriagemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TriagemServiceTest {

    @Mock
    private RegraTriagemRepository repository;

    @InjectMocks
    private TriagemService service;

    private TipoRequerimento estagio;
    private TipoRequerimento trancamento;
    private RegraTriagem regraEstagio;
    private RegraTriagem regraTrancamento;
    private RegraTriagem regraOuvidoria;

    @BeforeEach
    void setUp() {
        estagio = TipoRequerimento.novo(GrupoRequerimento.ESTAGIO);
        estagio.setNomeNoPortal("ENCERRAMENTO DE ESTÁGIO");
        trancamento = TipoRequerimento.novo(GrupoRequerimento.ACADEMICO);
        trancamento.setNomeNoPortal("TRANCAMENTO DE MATRÍCULA EM DISCIPLINA");

        regraEstagio = regra("encerrar,encerramento,estagio", estagio, null, 5);
        regraTrancamento = regra("trancar,trancamento", trancamento, null, 5);
        regraOuvidoria = regra("reclamacao,ouvidoria", null, "Ouvidoria", 5);

        when(repository.findByAtivaTrue()).thenReturn(List.of(regraEstagio, regraTrancamento, regraOuvidoria));
    }

    @Test
    void deveEncontrarCategoriaDeEstagioQuandoDescreverEncerramentoDeEstagio() {
        ResultadoTriagem resultado = service.triar("preciso encerrar meu estágio para começar outro");

        assertTrue(resultado.encontrou());
        assertSame(estagio, resultado.categoria());
        assertSame(regraEstagio, resultado.regraAplicada());
    }

    @Test
    void deveEncontrarTrancamentoQuandoDescreverTrancamentoDeDisciplina() {
        ResultadoTriagem resultado = service.triar("quero trancar uma disciplina");

        assertTrue(resultado.encontrou());
        assertSame(trancamento, resultado.categoria());
    }

    @Test
    void deveUsarEtapaPadraoQuandoNaoHouverPalavraConhecida() {
        ResultadoTriagem resultado = service.triar("blablabla qualquer coisa");

        assertFalse(resultado.encontrou());
        assertNull(resultado.categoria());
        assertNull(resultado.regraAplicada());
        assertEquals(EtapaPadrao.MENSAGEM, resultado.mensagem());
    }

    @Test
    void deveIndicarCanalAlternativoQuandoSoRegraSemCategoriaCobrir() {
        ResultadoTriagem resultado = service.triar("quero fazer uma reclamação");

        assertFalse(resultado.encontrou());
        assertNull(resultado.categoria());
        assertEquals("Ouvidoria", resultado.canalAlternativo());
        assertSame(regraOuvidoria, resultado.regraAplicada());
    }

    @Test
    void deveIgnorarAcentosEMaiusculasQuandoTriar() {
        ResultadoTriagem comAcento = service.triar("QUERO TRANCAR UMA DISCIPLINA!");
        ResultadoTriagem semAcento = service.triar("quero trancar uma disciplina");
        ResultadoTriagem acentuado = service.triar("Preciso encerrar meu ESTÁGIO");

        assertSame(comAcento.categoria(), semAcento.categoria());
        assertSame(estagio, acentuado.categoria());
    }

    @Test
    void deveVencerMaiorPontuacaoQuandoDuasRegrasCasarem() {
        // "encerrar" e "estagio" pontuam 2 contra 1 de "trancar"
        ResultadoTriagem resultado = service.triar("trancar ou encerrar meu estagio");

        assertSame(estagio, resultado.categoria());
    }

    @Test
    void deveDesempatarPelaPrioridadeQuandoPontuacaoIgual() {
        regraTrancamento.setPrioridade(9);

        ResultadoTriagem resultado = service.triar("trancar ou encerrar");

        assertSame(trancamento, resultado.categoria());
    }

    @Test
    void deveIgnorarRegraInativaQuandoTriar() {
        regraTrancamento.setAtiva(false);

        ResultadoTriagem resultado = service.triar("quero trancar uma disciplina");

        assertFalse(resultado.encontrou());
    }

    private RegraTriagem regra(String palavras, TipoRequerimento categoria, String canal, int prioridade) {
        RegraTriagem regra = new RegraTriagem(palavras, OrigemRegra.MANUAL);
        regra.setCategoria(categoria);
        regra.setCanalAlternativo(canal);
        regra.setPrioridade(prioridade);
        return regra;
    }
}
