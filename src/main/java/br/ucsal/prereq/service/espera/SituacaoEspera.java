package br.ucsal.prereq.service.espera;

import java.time.LocalDate;
import java.util.Optional;

import br.ucsal.prereq.model.Setor;
import br.ucsal.prereq.model.TipoRequerimento;
import br.ucsal.prereq.service.prazo.PrazoReferencia;

public record SituacaoEspera(TipoRequerimento categoria, LocalDate dataAbertura, Optional<Setor> setorAtual,
        long diasCorridos, Optional<PrazoReferencia> prazo) {}
