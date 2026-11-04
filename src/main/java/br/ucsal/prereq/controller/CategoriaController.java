package br.ucsal.prereq.controller;

import br.ucsal.prereq.service.CategoriaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/categorias")
public class CategoriaController {

    private final CategoriaService service;

    public CategoriaController(CategoriaService service) {
        this.service = service;
    }

    @GetMapping
    public String listar(
            @RequestParam(value = "q", required = false) String q,
            Model model) {

        // Se o utilizador pesquisou algo, devolve o resultado da busca
        if (q != null && !q.trim().isEmpty()) {
            model.addAttribute("categoriasBusca", service.buscarPorNome(q));
            model.addAttribute("termoBusca", q);
        } else {
            // Se não pesquisou nada, devolve a lista completa agrupada
            model.addAttribute("categoriasAgrupadas", service.listarAtivasPorGrupo());
        }

        return "categorias/lista";
    }

    @GetMapping("/{id}")
    public String detalhe(@PathVariable Long id, Model model) {
        model.addAttribute("categoria", service.buscar(id));
        return "categorias/detalhe";
    }
}
