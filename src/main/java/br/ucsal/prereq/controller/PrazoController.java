package br.ucsal.prereq.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.ucsal.prereq.dto.ConfirmacaoPrazoForm;
import br.ucsal.prereq.dto.ContribuicaoForm;
import br.ucsal.prereq.dto.EtapaPrazoForm;
import br.ucsal.prereq.exception.RegraNegocioException;
import br.ucsal.prereq.service.CategoriaService;
import br.ucsal.prereq.service.prazo.EtapaLida;
import br.ucsal.prereq.service.prazo.PreviaPrazo;
import br.ucsal.prereq.service.prazo.RegistroPrazoService;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/prazos")
public class PrazoController {

    private static final String VIEW_FORM = "prazos/contribuir";
    private static final String VIEW_PREVIA = "prazos/previa";
    private static final String REDIRECT_FORM = "redirect:/prazos/contribuir";

    private final RegistroPrazoService service;
    private final CategoriaService categoriaService;

    public PrazoController(RegistroPrazoService service, CategoriaService categoriaService) {
        this.service = service;
        this.categoriaService = categoriaService;
    }

    @GetMapping("/contribuir")
    public String formulario(Model model) {
        model.addAttribute("contribuicaoForm", new ContribuicaoForm());
        return abrirForm(model);
    }

    @PostMapping("/contribuir")
    public String previsualizar(@Valid @ModelAttribute("contribuicaoForm") ContribuicaoForm form,
            BindingResult result, Model model) {
        if (result.hasErrors()) {
            return abrirForm(model);
        }
        PreviaPrazo previa;
        try {
            previa = service.previsualizar(form.getCategoriaId(), form.getHistorico());
        } catch (RegraNegocioException e) {
            result.rejectValue("historico", "historico.invalido", e.getMessage());
            return abrirForm(model);
        }
        model.addAttribute("previa", previa);
        model.addAttribute("confirmacaoForm", montarConfirmacao(previa));
        return VIEW_PREVIA;
    }

    @PostMapping("/contribuir/confirmar")
    public String confirmar(@Valid @ModelAttribute("confirmacaoForm") ConfirmacaoPrazoForm form,
            BindingResult result, RedirectAttributes redirect) {
        if (result.hasErrors()) {
            redirect.addFlashAttribute("erro", "Não foi possível guardar o prazo. Cole o histórico de novo.");
            return REDIRECT_FORM;
        }
        try {
            service.confirmar(form);
        } catch (RegraNegocioException e) {
            redirect.addFlashAttribute("erro", e.getMessage());
            return REDIRECT_FORM;
        }
        redirect.addFlashAttribute("sucesso", "Prazo guardado. Obrigado por contribuir.");
        return REDIRECT_FORM;
    }

    private String abrirForm(Model model) {
        model.addAttribute("categorias", categoriaService.listarAtivasPorGrupo());
        return VIEW_FORM;
    }

    private ConfirmacaoPrazoForm montarConfirmacao(PreviaPrazo previa) {
        ConfirmacaoPrazoForm form = new ConfirmacaoPrazoForm();
        form.setCategoriaId(previa.categoria().getId());
        form.setResultado(previa.resultado());
        form.setTotalDias((int) previa.totalDias());
        form.setMesConclusao(previa.mesConclusao().toString());
        List<EtapaPrazoForm> etapas = new ArrayList<>();
        for (EtapaLida lida : previa.etapas()) {
            EtapaPrazoForm etapa = new EtapaPrazoForm();
            etapa.setSetor(lida.setor());
            etapa.setDias((int) lida.dias());
            etapas.add(etapa);
        }
        form.setEtapas(etapas);
        return form;
    }
}
