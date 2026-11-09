package br.ucsal.prereq.dto;

import br.ucsal.prereq.model.OrigemRegra;
import br.ucsal.prereq.model.RegraTriagem;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegraForm {

    private Long id;

    @NotBlank(message = "Informe as palavras-chave.")
    @Size(max = 1000, message = "As palavras-chave devem ter no máximo 1000 caracteres.")
    private String palavrasChave;

    private Long categoriaId;

    @Size(max = 2000, message = "O canal alternativo deve ter no máximo 2000 caracteres.")
    private String canalAlternativo;

    @Size(max = 2000, message = "A explicação deve ter no máximo 2000 caracteres.")
    private String explicacao;

    private int prioridade;

    private OrigemRegra origem;

    private Long indeferimentoId;

    public RegraForm() {
    }

    public RegraForm(RegraTriagem regra) {
        this.id = regra.getId();
        this.palavrasChave = regra.getPalavrasChave();
        this.categoriaId = regra.getCategoria() == null ? null : regra.getCategoria().getId();
        this.canalAlternativo = regra.getCanalAlternativo();
        this.explicacao = regra.getExplicacao();
        this.prioridade = regra.getPrioridade();
        this.origem = regra.getOrigem();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPalavrasChave() {
        return palavrasChave;
    }

    public void setPalavrasChave(String palavrasChave) {
        this.palavrasChave = palavrasChave;
    }

    public Long getCategoriaId() {
        return categoriaId;
    }

    public void setCategoriaId(Long categoriaId) {
        this.categoriaId = categoriaId;
    }

    public String getCanalAlternativo() {
        return canalAlternativo;
    }

    public void setCanalAlternativo(String canalAlternativo) {
        this.canalAlternativo = canalAlternativo;
    }

    public String getExplicacao() {
        return explicacao;
    }

    public void setExplicacao(String explicacao) {
        this.explicacao = explicacao;
    }

    public int getPrioridade() {
        return prioridade;
    }

    public void setPrioridade(int prioridade) {
        this.prioridade = prioridade;
    }

    public OrigemRegra getOrigem() {
        return origem;
    }

    public void setOrigem(OrigemRegra origem) {
        this.origem = origem;
    }

    public Long getIndeferimentoId() {
        return indeferimentoId;
    }

    public void setIndeferimentoId(Long indeferimentoId) {
        this.indeferimentoId = indeferimentoId;
    }
}
