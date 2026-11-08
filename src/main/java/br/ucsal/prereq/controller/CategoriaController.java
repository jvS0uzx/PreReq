package br.ucsal.prereq.controller;

import br.ucsal.prereq.dto.DadosSolicitacao;
import br.ucsal.prereq.model.TipoRequerimento;
import br.ucsal.prereq.service.CategoriaService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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

        if (q != null && !q.trim().isEmpty()) {
            model.addAttribute("categoriasBusca", service.buscarPorNome(q));
            model.addAttribute("termoBusca", q);
        } else {
            model.addAttribute("categoriasAgrupadas", service.listarAtivasPorGrupo());
        }

        return "categorias/lista";
    }

    @GetMapping("/{id}")
    public String detalhe(@PathVariable Long id, Model model) {
        model.addAttribute("categoria", service.buscar(id));
        model.addAttribute("dadosSolicitacao", new DadosSolicitacao());
        return "categorias/detalhe";
    }

    @PostMapping("/{id}/modelo")
    public String gerarModelo(
            @PathVariable Long id,
            @Valid @ModelAttribute("dadosSolicitacao") DadosSolicitacao dados,
            BindingResult result,
            Model model) {

        TipoRequerimento categoria = service.buscar(id);

        if (result.hasErrors()) {
            model.addAttribute("categoria", categoria);
            return "categorias/detalhe";
        }

        String textoGerado = categoria.montarModeloTexto(dados);
        model.addAttribute("categoria", categoria);
        model.addAttribute("textoGerado", textoGerado);

        return "categorias/detalhe";
    }
}
