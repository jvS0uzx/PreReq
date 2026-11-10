package br.ucsal.prereq.service.admin;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.ucsal.prereq.dto.CategoriaForm;
import br.ucsal.prereq.dto.CategoriaForm.SetorOrdem;
import br.ucsal.prereq.exception.RecursoNaoEncontradoException;
import br.ucsal.prereq.exception.RegraNegocioException;
import br.ucsal.prereq.model.Documento;
import br.ucsal.prereq.model.Setor;
import br.ucsal.prereq.model.TipoRequerimento;
import br.ucsal.prereq.repository.DocumentoRepository;
import br.ucsal.prereq.repository.SetorRepository;
import br.ucsal.prereq.repository.TipoRequerimentoRepository;

@Service
@Transactional(readOnly = true)
public class CategoriaAdminService {

    private final TipoRequerimentoRepository repository;
    private final SetorRepository setorRepository;
    private final DocumentoRepository documentoRepository;

    public CategoriaAdminService(TipoRequerimentoRepository repository, SetorRepository setorRepository,
            DocumentoRepository documentoRepository) {
        this.repository = repository;
        this.setorRepository = setorRepository;
        this.documentoRepository = documentoRepository;
    }

    public List<TipoRequerimento> listar() {
        return repository.findAllByOrderByNomeNoPortal();
    }

    public List<Setor> listarSetores() {
        return setorRepository.findAllByOrderByNome();
    }

    public List<Documento> listarDocumentos() {
        return documentoRepository.findAllByOrderByNome();
    }

    public CategoriaForm novoForm() {
        CategoriaForm form = new CategoriaForm();
        for (Setor setor : listarSetores()) {
            form.getSetores().add(new SetorOrdem(setor.getId(), null));
        }
        return form;
    }

    public CategoriaForm buscarForm(Long id) {
        TipoRequerimento categoria = buscar(id);
        CategoriaForm form = new CategoriaForm(categoria);
        List<Long> tramitacao = categoria.getSetores().stream().map(Setor::getId).toList();
        for (Setor setor : listarSetores()) {
            int posicao = tramitacao.indexOf(setor.getId());
            form.getSetores().add(new SetorOrdem(setor.getId(), posicao < 0 ? null : posicao + 1));
        }
        return form;
    }

    public TipoRequerimento buscar(Long id) {
        TipoRequerimento categoria = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Categoria não encontrada com o ID: " + id));
        categoria.getDocumentos().size();
        categoria.getSetores().size();
        return categoria;
    }

    @Transactional
    public TipoRequerimento salvar(CategoriaForm form) {
        String nome = form.getNomeNoPortal().trim();
        TipoRequerimento categoria;
        if (form.getId() == null) {
            if (repository.existsByNomeNoPortalIgnoreCase(nome)) {
                throw new RegraNegocioException("Já existe uma categoria com esse nome no portal.");
            }
            categoria = TipoRequerimento.novo(form.getGrupo());
        } else {
            categoria = buscar(form.getId());
            if (repository.existsByNomeNoPortalIgnoreCaseAndIdNot(nome, form.getId())) {
                throw new RegraNegocioException("Já existe uma categoria com esse nome no portal.");
            }
        }
        categoria.setNomeNoPortal(nome);
        categoria.setDescricao(form.getDescricao());
        categoria.setQuandoUsar(form.getQuandoUsar());
        categoria.setQuandoNaoUsar(form.getQuandoNaoUsar());
        categoria.setEfeitos(form.getEfeitos());
        categoria.setDocumentos(documentoRepository.findAllById(form.getDocumentosIds()));
        categoria.setSetores(setoresOrdenados(form.getSetores()));
        categoria.setDataRevisao(LocalDate.now());
        return repository.save(categoria);
    }

    @Transactional
    public void desativar(Long id) {
        TipoRequerimento categoria = buscar(id);
        categoria.setAtivo(false);
        repository.save(categoria);
    }

    private List<Setor> setoresOrdenados(List<SetorOrdem> escolhidos) {
        return escolhidos.stream()
                .filter(s -> s.getSetorId() != null && s.getOrdem() != null)
                .sorted(Comparator.comparing(SetorOrdem::getOrdem))
                .map(s -> setorRepository.findById(s.getSetorId())
                        .orElseThrow(() -> new RecursoNaoEncontradoException(
                                "Setor não encontrado com o ID: " + s.getSetorId())))
                .toList();
    }
}
