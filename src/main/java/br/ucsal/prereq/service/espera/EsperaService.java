package br.ucsal.prereq.service.espera;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.ucsal.prereq.dto.EsperaForm;
import br.ucsal.prereq.exception.RecursoNaoEncontradoException;
import br.ucsal.prereq.exception.RegraNegocioException;
import br.ucsal.prereq.model.Setor;
import br.ucsal.prereq.model.TipoRequerimento;
import br.ucsal.prereq.repository.SetorRepository;
import br.ucsal.prereq.repository.TipoRequerimentoRepository;
import br.ucsal.prereq.service.prazo.CalculoPercentil;
import br.ucsal.prereq.service.prazo.CalculoPrazo;
import br.ucsal.prereq.service.prazo.PrazoReferenciaService;

@Service
@Transactional(readOnly = true)
public class EsperaService {

    private static final CalculoPrazo LIMITE_DO_USUAL = new CalculoPercentil(75);

    private final TipoRequerimentoRepository categoriaRepository;
    private final SetorRepository setorRepository;
    private final PrazoReferenciaService prazoService;
    private final VerificadorEspera verificador;

    public EsperaService(TipoRequerimentoRepository categoriaRepository, SetorRepository setorRepository,
            PrazoReferenciaService prazoService, VerificadorEspera verificador) {
        this.categoriaRepository = categoriaRepository;
        this.setorRepository = setorRepository;
        this.prazoService = prazoService;
        this.verificador = verificador;
    }

    public ResultadoEspera verificar(EsperaForm form) {
        LocalDate hoje = LocalDate.now();
        if (form.getDataAbertura().isAfter(hoje)) {
            throw new RegraNegocioException("A data de abertura não pode estar no futuro.");
        }
        TipoRequerimento categoria = categoriaRepository.findById(form.getCategoriaId())
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Categoria não encontrada com o ID: " + form.getCategoriaId()));
        Optional<Setor> setorAtual = form.getSetorId() == null
                ? Optional.empty()
                : Optional.of(setorRepository.findById(form.getSetorId())
                        .orElseThrow(() -> new RecursoNaoEncontradoException(
                                "Setor não encontrado com o ID: " + form.getSetorId())));

        SituacaoEspera situacao = new SituacaoEspera(categoria, form.getDataAbertura(), setorAtual,
                ChronoUnit.DAYS.between(form.getDataAbertura(), hoje),
                prazoService.porCategoria(categoria.getId(), LIMITE_DO_USUAL));
        return new ResultadoEspera(situacao, verificador.verificar(situacao));
    }
}
