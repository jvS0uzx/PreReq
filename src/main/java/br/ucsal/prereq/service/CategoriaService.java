package br.ucsal.prereq.service;

import br.ucsal.prereq.exception.RecursoNaoEncontradoException;
import br.ucsal.prereq.model.GrupoRequerimento;
import br.ucsal.prereq.model.TipoRequerimento;
import br.ucsal.prereq.repository.TipoRequerimentoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CategoriaService {

    private final TipoRequerimentoRepository repository;

    public CategoriaService(TipoRequerimentoRepository repository) {
        this.repository = repository;
    }

    public Map<GrupoRequerimento, List<TipoRequerimento>> listarAtivasPorGrupo() {
        // Usa o método do P1 para buscar apenas as ativas e já ordenadas
        List<TipoRequerimento> ativas = repository.findByAtivoTrueOrderByNomeNoPortal();

        // Agrupa usando o método getGrupo() correto da entidade
        return ativas.stream()
                .collect(Collectors.groupingBy(TipoRequerimento::getGrupo));
    }

    public List<TipoRequerimento> buscarPorNome(String q) {
        // Usa o método que acabamos de adicionar no repositório
        return repository.findByNomeNoPortalContainingIgnoreCase(q);
    }

    public TipoRequerimento buscar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Categoria não encontrada com o ID: " + id));
    }
}
