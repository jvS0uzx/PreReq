package br.ucsal.prereq.service.triagem;

import br.ucsal.prereq.model.RegraTriagem;
import br.ucsal.prereq.repository.RegraTriagemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class TriagemService {

    private final RegraTriagemRepository repository;

    public TriagemService(RegraTriagemRepository repository) {
        this.repository = repository;
    }

    public ResultadoTriagem triar(String descricao) {
        List<RegraTriagem> regras = repository.findByAtivaTrue();
        EtapaTriagem inicio = new EtapaRegraComCategoria(regras);
        inicio.encadear(new EtapaRegraSemCategoria(regras))
                .encadear(new EtapaPadrao());
        return inicio.avaliar(NormalizadorTexto.normalizar(descricao));
    }
}
