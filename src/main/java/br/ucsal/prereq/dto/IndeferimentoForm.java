package br.ucsal.prereq.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class IndeferimentoForm {

    @NotNull(message = "Escolha a categoria que você usou no portal.")
    private Long categoriaEscolhidaId;

    @NotBlank(message = "Conte o que você precisava resolver.")
    @Size(min = 10, max = 1000, message = "O relato deve ter entre 10 e 1000 caracteres.")
    private String necessidade;

    @NotBlank(message = "Informe o motivo que veio no indeferimento.")
    @Size(max = 1000, message = "O motivo deve ter no máximo 1000 caracteres.")
    private String motivoInformado;

    public IndeferimentoForm() {
    }

    public Long getCategoriaEscolhidaId() {
        return categoriaEscolhidaId;
    }

    public void setCategoriaEscolhidaId(Long categoriaEscolhidaId) {
        this.categoriaEscolhidaId = categoriaEscolhidaId;
    }

    public String getNecessidade() {
        return necessidade;
    }

    public void setNecessidade(String necessidade) {
        this.necessidade = necessidade;
    }

    public String getMotivoInformado() {
        return motivoInformado;
    }

    public void setMotivoInformado(String motivoInformado) {
        this.motivoInformado = motivoInformado;
    }
}
