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

import br.ucsal.prereq.dto.SetorForm;
import br.ucsal.prereq.exception.RecursoNaoEncontradoException;
import br.ucsal.prereq.exception.RegraNegocioException;
import br.ucsal.prereq.model.GrupoRequerimento;
import br.ucsal.prereq.model.Setor;
import br.ucsal.prereq.model.TipoRequerimento;
import br.ucsal.prereq.repository.SetorRepository;
import br.ucsal.prereq.repository.TipoRequerimentoRepository;

@ExtendWith(MockitoExtension.class)
class SetorServiceTest {

    @Mock
    private SetorRepository repository;

    @Mock
    private TipoRequerimentoRepository tipoRepository;

    @InjectMocks
    private SetorService service;

    @Test
    void deveLancarErroQuandoNomeDuplicadoNaCriacao() {
        when(repository.existsByNomeIgnoreCase("Secretaria")).thenReturn(true);

        assertThatThrownBy(() -> service.salvar(form(null, "Secretaria")))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessage("Já existe um setor com esse nome.");
        verify(repository, never()).save(any());
    }

    @Test
    void deveLancarErroQuandoNomeDuplicadoNaEdicao() {
        when(repository.findById(1L)).thenReturn(Optional.of(new Setor("Antigo", null)));
        when(repository.existsByNomeIgnoreCaseAndIdNot("Secretaria", 1L)).thenReturn(true);

        assertThatThrownBy(() -> service.salvar(form(1L, "Secretaria")))
                .isInstanceOf(RegraNegocioException.class);
    }

    @Test
    void devePermitirSalvarQuandoNomeForDoProprioRegistro() {
        Setor existente = new Setor("Secretaria", null);
        when(repository.findById(1L)).thenReturn(Optional.of(existente));
        when(repository.existsByNomeIgnoreCaseAndIdNot("Secretaria", 1L)).thenReturn(false);
        when(repository.save(existente)).thenReturn(existente);

        assertThatCode(() -> service.salvar(form(1L, " Secretaria "))).doesNotThrowAnyException();
        assertThat(existente.getNome()).isEqualTo("Secretaria");
    }

    @Test
    void deveMontarMensagemComNomeDaCategoriaQuandoExcluirSetorEmUso() {
        Setor setor = new Setor("Secretaria", null);
        when(repository.findById(1L)).thenReturn(Optional.of(setor));
        when(tipoRepository.findBySetoresContaining(setor)).thenReturn(List.of(categoria("Histórico")));

        assertThatThrownBy(() -> service.excluir(1L))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessage("Este setor é usado pela categoria Histórico.");
        verify(repository, never()).deleteById(any());
    }

    @Test
    void deveListarVariasCategoriasQuandoExcluirSetorEmUso() {
        Setor setor = new Setor("Secretaria", null);
        when(repository.findById(1L)).thenReturn(Optional.of(setor));
        when(tipoRepository.findBySetoresContaining(setor))
                .thenReturn(List.of(categoria("Histórico"), categoria("Declaração")));

        assertThatThrownBy(() -> service.excluir(1L))
                .hasMessage("Este setor é usado pelas categorias Histórico, Declaração.");
    }

    @Test
    void deveLancarErroQuandoBuscarSetorInexistente() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.buscar(99L))
                .isInstanceOf(RecursoNaoEncontradoException.class);
    }

    private SetorForm form(Long id, String nome) {
        SetorForm form = new SetorForm();
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
