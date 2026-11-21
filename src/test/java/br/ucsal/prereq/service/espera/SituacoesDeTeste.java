package br.ucsal.prereq.service.espera;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import br.ucsal.prereq.model.GrupoRequerimento;
import br.ucsal.prereq.model.Setor;
import br.ucsal.prereq.model.TipoRequerimento;
import br.ucsal.prereq.service.prazo.PrazoReferencia;

final class SituacoesDeTeste {

    private SituacoesDeTeste() {
    }

    static SituacaoEspera situacao(GrupoRequerimento grupo, String setorAtual, long diasCorridos, Double p75) {
        TipoRequerimento categoria = TipoRequerimento.novo(grupo);
        categoria.setNomeNoPortal("CATEGORIA DO TESTE");
        Optional<Setor> setor = Optional.ofNullable(setorAtual).map(nome -> new Setor(nome, null));
        Optional<PrazoReferencia> prazo = Optional.ofNullable(p75)
                .map(dias -> new PrazoReferencia("P75", dias, List.of(), 4));
        return new SituacaoEspera(categoria, LocalDate.now().minusDays(diasCorridos), setor, diasCorridos, prazo);
    }
}
