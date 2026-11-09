package br.ucsal.prereq.service.admin;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.ucsal.prereq.dto.RegraForm;
import br.ucsal.prereq.exception.RecursoNaoEncontradoException;
import br.ucsal.prereq.exception.RegraNegocioException;
import br.ucsal.prereq.model.OrigemRegra;
import br.ucsal.prereq.model.RegraTriagem;
import br.ucsal.prereq.model.TipoRequerimento;
import br.ucsal.prereq.repository.RegraTriagemRepository;
import br.ucsal.prereq.repository.TipoRequerimentoRepository;
import br.ucsal.prereq.service.triagem.NormalizadorTexto;

@Service
@Transactional(readOnly = true)
public class RegraTriagemAdminService {

    private final RegraTriagemRepository repository;
    private final TipoRequerimentoRepository categoriaRepository;

    public RegraTriagemAdminService(RegraTriagemRepository repository,
            TipoRequerimentoRepository categoriaRepository) {
        this.repository = repository;
        this.categoriaRepository = categoriaRepository;
    }

    public List<RegraTriagem> listar(boolean somenteInativas) {
        return somenteInativas
                ? repository.findByAtivaFalseOrderByPrioridadeDescIdAsc()
                : repository.findAllByOrderByPrioridadeDescIdAsc();
    }

    public List<TipoRequerimento> listarCategoriasAtivas() {
        return categoriaRepository.findByAtivoTrueOrderByNomeNoPortal();
    }

    public RegraForm novoForm() {
        RegraForm form = new RegraForm();
        form.setOrigem(OrigemRegra.MANUAL);
        return form;
    }

    public RegraForm buscarForm(Long id) {
        return new RegraForm(buscar(id));
    }

    public RegraTriagem buscar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Regra não encontrada com o ID: " + id));
    }

    @Transactional
    public RegraTriagem salvar(RegraForm form) {
        String canal = form.getCanalAlternativo() == null ? "" : form.getCanalAlternativo().trim();
        boolean temCategoria = form.getCategoriaId() != null;
        if (temCategoria == !canal.isEmpty()) {
            throw new RegraNegocioException(temCategoria
                    ? "Informe a categoria ou o canal alternativo, não os dois."
                    : "Informe a categoria ou o canal alternativo.");
        }
        String palavras = normalizarPalavras(form.getPalavrasChave());

        RegraTriagem regra;
        if (form.getId() == null) {
            OrigemRegra origem = form.getOrigem() == null ? OrigemRegra.MANUAL : form.getOrigem();
            regra = new RegraTriagem(palavras, origem);
        } else {
            regra = buscar(form.getId());
            regra.setPalavrasChave(palavras);
        }
        regra.setCategoria(temCategoria ? buscarCategoria(form.getCategoriaId()) : null);
        regra.setCanalAlternativo(canal.isEmpty() ? null : canal);
        regra.setExplicacao(form.getExplicacao());
        regra.setPrioridade(form.getPrioridade());
        regra.setDataRevisao(LocalDate.now());
        return repository.save(regra);
    }

    @Transactional
    public void desativar(Long id) {
        RegraTriagem regra = buscar(id);
        regra.setAtiva(false);
        repository.save(regra);
    }

    private TipoRequerimento buscarCategoria(Long id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Categoria não encontrada com o ID: " + id));
    }

    private String normalizarPalavras(String palavrasChave) {
        Set<String> termos = new LinkedHashSet<>();
        for (String termo : palavrasChave.split(",")) {
            String normalizado = NormalizadorTexto.normalizar(termo);
            if (!normalizado.isEmpty()) {
                termos.add(normalizado);
            }
        }
        if (termos.isEmpty()) {
            throw new RegraNegocioException("Informe ao menos uma palavra-chave válida.");
        }
        return String.join(", ", termos);
    }
}
