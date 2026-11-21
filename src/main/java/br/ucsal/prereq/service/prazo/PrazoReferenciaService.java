package br.ucsal.prereq.service.prazo;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.ucsal.prereq.model.EtapaPrazo;
import br.ucsal.prereq.model.RegistroPrazo;
import br.ucsal.prereq.repository.RegistroPrazoRepository;

@Service
@Transactional(readOnly = true)
public class PrazoReferenciaService {

    static final int MINIMO_DE_REGISTROS = 3;

    private final RegistroPrazoRepository repository;

    public PrazoReferenciaService(RegistroPrazoRepository repository) {
        this.repository = repository;
    }

    public Optional<PrazoReferencia> porCategoria(Long categoriaId, CalculoPrazo calculo) {
        return calcular(repository.findByCategoriaId(categoriaId), calculo);
    }

    public List<LinhaPrazo> listar(CalculoPrazo central, CalculoPrazo folga) {
        Map<Long, List<RegistroPrazo>> porCategoria = new LinkedHashMap<>();
        for (RegistroPrazo registro : repository.findAllByOrderByCategoriaNomeNoPortalAscIdAsc()) {
            porCategoria.computeIfAbsent(registro.getCategoria().getId(), id -> new ArrayList<>()).add(registro);
        }
        return porCategoria.values().stream()
                .map(registros -> new LinhaPrazo(registros.getFirst().getCategoria(), registros.size(),
                        calcular(registros, central), calcular(registros, folga)))
                .toList();
    }

    private Optional<PrazoReferencia> calcular(List<RegistroPrazo> registros, CalculoPrazo calculo) {
        if (registros.size() < MINIMO_DE_REGISTROS) {
            return Optional.empty();
        }
        List<Long> totais = registros.stream().map(RegistroPrazo::getTotalDias).toList();
        Map<String, List<Long>> diasPorSetor = new LinkedHashMap<>();
        for (RegistroPrazo registro : registros) {
            somarPorSetor(registro).forEach((setor, dias) ->
                    diasPorSetor.computeIfAbsent(setor, nome -> new ArrayList<>()).add(dias));
        }
        List<PrazoSetor> setores = diasPorSetor.entrySet().stream()
                .map(e -> new PrazoSetor(e.getKey(), calculo.calcular(e.getValue()), e.getValue().size()))
                .toList();
        return Optional.of(new PrazoReferencia(calculo.nome(), calculo.calcular(totais), setores,
                registros.size()));
    }

    private Map<String, Long> somarPorSetor(RegistroPrazo registro) {
        Map<String, Long> dias = new LinkedHashMap<>();
        for (EtapaPrazo etapa : registro.getEtapas()) {
            String setor = etapa.getSetor() != null ? etapa.getSetor().getNome() : etapa.getSetorNaoCadastrado();
            dias.merge(setor, etapa.getDias(), Long::sum);
        }
        return dias;
    }
}
