package br.ucsal.prereq.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ContribuicaoForm {

    @NotNull(message = "Escolha a categoria do pedido.")
    private Long categoriaId;

    @NotBlank(message = "Cole o histórico de etapas do protocolo.")
    @Size(max = 5000, message = "O histórico deve ter no máximo 5000 caracteres.")
    private String historico;

    public ContribuicaoForm() {
    }

    public Long getCategoriaId() {
        return categoriaId;
    }

    public void setCategoriaId(Long categoriaId) {
        this.categoriaId = categoriaId;
    }

    public String getHistorico() {
        return historico;
    }

    public void setHistorico(String historico) {
        this.historico = historico;
    }
}
