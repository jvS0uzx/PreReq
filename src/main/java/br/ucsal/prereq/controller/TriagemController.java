package br.ucsal.prereq.controller;

import br.ucsal.prereq.dto.TriagemForm;
import br.ucsal.prereq.service.triagem.ResultadoTriagem;
import br.ucsal.prereq.service.triagem.TriagemService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/triagem")
public class TriagemController {

    private final TriagemService service;

    public TriagemController(TriagemService service) {
        this.service = service;
    }

    @GetMapping
    public String formulario(@RequestParam(value = "descricao", required = false) String descricao, Model model) {
        model.addAttribute("triagemForm", new TriagemForm(descricao));
        return "triagem/formulario";
    }

    @PostMapping
    public String triar(@Valid @ModelAttribute("triagemForm") TriagemForm form,
                        BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "triagem/formulario";
        }
        ResultadoTriagem resultado = service.triar(form.getDescricao());
        model.addAttribute("resultado", resultado);
        model.addAttribute("descricao", form.getDescricao());
        return "triagem/resultado";
    }
}
