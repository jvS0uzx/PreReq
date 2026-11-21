package br.ucsal.prereq.service.prazo;

import java.time.YearMonth;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.ucsal.prereq.dto.ConfirmacaoPrazoForm;
import br.ucsal.prereq.dto.EtapaPrazoForm;
import br.ucsal.prereq.exception.RecursoNaoEncontradoException;
import br.ucsal.prereq.exception.RegraNegocioException;
import br.ucsal.prereq.model.EtapaPrazo;
import br.ucsal.prereq.model.RegistroPrazo;
import br.ucsal.prereq.model.ResultadoPrazo;
import br.ucsal.prereq.model.Setor;
import br.ucsal.prereq.model.TipoRequerimento;
import br.ucsal.prereq.repository.RegistroPrazoRepository;
import br.ucsal.prereq.repository.SetorRepository;
import br.ucsal.prereq.repository.TipoRequerimentoRepository;

@Service
@Transactional(readOnly = true)
public class RegistroPrazoService {

    private final RegistroPrazoRepository repository;
    private final TipoRequerimentoRepository categoriaRepository;
    private final SetorRepository setorRepository;

    public RegistroPrazoService(RegistroPrazoRepository repository,
            TipoRequerimentoRepository categoriaRepository, SetorRepository setorRepository) {
        this.repository = repository;
        this.categoriaRepository = categoriaRepository;
        this.setorRepository = setorRepository;
    }

    public PreviaPrazo previsualizar(Long categoriaId, String texto) {
        TipoRequerimento categoria = buscarCategoria(categoriaId);
        ResultadoLeitura leitura = HistoricoParser.ler(texto);
        return new PreviaPrazo(categoria, ResultadoPrazo.valueOf(leitura.resultado().name()),
                leitura.totalDias(), YearMonth.from(leitura.conclusao()), leitura.etapas());
    }

    @Transactional
    public RegistroPrazo confirmar(ConfirmacaoPrazoForm form) {
        TipoRequerimento categoria = buscarCategoria(form.getCategoriaId());
        YearMonth mesConclusao = YearMonth.parse(form.getMesConclusao());
        if (mesConclusao.isAfter(YearMonth.now())) {
            throw new RegraNegocioException("O mês de conclusão não pode estar no futuro.");
        }
        int diasNosSetores = form.getEtapas().stream().mapToInt(EtapaPrazoForm::getDias).sum();
        if (diasNosSetores > form.getTotalDias()) {
            throw new RegraNegocioException("Os dias nos setores passam do total do pedido.");
        }

        RegistroPrazo registro = new RegistroPrazo(categoria, form.getResultado(), form.getTotalDias(),
                mesConclusao);
        int ordem = 1;
        for (EtapaPrazoForm etapa : form.getEtapas()) {
            Setor setor = setorRepository.findFirstByNomeStartingWithIgnoreCaseOrderByNome(etapa.getSetor())
                    .orElse(null);
            String naoCadastrado = setor == null ? etapa.getSetor() : null;
            registro.adicionarEtapa(new EtapaPrazo(setor, naoCadastrado, etapa.getDias(), ordem++));
        }
        return repository.save(registro);
    }

    private TipoRequerimento buscarCategoria(Long id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Categoria não encontrada com o ID: " + id));
    }
}
