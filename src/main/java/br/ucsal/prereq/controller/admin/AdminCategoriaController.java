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

import br.ucsal.prereq.dto.CategoriaForm;
import br.ucsal.prereq.exception.RegraNegocioException;
import br.ucsal.prereq.model.GrupoRequerimento;
import br.ucsal.prereq.service.admin.CategoriaAdminService;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin/categorias")
public class AdminCategoriaController {

    private static final String VIEW_LISTA = "admin/categorias/lista";
    private static final String VIEW_FORM = "admin/categorias/form";
    private static final String REDIRECT_LISTA = "redirect:/admin/categorias";

    private final CategoriaAdminService service;

    public AdminCategoriaController(CategoriaAdminService service) {
        this.service = service;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("categorias", service.listar());
        return VIEW_LISTA;
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("form", service.novoForm());
        return abrirForm(model);
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("form", service.buscarForm(id));
        return abrirForm(model);
    }

    @PostMapping
    public String salvar(@Valid @ModelAttribute("form") CategoriaForm form, BindingResult result,
            Model model, RedirectAttributes redirect) {
        if (form.getId() == null && form.getGrupo() == null) {
            result.rejectValue("grupo", "obrigatorio", "Informe o grupo.");
        }
        if (result.hasErrors()) {
            return abrirForm(model);
        }
        try {
            service.salvar(form);
        } catch (RegraNegocioException e) {
            result.rejectValue("nomeNoPortal", "duplicado", e.getMessage());
            return abrirForm(model);
        }
        redirect.addFlashAttribute("sucesso", "Categoria salva.");
        return REDIRECT_LISTA;
    }

    @PostMapping("/{id}/desativar")
    public String desativar(@PathVariable Long id, RedirectAttributes redirect) {
        service.desativar(id);
        redirect.addFlashAttribute("sucesso", "Categoria desativada.");
        return REDIRECT_LISTA;
    }

    private String abrirForm(Model model) {
        model.addAttribute("grupos", GrupoRequerimento.values());
        model.addAttribute("todosSetores", service.listarSetores());
        model.addAttribute("todosDocumentos", service.listarDocumentos());
        return VIEW_FORM;
    }
}
