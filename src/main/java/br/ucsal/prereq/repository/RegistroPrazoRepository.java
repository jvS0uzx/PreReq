package br.ucsal.prereq.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import br.ucsal.prereq.model.RegistroPrazo;

public interface RegistroPrazoRepository extends JpaRepository<RegistroPrazo, Long> {

    @EntityGraph(attributePaths = { "etapas", "etapas.setor" })
    List<RegistroPrazo> findByCategoriaId(Long categoriaId);

    @EntityGraph(attributePaths = { "categoria", "etapas", "etapas.setor" })
    List<RegistroPrazo> findAllByOrderByCategoriaNomeNoPortalAscIdAsc();
}
