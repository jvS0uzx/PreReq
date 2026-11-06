package br.ucsal.prereq.service.triagem;

import java.util.Optional;

public class EtapaPadrao extends EtapaTriagem {

    static final String MENSAGEM = "Não identificamos a categoria. Procure o CRA ou a coordenação antes de abrir o pedido.";

    @Override
    protected Optional<ResultadoTriagem> tentar(String descricao) {
        return Optional.of(new ResultadoTriagem(null, null, null, MENSAGEM, false));
    }
}
