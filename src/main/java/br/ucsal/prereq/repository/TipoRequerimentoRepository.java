package br.ucsal.prereq.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.ucsal.prereq.model.Documento;
import br.ucsal.prereq.model.Setor;
import br.ucsal.prereq.model.TipoRequerimento;

public interface TipoRequerimentoRepository extends JpaRepository<TipoRequerimento, Long> {

    List<TipoRequerimento> findByAtivoTrueOrderByNomeNoPortal();

    Optional<TipoRequerimento> findByNomeNoPortal(String nomeNoPortal);

    List<TipoRequerimento> findByAtivoTrueAndNomeNoPortalContainingIgnoreCase(String nomeNoPortal);

    List<TipoRequerimento> findBySetoresContaining(Setor setor);

    List<TipoRequerimento> findByDocumentosContaining(Documento documento);
}
