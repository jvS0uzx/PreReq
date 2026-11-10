package br.ucsal.prereq.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import br.ucsal.prereq.model.GrupoRequerimento;
import br.ucsal.prereq.model.TipoRequerimento;
import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CategoriaForm {

    private Long id;

    private GrupoRequerimento grupo;

    @NotBlank(message = "Informe o nome no portal.")
    @Size(max = 255, message = "O nome no portal deve ter no máximo 255 caracteres.")
    private String nomeNoPortal;

    @Size(max = 2000, message = "A descrição deve ter no máximo 2000 caracteres.")
    private String descricao;

    @Size(max = 2000, message = "O campo quando usar deve ter no máximo 2000 caracteres.")
    private String quandoUsar;

    @Size(max = 2000, message = "O campo quando não usar deve ter no máximo 2000 caracteres.")
    private String quandoNaoUsar;

    @Size(max = 2000, message = "Os efeitos devem ter no máximo 2000 caracteres.")
    private String efeitos;

    private List<Long> documentosIds = new ArrayList<>();

    @Valid
    private List<SetorOrdem> setores = new ArrayList<>();

    public CategoriaForm() {
    }

    public CategoriaForm(TipoRequerimento categoria) {
        this.id = categoria.getId();
        this.grupo = categoria.getGrupo();
        this.nomeNoPortal = categoria.getNomeNoPortal();
        this.descricao = categoria.getDescricao();
        this.quandoUsar = categoria.getQuandoUsar();
        this.quandoNaoUsar = categoria.getQuandoNaoUsar();
        this.efeitos = categoria.getEfeitos();
        categoria.getDocumentos().forEach(d -> documentosIds.add(d.getId()));
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public GrupoRequerimento getGrupo() {
        return grupo;
    }

    public void setGrupo(GrupoRequerimento grupo) {
        this.grupo = grupo;
    }

    public String getNomeNoPortal() {
        return nomeNoPortal;
    }

    public void setNomeNoPortal(String nomeNoPortal) {
        this.nomeNoPortal = nomeNoPortal;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getQuandoUsar() {
        return quandoUsar;
    }

    public void setQuandoUsar(String quandoUsar) {
        this.quandoUsar = quandoUsar;
    }

    public String getQuandoNaoUsar() {
        return quandoNaoUsar;
    }

    public void setQuandoNaoUsar(String quandoNaoUsar) {
        this.quandoNaoUsar = quandoNaoUsar;
    }

    public String getEfeitos() {
        return efeitos;
    }

    public void setEfeitos(String efeitos) {
        this.efeitos = efeitos;
    }

    public List<Long> getDocumentosIds() {
        return documentosIds;
    }

    public void setDocumentosIds(List<Long> documentosIds) {
        this.documentosIds = documentosIds;
    }

    public List<SetorOrdem> getSetores() {
        return setores;
    }

    public void setSetores(List<SetorOrdem> setores) {
        this.setores = setores;
    }

    @AssertTrue(message = "Dois setores não podem ter a mesma ordem.")
    public boolean isOrdensDistintas() {
        List<Integer> ordens = setores.stream()
                .map(SetorOrdem::getOrdem)
                .filter(Objects::nonNull)
                .toList();
        return ordens.stream().distinct().count() == ordens.size();
    }

    public static class SetorOrdem {

        private Long setorId;

        @Min(value = 1, message = "A ordem deve ser maior que zero.")
        private Integer ordem;

        public SetorOrdem() {
        }

        public SetorOrdem(Long setorId, Integer ordem) {
            this.setorId = setorId;
            this.ordem = ordem;
        }

        public Long getSetorId() {
            return setorId;
        }

        public void setSetorId(Long setorId) {
            this.setorId = setorId;
        }

        public Integer getOrdem() {
            return ordem;
        }

        public void setOrdem(Integer ordem) {
            this.ordem = ordem;
        }
    }
}
