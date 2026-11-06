package br.ucsal.prereq.service.triagem;

import br.ucsal.prereq.model.RegraTriagem;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class EtapaRegraSemCategoria extends EtapaTriagem {

    private final List<RegraTriagem> regras;

    public EtapaRegraSemCategoria(List<RegraTriagem> regras) {
        this.regras = regras.stream()
                .filter(RegraTriagem::isAtiva)
                .filter(regra -> regra.getCategoria() == null)
                .filter(regra -> regra.getCanalAlternativo() != null)
                .toList();
    }

    @Override
    protected Optional<ResultadoTriagem> tentar(String descricao) {
        return regras.stream()
                .map(regra -> RegraPontuada.de(regra, descricao))
                .filter(pontuada -> pontuada.pontos() > 0)
                .max(Comparator.comparingLong(RegraPontuada::pontos)
                        .thenComparingInt(pontuada -> pontuada.regra().getPrioridade()))
                .map(pontuada -> {
                    RegraTriagem regra = pontuada.regra();
                    return new ResultadoTriagem(null, regra.getCanalAlternativo(), regra,
                            regra.getExplicacao(), false);
                });
    }
}
