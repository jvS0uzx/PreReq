package br.ucsal.prereq.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import br.ucsal.prereq.model.Documento;
import br.ucsal.prereq.model.RequerimentoFinanceiro;
import br.ucsal.prereq.model.Setor;
import br.ucsal.prereq.model.TipoRequerimento;
import br.ucsal.prereq.repository.DocumentoRepository;
import br.ucsal.prereq.repository.RegraTriagemRepository;
import br.ucsal.prereq.repository.SetorRepository;
import br.ucsal.prereq.repository.TipoRequerimentoRepository;

@SpringBootTest
class CargaInicialTest {

    @Autowired
    private CargaInicial cargaInicial;

    @Autowired
    private SetorRepository setorRepository;

    @Autowired
    private DocumentoRepository documentoRepository;

    @Autowired
    private TipoRequerimentoRepository tipoRequerimentoRepository;

    @Autowired
    private RegraTriagemRepository regraTriagemRepository;

    @Test
    void deveCarregarTodosOsCsvQuandoSubirComBancoVazio() {
        assertThat(setorRepository.count()).isEqualTo(2);
        assertThat(documentoRepository.count()).isEqualTo(2);
        assertThat(tipoRequerimentoRepository.count()).isEqualTo(3);
        assertThat(regraTriagemRepository.count()).isEqualTo(2);
    }

    @Test
    void deveManterAsQuantidadesQuandoRodarACargaDeNovo() {
        cargaInicial.run();

        assertThat(setorRepository.count()).isEqualTo(2);
        assertThat(documentoRepository.count()).isEqualTo(2);
        assertThat(tipoRequerimentoRepository.count()).isEqualTo(3);
        assertThat(regraTriagemRepository.count()).isEqualTo(2);
    }

    @Test
    @Transactional
    void deveLigarDocumentosESetoresNaOrdemDoCsvQuandoCarregarCategoria() {
        TipoRequerimento reembolso = tipoRequerimentoRepository.findByNomeNoPortal("REEMBOLSO TODOS OS NIVEIS")
                .orElseThrow();

        assertThat(reembolso).isInstanceOf(RequerimentoFinanceiro.class);
        assertThat(reembolso.getDocumentos()).extracting(Documento::getNome)
                .containsExactly("Formulário de reembolso assinado");
        assertThat(reembolso.getSetores()).extracting(Setor::getNome)
                .containsExactly("Contas a Receber", "Coordenação do curso");
    }

    @Test
    @Transactional
    void deveCarregarCategoriaSemOSetorQuandoONomeNaoEstiverCadastrado() {
        TipoRequerimento historico = tipoRequerimentoRepository.findByNomeNoPortal("HISTÓRICO ESCOLAR - GRADUAÇÃO")
                .orElseThrow();

        assertThat(historico.getSetores()).isEmpty();
    }
}
