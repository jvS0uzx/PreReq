package br.ucsal.prereq.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.ucsal.prereq.dto.IndeferimentoForm;
import br.ucsal.prereq.exception.RecursoNaoEncontradoException;
import br.ucsal.prereq.exception.RegraNegocioException;
import br.ucsal.prereq.model.Indeferimento;
import br.ucsal.prereq.model.StatusIndeferimento;
import br.ucsal.prereq.model.TipoRequerimento;
import br.ucsal.prereq.repository.IndeferimentoRepository;
import br.ucsal.prereq.repository.TipoRequerimentoRepository;

@Service
@Transactional(readOnly = true)
public class IndeferimentoService {

    private final IndeferimentoRepository repository;
    private final TipoRequerimentoRepository categoriaRepository;

    public IndeferimentoService(IndeferimentoRepository repository,
            TipoRequerimentoRepository categoriaRepository) {
        this.repository = repository;
        this.categoriaRepository = categoriaRepository;
    }

    public List<Indeferimento> listarPendentes() {
        return repository.findByStatusOrderByDataRegistroAscIdAsc(StatusIndeferimento.PENDENTE);
    }

    public Indeferimento buscar(Long id) {
        return repository.findWithCategoriaEscolhidaById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Indeferimento não encontrado com o ID: " + id));
    }

    @Transactional
    public Indeferimento registrar(IndeferimentoForm form) {
        TipoRequerimento categoria = categoriaRepository.findById(form.getCategoriaEscolhidaId())
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Categoria não encontrada com o ID: " + form.getCategoriaEscolhidaId()));
        return repository.save(new Indeferimento(categoria, form.getNecessidade().trim(),
                form.getMotivoInformado().trim(), LocalDate.now()));
    }

    @Transactional
    public void descartar(Long id) {
        Indeferimento indeferimento = buscarPendente(id);
        indeferimento.setStatus(StatusIndeferimento.DESCARTADO);
        repository.save(indeferimento);
    }

    @Transactional
    public void marcarConvertido(Long id, TipoRequerimento categoriaCorreta) {
        Indeferimento indeferimento = buscarPendente(id);
        indeferimento.setStatus(StatusIndeferimento.CONVERTIDO);
        indeferimento.setCategoriaCorreta(categoriaCorreta);
        repository.save(indeferimento);
    }

    private Indeferimento buscarPendente(Long id) {
        Indeferimento indeferimento = buscar(id);
        if (indeferimento.getStatus() != StatusIndeferimento.PENDENTE) {
            throw new RegraNegocioException("Este indeferimento já foi tratado.");
        }
        return indeferimento;
    }
}
