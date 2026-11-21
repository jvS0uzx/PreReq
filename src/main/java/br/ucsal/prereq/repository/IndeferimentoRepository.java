package br.ucsal.prereq.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import br.ucsal.prereq.model.Indeferimento;
import br.ucsal.prereq.model.StatusIndeferimento;

public interface IndeferimentoRepository extends JpaRepository<Indeferimento, Long> {

    @EntityGraph(attributePaths = "categoriaEscolhida")
    List<Indeferimento> findByStatusOrderByDataRegistroAscIdAsc(StatusIndeferimento status);

    @EntityGraph(attributePaths = "categoriaEscolhida")
    Optional<Indeferimento> findWithCategoriaEscolhidaById(Long id);
}
