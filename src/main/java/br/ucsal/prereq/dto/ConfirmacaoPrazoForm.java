package br.ucsal.prereq.dto;

import java.util.ArrayList;
import java.util.List;

import br.ucsal.prereq.model.ResultadoPrazo;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class ConfirmacaoPrazoForm {

    @NotNull(message = "Escolha a categoria do pedido.")
    private Long categoriaId;

    @NotNull(message = "Informe o resultado.")
    private ResultadoPrazo resultado;

    @NotNull(message = "Informe o total de dias.")
    @Min(value = 0, message = "O total de dias não pode ser negativo.")
    @Max(value = 3650, message = "O total de dias passa do limite aceito.")
    private Integer totalDias;

    @NotNull(message = "Informe o mês de conclusão.")
    @Pattern(regexp = "\\d{4}-(0[1-9]|1[0-2])", message = "O mês de conclusão deve estar no formato aaaa-mm.")
    private String mesConclusao;

    @Valid
    @Size(max = 30, message = "O histórico tem etapas demais.")
    private List<EtapaPrazoForm> etapas = new ArrayList<>();

    public ConfirmacaoPrazoForm() {
    }

    public Long getCategoriaId() {
        return categoriaId;
    }

    public void setCategoriaId(Long categoriaId) {
        this.categoriaId = categoriaId;
    }

    public ResultadoPrazo getResultado() {
        return resultado;
    }

    public void setResultado(ResultadoPrazo resultado) {
        this.resultado = resultado;
    }

    public Integer getTotalDias() {
        return totalDias;
    }

    public void setTotalDias(Integer totalDias) {
        this.totalDias = totalDias;
    }

    public String getMesConclusao() {
        return mesConclusao;
    }

    public void setMesConclusao(String mesConclusao) {
        this.mesConclusao = mesConclusao;
    }

    public List<EtapaPrazoForm> getEtapas() {
        return etapas;
    }

    public void setEtapas(List<EtapaPrazoForm> etapas) {
        this.etapas = etapas;
    }
}
