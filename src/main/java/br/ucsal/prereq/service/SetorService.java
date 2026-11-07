package br.ucsal.prereq.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.ucsal.prereq.dto.SetorForm;
import br.ucsal.prereq.exception.RecursoNaoEncontradoException;
import br.ucsal.prereq.exception.RegraNegocioException;
import br.ucsal.prereq.model.Setor;
import br.ucsal.prereq.model.TipoRequerimento;
import br.ucsal.prereq.repository.SetorRepository;
import br.ucsal.prereq.repository.TipoRequerimentoRepository;

@Service
@Transactional(readOnly = true)
public class SetorService {

    private final SetorRepository repository;
    private final TipoRequerimentoRepository tipoRepository;

    public SetorService(SetorRepository repository, TipoRequerimentoRepository tipoRepository) {
        this.repository = repository;
        this.tipoRepository = tipoRepository;
    }

    public List<Setor> listar() {
        return repository.findAllByOrderByNome();
    }

    public Setor buscar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Setor não encontrado com o ID: " + id));
    }

    @Transactional
    public Setor salvar(SetorForm form) {
        String nome = form.getNome().trim();
        Setor setor;
        if (form.getId() == null) {
            if (repository.existsByNomeIgnoreCase(nome)) {
                throw new RegraNegocioException("Já existe um setor com esse nome.");
            }
            setor = new Setor(nome, form.getDescricao());
        } else {
            setor = buscar(form.getId());
            if (repository.existsByNomeIgnoreCaseAndIdNot(nome, form.getId())) {
                throw new RegraNegocioException("Já existe um setor com esse nome.");
            }
            setor.setNome(nome);
            setor.setDescricao(form.getDescricao());
        }
        return repository.save(setor);
    }

    @Transactional
    public void excluir(Long id) {
        Setor setor = buscar(id);
        List<TipoRequerimento> categorias = tipoRepository.findBySetoresContaining(setor);
        if (!categorias.isEmpty()) {
            throw new RegraNegocioException(mensagemEmUso(categorias));
        }
        try {
            repository.deleteById(id);
            repository.flush();
        } catch (DataIntegrityViolationException e) {
            throw new RegraNegocioException("Este setor está em uso e não pode ser excluído.");
        }
    }

    private String mensagemEmUso(List<TipoRequerimento> categorias) {
        String nomes = categorias.stream()
                .map(TipoRequerimento::getNomeNoPortal)
                .collect(Collectors.joining(", "));
        if (categorias.size() == 1) {
            return "Este setor é usado pela categoria " + nomes + ".";
        }
        return "Este setor é usado pelas categorias " + nomes + ".";
    }
}
