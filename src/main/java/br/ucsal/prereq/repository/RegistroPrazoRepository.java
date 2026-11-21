package br.ucsal.prereq.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.ucsal.prereq.model.RegistroPrazo;

public interface RegistroPrazoRepository extends JpaRepository<RegistroPrazo, Long> {
}
