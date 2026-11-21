package br.ucsal.prereq.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.ucsal.prereq.exception.RegraNegocioException;
import br.ucsal.prereq.service.IndeferimentoService;

@Controller
@RequestMapping("/admin/indeferimentos")
public class AdminIndeferimentoController {

    private static final String REDIRECT_LISTA = "redirect:/admin/indeferimentos";

    private final IndeferimentoService service;

    public AdminIndeferimentoController(IndeferimentoService service) {
        this.service = service;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("indeferimentos", service.listarPendentes());
        return "admin/indeferimentos/lista";
    }

    @PostMapping("/{id}/converter")
    public String converter(@PathVariable Long id) {
        service.buscar(id);
        return "redirect:/admin/regras/novo?indeferimento=" + id;
    }

    @PostMapping("/{id}/descartar")
    public String descartar(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            service.descartar(id);
            redirect.addFlashAttribute("sucesso", "Indeferimento descartado.");
        } catch (RegraNegocioException e) {
            redirect.addFlashAttribute("erro", e.getMessage());
        }
        return REDIRECT_LISTA;
    }
}
