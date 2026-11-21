package br.ucsal.prereq.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.ucsal.prereq.model.Setor;

public interface SetorRepository extends JpaRepository<Setor, Long> {

    List<Setor> findAllByOrderByNome();

    boolean existsByNomeIgnoreCase(String nome);

    Optional<Setor> findFirstByNomeStartingWithIgnoreCaseOrderByNome(String nome);

    boolean existsByNomeIgnoreCaseAndIdNot(String nome, Long id);
}
