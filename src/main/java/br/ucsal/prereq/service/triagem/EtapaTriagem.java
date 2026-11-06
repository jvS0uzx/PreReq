package br.ucsal.prereq.service.triagem;

import java.util.Optional;

public abstract class EtapaTriagem {

    private EtapaTriagem proxima;

    public EtapaTriagem encadear(EtapaTriagem proxima) {
        this.proxima = proxima;
        return proxima;
    }

    public ResultadoTriagem avaliar(String descricaoNormalizada) {
        Optional<ResultadoTriagem> resultado = tentar(descricaoNormalizada);
        if (resultado.isPresent()) {
            return resultado.get();
        }
        if (proxima == null) {
            throw new IllegalStateException("A cadeia de triagem terminou sem uma etapa padrão");
        }
        return proxima.avaliar(descricaoNormalizada);
    }

    protected abstract Optional<ResultadoTriagem> tentar(String descricao);
}
