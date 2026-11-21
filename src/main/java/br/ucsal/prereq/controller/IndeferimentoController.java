package br.ucsal.prereq.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.ucsal.prereq.dto.IndeferimentoForm;
import br.ucsal.prereq.service.CategoriaService;
import br.ucsal.prereq.service.IndeferimentoService;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/indeferimentos")
public class IndeferimentoController {

    private static final String VIEW_FORM = "indeferimentos/novo";

    private final IndeferimentoService service;
    private final CategoriaService categoriaService;

    public IndeferimentoController(IndeferimentoService service, CategoriaService categoriaService) {
        this.service = service;
        this.categoriaService = categoriaService;
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("indeferimentoForm", new IndeferimentoForm());
        return abrirForm(model);
    }

    @PostMapping("/novo")
    public String registrar(@Valid @ModelAttribute("indeferimentoForm") IndeferimentoForm form,
            BindingResult result, Model model, RedirectAttributes redirect) {
        if (result.hasErrors()) {
            return abrirForm(model);
        }
        service.registrar(form);
        redirect.addFlashAttribute("sucesso",
                "Relato recebido. Ele vai ser analisado e pode virar uma regra da triagem.");
        return "redirect:/indeferimentos/novo";
    }

    private String abrirForm(Model model) {
        model.addAttribute("categorias", categoriaService.listarAtivasPorGrupo());
        return VIEW_FORM;
    }
}
