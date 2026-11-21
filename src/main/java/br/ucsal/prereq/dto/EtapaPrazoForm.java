package br.ucsal.prereq.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class EtapaPrazoForm {

    @NotBlank(message = "Informe o setor.")
    @Size(max = 100, message = "O setor deve ter no máximo 100 caracteres.")
    private String setor;

    @NotNull(message = "Informe os dias.")
    @Min(value = 0, message = "Os dias não podem ser negativos.")
    @Max(value = 3650, message = "Os dias passam do limite aceito.")
    private Integer dias;

    public EtapaPrazoForm() {
    }

    public String getSetor() {
        return setor;
    }

    public void setSetor(String setor) {
        this.setor = setor;
    }

    public Integer getDias() {
        return dias;
    }

    public void setDias(Integer dias) {
        this.dias = dias;
    }
}
