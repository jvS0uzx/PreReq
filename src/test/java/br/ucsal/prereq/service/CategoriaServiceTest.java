package br.ucsal.prereq.service;

import br.ucsal.prereq.exception.RecursoNaoEncontradoException;
import br.ucsal.prereq.model.GrupoRequerimento;
import br.ucsal.prereq.model.TipoRequerimento;
import br.ucsal.prereq.repository.TipoRequerimentoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoriaServiceTest {

    @Mock
    private TipoRequerimentoRepository repository;

    @InjectMocks
    private CategoriaService service;

    private TipoRequerimento categoriaMock;

    @BeforeEach
    void setUp() {
        categoriaMock = TipoRequerimento.novo(GrupoRequerimento.ACADEMICO);
        categoriaMock.setNomeNoPortal("Histórico Escolar");
        categoriaMock.setAtivo(true);
    }

    @Test
    void deveListarAtivasPorGrupo() {
        when(repository.findByAtivoTrueOrderByNomeNoPortal()).thenReturn(List.of(categoriaMock));

        Map<GrupoRequerimento, List<TipoRequerimento>> resultado = service.listarAtivasPorGrupo();

        assertTrue(resultado.containsKey(GrupoRequerimento.ACADEMICO));
        assertEquals(1, resultado.get(GrupoRequerimento.ACADEMICO).size());
        assertEquals("Histórico Escolar", resultado.get(GrupoRequerimento.ACADEMICO).get(0).getNomeNoPortal());
    }

    @Test
    void deveBuscarPorNome() {
        when(repository.findByNomeNoPortalContainingIgnoreCase("histórico"))
                .thenReturn(List.of(categoriaMock));

        List<TipoRequerimento> resultado = service.buscarPorNome("histórico");

        assertFalse(resultado.isEmpty());
        assertEquals("Histórico Escolar", resultado.get(0).getNomeNoPortal());
    }

    @Test
    void deveBuscarPorIdComSucesso() {
        when(repository.findById(1L)).thenReturn(Optional.of(categoriaMock));

        TipoRequerimento resultado = service.buscar(1L);

        assertNotNull(resultado);
        assertEquals("Histórico Escolar", resultado.getNomeNoPortal());
    }

    @Test
    void deveLancarExcecaoQuandoIdNaoExistir() {
        when(repository.findById(999999L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> service.buscar(999999L));
    }
}
