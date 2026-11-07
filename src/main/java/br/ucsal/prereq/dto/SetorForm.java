package br.ucsal.prereq.dto;

import br.ucsal.prereq.model.Setor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class SetorForm {

    private Long id;

    @NotBlank(message = "Informe o nome do setor.")
    @Size(max = 255, message = "O nome deve ter no máximo 255 caracteres.")
    private String nome;

    @Size(max = 2000, message = "A descrição deve ter no máximo 2000 caracteres.")
    private String descricao;

    public SetorForm() {
    }

    public SetorForm(Setor setor) {
        this.id = setor.getId();
        this.nome = setor.getNome();
        this.descricao = setor.getDescricao();
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
}
