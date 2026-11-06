package br.ucsal.prereq.service.triagem;

import br.ucsal.prereq.model.RegraTriagem;

import java.util.Arrays;

record RegraPontuada(RegraTriagem regra, long pontos) {

    static RegraPontuada de(RegraTriagem regra, String descricaoNormalizada) {
        long pontos = Arrays.stream(regra.getPalavrasChave().split(","))
                .map(NormalizadorTexto::normalizar)
                .filter(palavra -> !palavra.isEmpty())
                .filter(descricaoNormalizada::contains)
                .count();
        return new RegraPontuada(regra, pontos);
    }
}
