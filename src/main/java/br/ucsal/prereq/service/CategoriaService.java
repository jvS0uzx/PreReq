package br.ucsal.prereq.service;

import br.ucsal.prereq.exception.RecursoNaoEncontradoException;
import br.ucsal.prereq.model.GrupoRequerimento;
import br.ucsal.prereq.model.TipoRequerimento;
import br.ucsal.prereq.repository.TipoRequerimentoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class CategoriaService {

    private final TipoRequerimentoRepository repository;

    public CategoriaService(TipoRequerimentoRepository repository) {
        this.repository = repository;
    }

    public Map<GrupoRequerimento, List<TipoRequerimento>> listarAtivasPorGrupo() {
        return repository.findByAtivoTrueOrderByNomeNoPortal().stream()
                .collect(Collectors.groupingBy(TipoRequerimento::getGrupo,
                        () -> new EnumMap<>(GrupoRequerimento.class), Collectors.toList()));
    }

    public List<TipoRequerimento> buscarPorNome(String q) {
        return repository.findByAtivoTrueAndNomeNoPortalContainingIgnoreCase(q);
    }

    public TipoRequerimento buscar(Long id) {
        TipoRequerimento categoria = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Categoria não encontrada com o ID: " + id));
        categoria.getDocumentos().size();
        categoria.getSetores().size();
        return categoria;
    }
}
