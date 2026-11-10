package br.ucsal.prereq.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import br.ucsal.prereq.model.RegraTriagem;

public interface RegraTriagemRepository extends JpaRepository<RegraTriagem, Long> {

    @EntityGraph(attributePaths = "categoria")
    List<RegraTriagem> findByAtivaTrue();

    @EntityGraph(attributePaths = "categoria")
    List<RegraTriagem> findAllByOrderByPrioridadeDescIdAsc();

    @EntityGraph(attributePaths = "categoria")
    List<RegraTriagem> findByAtivaFalseOrderByPrioridadeDescIdAsc();
}
