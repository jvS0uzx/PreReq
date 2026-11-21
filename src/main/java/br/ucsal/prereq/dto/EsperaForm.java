package br.ucsal.prereq.dto;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

public class EsperaForm {

    @NotNull(message = "Escolha a categoria do pedido.")
    private Long categoriaId;

    @NotNull(message = "Informe a data em que você abriu o pedido.")
    @PastOrPresent(message = "A data de abertura não pode estar no futuro.")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dataAbertura;

    private Long setorId;

    public EsperaForm() {
    }

    public Long getCategoriaId() {
        return categoriaId;
    }

    public void setCategoriaId(Long categoriaId) {
        this.categoriaId = categoriaId;
    }

    public LocalDate getDataAbertura() {
        return dataAbertura;
    }

    public void setDataAbertura(LocalDate dataAbertura) {
        this.dataAbertura = dataAbertura;
    }

    public Long getSetorId() {
        return setorId;
    }

    public void setSetorId(Long setorId) {
        this.setorId = setorId;
    }
}
