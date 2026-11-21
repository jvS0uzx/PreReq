package br.ucsal.prereq.controller;

import br.ucsal.prereq.dto.DadosSolicitacao;
import br.ucsal.prereq.model.TipoRequerimento;
import br.ucsal.prereq.service.CategoriaService;
import br.ucsal.prereq.service.prazo.CalculoMediana;
import br.ucsal.prereq.service.prazo.CalculoPercentil;
import br.ucsal.prereq.service.prazo.CalculoPrazo;
import br.ucsal.prereq.service.prazo.PrazoReferenciaService;
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

    private static final CalculoPrazo CENTRAL = new CalculoMediana();
    private static final CalculoPrazo FOLGA = new CalculoPercentil(75);

    private final CategoriaService service;
    private final PrazoReferenciaService prazoService;

    public CategoriaController(CategoriaService service, PrazoReferenciaService prazoService) {
        this.service = service;
        this.prazoService = prazoService;
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
        adicionarPrazo(id, model);
        return "categorias/detalhe";
    }

    @PostMapping("/{id}/modelo")
    public String gerarModelo(
            @PathVariable Long id,
            @Valid @ModelAttribute("dadosSolicitacao") DadosSolicitacao dados,
            BindingResult result,
            Model model) {

        TipoRequerimento categoria = service.buscar(id);
        adicionarPrazo(id, model);

        if (result.hasErrors()) {
            model.addAttribute("categoria", categoria);
            return "categorias/detalhe";
        }

        String textoGerado = categoria.montarModeloTexto(dados);
        model.addAttribute("categoria", categoria);
        model.addAttribute("textoGerado", textoGerado);

        return "categorias/detalhe";
    }

    private void adicionarPrazo(Long id, Model model) {
        model.addAttribute("prazoCentral", prazoService.porCategoria(id, CENTRAL).orElse(null));
        model.addAttribute("prazoFolga", prazoService.porCategoria(id, FOLGA).orElse(null));
    }
}
