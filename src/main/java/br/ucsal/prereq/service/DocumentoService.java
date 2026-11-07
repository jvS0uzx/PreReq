package br.ucsal.prereq.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.ucsal.prereq.dto.DocumentoForm;
import br.ucsal.prereq.exception.RecursoNaoEncontradoException;
import br.ucsal.prereq.exception.RegraNegocioException;
import br.ucsal.prereq.model.Documento;
import br.ucsal.prereq.model.TipoRequerimento;
import br.ucsal.prereq.repository.DocumentoRepository;
import br.ucsal.prereq.repository.TipoRequerimentoRepository;

@Service
@Transactional(readOnly = true)
public class DocumentoService {

    private final DocumentoRepository repository;
    private final TipoRequerimentoRepository tipoRepository;

    public DocumentoService(DocumentoRepository repository, TipoRequerimentoRepository tipoRepository) {
        this.repository = repository;
        this.tipoRepository = tipoRepository;
    }

    public List<Documento> listar() {
        return repository.findAllByOrderByNome();
    }

    public Documento buscar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Documento não encontrado com o ID: " + id));
    }

    @Transactional
    public Documento salvar(DocumentoForm form) {
        String nome = form.getNome().trim();
        Documento documento;
        if (form.getId() == null) {
            if (repository.existsByNomeIgnoreCase(nome)) {
                throw new RegraNegocioException("Já existe um documento com esse nome.");
            }
            documento = new Documento(nome, form.getDescricao(), form.getOndeConseguir());
        } else {
            documento = buscar(form.getId());
            if (repository.existsByNomeIgnoreCaseAndIdNot(nome, form.getId())) {
                throw new RegraNegocioException("Já existe um documento com esse nome.");
            }
            documento.setNome(nome);
            documento.setDescricao(form.getDescricao());
            documento.setOndeConseguir(form.getOndeConseguir());
        }
        return repository.save(documento);
    }

    @Transactional
    public void excluir(Long id) {
        Documento documento = buscar(id);
        List<TipoRequerimento> categorias = tipoRepository.findByDocumentosContaining(documento);
        if (!categorias.isEmpty()) {
            throw new RegraNegocioException(mensagemEmUso(categorias));
        }
        try {
            repository.deleteById(id);
            repository.flush();
        } catch (DataIntegrityViolationException e) {
            throw new RegraNegocioException("Este documento está em uso e não pode ser excluído.");
        }
    }

    private String mensagemEmUso(List<TipoRequerimento> categorias) {
        String nomes = categorias.stream()
                .map(TipoRequerimento::getNomeNoPortal)
                .collect(Collectors.joining(", "));
        if (categorias.size() == 1) {
            return "Este documento é usado pela categoria " + nomes + ".";
        }
        return "Este documento é usado pelas categorias " + nomes + ".";
    }
}
