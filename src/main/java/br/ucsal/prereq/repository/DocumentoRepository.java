package br.ucsal.prereq.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.ucsal.prereq.model.Documento;

public interface DocumentoRepository extends JpaRepository<Documento, Long> {

    List<Documento> findAllByOrderByNome();

    boolean existsByNomeIgnoreCase(String nome);

    boolean existsByNomeIgnoreCaseAndIdNot(String nome, Long id);
}
