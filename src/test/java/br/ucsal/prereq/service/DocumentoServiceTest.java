package br.ucsal.prereq.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.ucsal.prereq.dto.DocumentoForm;
import br.ucsal.prereq.exception.RecursoNaoEncontradoException;
import br.ucsal.prereq.exception.RegraNegocioException;
import br.ucsal.prereq.model.Documento;
import br.ucsal.prereq.model.GrupoRequerimento;
import br.ucsal.prereq.model.TipoRequerimento;
import br.ucsal.prereq.repository.DocumentoRepository;
import br.ucsal.prereq.repository.TipoRequerimentoRepository;

@ExtendWith(MockitoExtension.class)
class DocumentoServiceTest {

    @Mock
    private DocumentoRepository repository;

    @Mock
    private TipoRequerimentoRepository tipoRepository;

    @InjectMocks
    private DocumentoService service;

    @Test
    void deveLancarErroQuandoNomeDuplicadoNaCriacao() {
        when(repository.existsByNomeIgnoreCase("RG")).thenReturn(true);

        assertThatThrownBy(() -> service.salvar(form(null, "RG")))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessage("Já existe um documento com esse nome.");
        verify(repository, never()).save(any());
    }

    @Test
    void deveLancarErroQuandoNomeDuplicadoNaEdicao() {
        when(repository.findById(1L)).thenReturn(Optional.of(new Documento("Antigo", null, null)));
        when(repository.existsByNomeIgnoreCaseAndIdNot("RG", 1L)).thenReturn(true);

        assertThatThrownBy(() -> service.salvar(form(1L, "RG")))
                .isInstanceOf(RegraNegocioException.class);
    }

    @Test
    void devePermitirSalvarQuandoNomeForDoProprioRegistro() {
        Documento existente = new Documento("RG", null, null);
        when(repository.findById(1L)).thenReturn(Optional.of(existente));
        when(repository.existsByNomeIgnoreCaseAndIdNot("RG", 1L)).thenReturn(false);
        when(repository.save(existente)).thenReturn(existente);

        assertThatCode(() -> service.salvar(form(1L, " RG "))).doesNotThrowAnyException();
        assertThat(existente.getNome()).isEqualTo("RG");
    }

    @Test
    void deveMontarMensagemComNomeDaCategoriaQuandoExcluirDocumentoEmUso() {
        Documento documento = new Documento("RG", null, null);
        when(repository.findById(1L)).thenReturn(Optional.of(documento));
        when(tipoRepository.findByDocumentosContaining(documento)).thenReturn(List.of(categoria("Histórico")));

        assertThatThrownBy(() -> service.excluir(1L))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessage("Este documento é usado pela categoria Histórico.");
        verify(repository, never()).deleteById(any());
    }

    @Test
    void deveListarVariasCategoriasQuandoExcluirDocumentoEmUso() {
        Documento documento = new Documento("RG", null, null);
        when(repository.findById(1L)).thenReturn(Optional.of(documento));
        when(tipoRepository.findByDocumentosContaining(documento))
                .thenReturn(List.of(categoria("Histórico"), categoria("Declaração")));

        assertThatThrownBy(() -> service.excluir(1L))
                .hasMessage("Este documento é usado pelas categorias Histórico, Declaração.");
    }

    @Test
    void deveLancarErroQuandoBuscarDocumentoInexistente() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.buscar(99L))
                .isInstanceOf(RecursoNaoEncontradoException.class);
    }

    private DocumentoForm form(Long id, String nome) {
        DocumentoForm form = new DocumentoForm();
        form.setId(id);
        form.setNome(nome);
        return form;
    }

    private TipoRequerimento categoria(String nome) {
        TipoRequerimento categoria = TipoRequerimento.novo(GrupoRequerimento.DOCUMENTAL);
        categoria.setNomeNoPortal(nome);
        return categoria;
    }
}
