package br.ucsal.prereq.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import br.ucsal.prereq.dto.EsperaForm;
import br.ucsal.prereq.service.CategoriaService;
import br.ucsal.prereq.service.SetorService;
import br.ucsal.prereq.service.espera.EsperaService;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/espera")
public class EsperaController {

    private final EsperaService service;
    private final CategoriaService categoriaService;
    private final SetorService setorService;

    public EsperaController(EsperaService service, CategoriaService categoriaService,
            SetorService setorService) {
        this.service = service;
        this.categoriaService = categoriaService;
        this.setorService = setorService;
    }

    @GetMapping
    public String formulario(Model model) {
        model.addAttribute("esperaForm", new EsperaForm());
        return abrirForm(model);
    }

    @PostMapping
    public String verificar(@Valid @ModelAttribute("esperaForm") EsperaForm form, BindingResult result,
            Model model) {
        if (result.hasErrors()) {
            return abrirForm(model);
        }
        model.addAttribute("resultado", service.verificar(form));
        return "espera/resultado";
    }

    private String abrirForm(Model model) {
        model.addAttribute("categorias", categoriaService.listarAtivasPorGrupo());
        model.addAttribute("setores", setorService.listar());
        return "espera/formulario";
    }
}
