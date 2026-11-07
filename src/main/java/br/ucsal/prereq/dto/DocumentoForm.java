package br.ucsal.prereq.dto;

import br.ucsal.prereq.model.Documento;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class DocumentoForm {

    private Long id;

    @NotBlank(message = "Informe o nome do documento.")
    @Size(max = 255, message = "O nome deve ter no máximo 255 caracteres.")
    private String nome;

    @Size(max = 2000, message = "A descrição deve ter no máximo 2000 caracteres.")
    private String descricao;

    @Size(max = 2000, message = "O campo onde conseguir deve ter no máximo 2000 caracteres.")
    private String ondeConseguir;

    public DocumentoForm() {
    }

    public DocumentoForm(Documento documento) {
        this.id = documento.getId();
        this.nome = documento.getNome();
        this.descricao = documento.getDescricao();
        this.ondeConseguir = documento.getOndeConseguir();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getOndeConseguir() {
        return ondeConseguir;
    }

    public void setOndeConseguir(String ondeConseguir) {
        this.ondeConseguir = ondeConseguir;
    }
}
