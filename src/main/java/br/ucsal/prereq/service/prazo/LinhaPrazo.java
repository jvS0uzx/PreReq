package br.ucsal.prereq.service.prazo;

import java.util.Optional;

import br.ucsal.prereq.model.TipoRequerimento;

public record LinhaPrazo(TipoRequerimento categoria, int registros, Optional<PrazoReferencia> central,
        Optional<PrazoReferencia> folga) {}
