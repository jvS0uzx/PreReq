package br.ucsal.prereq.service.admin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import br.ucsal.prereq.dto.CategoriaForm;
import br.ucsal.prereq.dto.CategoriaForm.SetorOrdem;
import br.ucsal.prereq.exception.RegraNegocioException;
import br.ucsal.prereq.model.GrupoRequerimento;
import br.ucsal.prereq.model.Setor;
import br.ucsal.prereq.model.TipoRequerimento;
import br.ucsal.prereq.repository.DocumentoRepository;
import br.ucsal.prereq.repository.SetorRepository;
import br.ucsal.prereq.repository.TipoRequerimentoRepository;

@ExtendWith(MockitoExtension.class)
class CategoriaAdminServiceTest {

    @Mock
    private TipoRequerimentoRepository repository;

    @Mock
    private SetorRepository setorRepository;

    @Mock
    private DocumentoRepository documentoRepository;

    @InjectMocks
    private CategoriaAdminService service;

    @Test
    void deveOrdenarSetoresPelaOrdemInformadaQuandoSalvar() {
        Setor secretaria = new Setor("Secretaria", null);
        Setor financeiro = new Setor("Financeiro", null);
        Setor coordenacao = new Setor("Coordenação", null);
        when(setorRepository.findById(1L)).thenReturn(Optional.of(secretaria));
        when(setorRepository.findById(2L)).thenReturn(Optional.of(financeiro));
        when(setorRepository.findById(3L)).thenReturn(Optional.of(coordenacao));
        when(repository.save(any(TipoRequerimento.class))).thenAnswer(i -> i.getArgument(0));

        CategoriaForm form = form(null, "Trancamento", GrupoRequerimento.ACADEMICO);
        form.getSetores().add(new SetorOrdem(1L, 3));
        form.getSetores().add(new SetorOrdem(2L, 1));
        form.getSetores().add(new SetorOrdem(3L, 2));

        TipoRequerimento salva = service.salvar(form);

        assertThat(salva.getSetores()).containsExactly(financeiro, coordenacao, secretaria);
    }

    @Test
    void deveIgnorarSetorSemOrdemQuandoSalvar() {
        Setor secretaria = new Setor("Secretaria", null);
        when(setorRepository.findById(1L)).thenReturn(Optional.of(secretaria));
        when(repository.save(any(TipoRequerimento.class))).thenAnswer(i -> i.getArgument(0));

        CategoriaForm form = form(null, "Trancamento", GrupoRequerimento.ACADEMICO);
        form.getSetores().add(new SetorOrdem(1L, 1));
        form.getSetores().add(new SetorOrdem(2L, null));

        TipoRequerimento salva = service.salvar(form);

        assertThat(salva.getSetores()).containsExactly(secretaria);
    }

    @Test
    void deveCriarCategoriaDoGrupoInformadoQuandoSalvarNova() {
        when(repository.save(any(TipoRequerimento.class))).thenAnswer(i -> i.getArgument(0));

        TipoRequerimento salva = service.salvar(form(null, "Estágio", GrupoRequerimento.ESTAGIO));

        assertThat(salva.getGrupo()).isEqualTo(GrupoRequerimento.ESTAGIO);
    }

    @Test
    void deveManterGrupoQuandoEditar() {
        TipoRequerimento existente = TipoRequerimento.novo(GrupoRequerimento.FINANCEIRO);
        existente.setNomeNoPortal("Antigo");
        when(repository.findById(1L)).thenReturn(Optional.of(existente));
        when(repository.save(existente)).thenReturn(existente);

        TipoRequerimento salva = service.salvar(form(1L, "Novo nome", GrupoRequerimento.ACADEMICO));

        assertThat(salva.getGrupo()).isEqualTo(GrupoRequerimento.FINANCEIRO);
        assertThat(salva.getNomeNoPortal()).isEqualTo("Novo nome");
    }

    @Test
    void deveLancarErroQuandoNomeDuplicadoNaCriacao() {
        when(repository.existsByNomeNoPortalIgnoreCase("Trancamento")).thenReturn(true);

        assertThatThrownBy(() -> service.salvar(form(null, "Trancamento", GrupoRequerimento.ACADEMICO)))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessage("Já existe uma categoria com esse nome no portal.");
        verify(repository, never()).save(any());
    }

    @Test
    void deveLancarErroQuandoNomeDuplicadoNaEdicao() {
        TipoRequerimento existente = TipoRequerimento.novo(GrupoRequerimento.ACADEMICO);
        when(repository.findById(1L)).thenReturn(Optional.of(existente));
        when(repository.existsByNomeNoPortalIgnoreCaseAndIdNot("Trancamento", 1L)).thenReturn(true);

        assertThatThrownBy(() -> service.salvar(form(1L, "Trancamento", null)))
                .isInstanceOf(RegraNegocioException.class);
        verify(repository, never()).save(any());
    }

    @Test
    void deveAtualizarDataRevisaoQuandoSalvar() {
        TipoRequerimento existente = TipoRequerimento.novo(GrupoRequerimento.ACADEMICO);
        existente.setDataRevisao(LocalDate.of(2020, 1, 1));
        when(repository.findById(1L)).thenReturn(Optional.of(existente));
        when(repository.save(existente)).thenReturn(existente);

        TipoRequerimento salva = service.salvar(form(1L, "Trancamento", null));

        assertThat(salva.getDataRevisao()).isEqualTo(LocalDate.now());
    }

    @Test
    void deveDesativarSemExcluirQuandoDesativar() {
        TipoRequerimento existente = TipoRequerimento.novo(GrupoRequerimento.ACADEMICO);
        when(repository.findById(1L)).thenReturn(Optional.of(existente));

        service.desativar(1L);

        ArgumentCaptor<TipoRequerimento> captor = ArgumentCaptor.forClass(TipoRequerimento.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().isAtivo()).isFalse();
        verify(repository, never()).delete(any());
        verify(repository, never()).deleteById(any());
    }

    @Test
    void devePreencherOrdemDosSetoresDaCategoriaQuandoMontarFormDeEdicao() {
        Setor secretaria = setor("Secretaria", 1L);
        Setor financeiro = setor("Financeiro", 2L);
        TipoRequerimento existente = TipoRequerimento.novo(GrupoRequerimento.ACADEMICO);
        existente.setSetores(List.of(financeiro));
        when(repository.findById(1L)).thenReturn(Optional.of(existente));
        when(setorRepository.findAllByOrderByNome()).thenReturn(List.of(financeiro, secretaria));

        CategoriaForm form = service.buscarForm(1L);

        assertThat(form.getSetores()).hasSize(2);
        assertThat(form.getSetores().get(0).getSetorId()).isEqualTo(2L);
        assertThat(form.getSetores().get(0).getOrdem()).isEqualTo(1);
        assertThat(form.getSetores().get(1).getSetorId()).isEqualTo(1L);
        assertThat(form.getSetores().get(1).getOrdem()).isNull();
    }

    private Setor setor(String nome, Long id) {
        Setor setor = new Setor(nome, null);
        ReflectionTestUtils.setField(setor, "id", id);
        return setor;
    }

    private CategoriaForm form(Long id, String nome, GrupoRequerimento grupo) {
        CategoriaForm form = new CategoriaForm();
        form.setId(id);
        form.setNomeNoPortal(nome);
        form.setGrupo(grupo);
        return form;
    }
}
