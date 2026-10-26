package br.ucsal.prereq.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.ucsal.prereq.model.TipoRequerimento;

public interface TipoRequerimentoRepository extends JpaRepository<TipoRequerimento, Long> {

    List<TipoRequerimento> findByAtivoTrueOrderByNomeNoPortal();

    Optional<TipoRequerimento> findByNomeNoPortal(String nomeNoPortal);
}
