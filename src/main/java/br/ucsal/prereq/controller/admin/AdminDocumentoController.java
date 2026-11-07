package br.ucsal.prereq.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.ucsal.prereq.dto.DocumentoForm;
import br.ucsal.prereq.exception.RegraNegocioException;
import br.ucsal.prereq.service.DocumentoService;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin/documentos")
public class AdminDocumentoController {

    private static final String VIEW_LISTA = "admin/documentos/lista";
    private static final String VIEW_FORM = "admin/documentos/form";
    private static final String REDIRECT_LISTA = "redirect:/admin/documentos";

    private final DocumentoService service;

    public AdminDocumentoController(DocumentoService service) {
        this.service = service;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("documentos", service.listar());
        return VIEW_LISTA;
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("form", new DocumentoForm());
        return VIEW_FORM;
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("form", new DocumentoForm(service.buscar(id)));
        return VIEW_FORM;
    }

    @PostMapping
    public String salvar(@Valid @ModelAttribute("form") DocumentoForm form, BindingResult result,
            RedirectAttributes redirect) {
        if (result.hasErrors()) {
            return VIEW_FORM;
        }
        try {
            service.salvar(form);
        } catch (RegraNegocioException e) {
            result.rejectValue("nome", "duplicado", e.getMessage());
            return VIEW_FORM;
        }
        redirect.addFlashAttribute("sucesso", "Documento salvo.");
        return REDIRECT_LISTA;
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            service.excluir(id);
            redirect.addFlashAttribute("sucesso", "Documento excluído.");
        } catch (RegraNegocioException e) {
            redirect.addFlashAttribute("erro", e.getMessage());
        }
        return REDIRECT_LISTA;
    }
}
