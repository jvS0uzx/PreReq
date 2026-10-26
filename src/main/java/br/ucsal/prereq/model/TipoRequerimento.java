package br.ucsal.prereq.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OrderColumn;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "grupo")
public abstract class TipoRequerimento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nomeNoPortal;

    @Column(length = 2000)
    private String descricao;

    @Column(length = 2000)
    private String quandoUsar;

    @Column(length = 2000)
    private String quandoNaoUsar;

    @Column(length = 2000)
    private String efeitos;

    private LocalDate dataRevisao;

    private boolean ativo = true;

    @ManyToMany
    @JoinTable(name = "tipo_documento",
            joinColumns = @JoinColumn(name = "tipo_id"),
            inverseJoinColumns = @JoinColumn(name = "documento_id"))
    private List<Documento> documentos = new ArrayList<>();

    @ManyToMany
    @JoinTable(name = "tipo_setor",
            joinColumns = @JoinColumn(name = "tipo_id"),
            inverseJoinColumns = @JoinColumn(name = "setor_id"))
    @OrderColumn(name = "ordem")
    private List<Setor> setores = new ArrayList<>();

    protected TipoRequerimento() {
    }

    public static TipoRequerimento novo(GrupoRequerimento grupo) {
        return switch (grupo) {
            case ACADEMICO -> new RequerimentoAcademico();
            case FINANCEIRO -> new RequerimentoFinanceiro();
            case ESTAGIO -> new RequerimentoEstagio();
            case DOCUMENTAL -> new RequerimentoDocumental();
        };
    }

    public abstract GrupoRequerimento getGrupo();

    public abstract List<String> alertasDoGrupo();

    public Long getId() {
        return id;
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

    public LocalDate getDataRevisao() {
        return dataRevisao;
    }

    public void setDataRevisao(LocalDate dataRevisao) {
        this.dataRevisao = dataRevisao;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public List<Documento> getDocumentos() {
        return Collections.unmodifiableList(documentos);
    }

    public void setDocumentos(List<Documento> documentos) {
        this.documentos.clear();
        this.documentos.addAll(documentos);
    }

    public List<Setor> getSetores() {
        return Collections.unmodifiableList(setores);
    }

    public void setSetores(List<Setor> setores) {
        this.setores.clear();
        this.setores.addAll(setores);
    }
}
