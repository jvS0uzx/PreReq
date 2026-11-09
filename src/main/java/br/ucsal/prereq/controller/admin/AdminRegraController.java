package br.ucsal.prereq.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.ucsal.prereq.dto.RegraForm;
import br.ucsal.prereq.exception.RegraNegocioException;
import br.ucsal.prereq.model.OrigemRegra;
import br.ucsal.prereq.service.admin.RegraTriagemAdminService;
import br.ucsal.prereq.service.triagem.TriagemService;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin/regras")
public class AdminRegraController {

    private static final String VIEW_LISTA = "admin/regras/lista";
    private static final String VIEW_FORM = "admin/regras/form";
    private static final String REDIRECT_LISTA = "redirect:/admin/regras";

    private final RegraTriagemAdminService service;
    private final TriagemService triagemService;

    public AdminRegraController(RegraTriagemAdminService service, TriagemService triagemService) {
        this.service = service;
        this.triagemService = triagemService;
    }

    @GetMapping
    public String listar(@RequestParam(defaultValue = "false") boolean inativas, Model model) {
        return abrirLista(inativas, model);
    }

    @PostMapping("/testar")
    public String testar(@RequestParam(defaultValue = "") String frase, Model model) {
        model.addAttribute("frase", frase);
        model.addAttribute("resultado", triagemService.triar(frase));
        return abrirLista(false, model);
    }

    @GetMapping("/novo")
    public String novo(@RequestParam(required = false) Long indeferimento, Model model) {
        RegraForm form = service.novoForm();
        if (indeferimento != null) {
            form.setOrigem(OrigemRegra.INDEFERIMENTO);
            form.setIndeferimentoId(indeferimento);
        }
        model.addAttribute("form", form);
        return abrirForm(model);
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("form", service.buscarForm(id));
        return abrirForm(model);
    }

    @PostMapping
    public String salvar(@Valid @ModelAttribute("form") RegraForm form, BindingResult result,
            Model model, RedirectAttributes redirect) {
        if (result.hasErrors()) {
            return abrirForm(model);
        }
        try {
            service.salvar(form);
        } catch (RegraNegocioException e) {
            result.reject("regra.invalida", e.getMessage());
            return abrirForm(model);
        }
        redirect.addFlashAttribute("sucesso", "Regra salva.");
        return REDIRECT_LISTA;
    }

    @PostMapping("/{id}/desativar")
    public String desativar(@PathVariable Long id, RedirectAttributes redirect) {
        service.desativar(id);
        redirect.addFlashAttribute("sucesso", "Regra desativada.");
        return REDIRECT_LISTA;
    }

    private String abrirLista(boolean inativas, Model model) {
        model.addAttribute("regras", service.listar(inativas));
        model.addAttribute("inativas", inativas);
        return VIEW_LISTA;
    }

    private String abrirForm(Model model) {
        model.addAttribute("categorias", service.listarCategoriasAtivas());
        return VIEW_FORM;
    }
}
