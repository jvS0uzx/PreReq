package br.ucsal.prereq.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Indeferimento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "categoria_escolhida_id")
    private TipoRequerimento categoriaEscolhida;

    @Column(nullable = false, length = 1000)
    private String oQueOAlunoPrecisava;

    @Column(nullable = false, length = 1000)
    private String motivoInformado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_correta_id")
    private TipoRequerimento categoriaCorreta;

    @Column(nullable = false)
    private LocalDate dataRegistro;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusIndeferimento status = StatusIndeferimento.PENDENTE;

    protected Indeferimento() {
    }

    public Indeferimento(TipoRequerimento categoriaEscolhida, String oQueOAlunoPrecisava,
            String motivoInformado, LocalDate dataRegistro) {
        this.categoriaEscolhida = categoriaEscolhida;
        this.oQueOAlunoPrecisava = oQueOAlunoPrecisava;
        this.motivoInformado = motivoInformado;
        this.dataRegistro = dataRegistro;
    }

    public Long getId() {
        return id;
    }

    public TipoRequerimento getCategoriaEscolhida() {
        return categoriaEscolhida;
    }

    public void setCategoriaEscolhida(TipoRequerimento categoriaEscolhida) {
        this.categoriaEscolhida = categoriaEscolhida;
    }

    public String getOQueOAlunoPrecisava() {
        return oQueOAlunoPrecisava;
    }

    public void setOQueOAlunoPrecisava(String oQueOAlunoPrecisava) {
        this.oQueOAlunoPrecisava = oQueOAlunoPrecisava;
    }

    public String getMotivoInformado() {
        return motivoInformado;
    }

    public void setMotivoInformado(String motivoInformado) {
        this.motivoInformado = motivoInformado;
    }

    public TipoRequerimento getCategoriaCorreta() {
        return categoriaCorreta;
    }

    public void setCategoriaCorreta(TipoRequerimento categoriaCorreta) {
        this.categoriaCorreta = categoriaCorreta;
    }

    public LocalDate getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(LocalDate dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

    public StatusIndeferimento getStatus() {
        return status;
    }

    public void setStatus(StatusIndeferimento status) {
        this.status = status;
    }
}
