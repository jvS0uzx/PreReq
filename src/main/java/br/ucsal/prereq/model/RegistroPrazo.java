package br.ucsal.prereq.model;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;

@Entity
public class RegistroPrazo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "categoria_id")
    private TipoRequerimento categoria;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ResultadoPrazo resultado;

    private long totalDias;

    @Column(nullable = false, length = 7)
    private String mesConclusao;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "registro_id")
    @OrderBy("ordem")
    private List<EtapaPrazo> etapas = new ArrayList<>();

    protected RegistroPrazo() {
    }

    public RegistroPrazo(TipoRequerimento categoria, ResultadoPrazo resultado, long totalDias,
            YearMonth mesConclusao) {
        this.categoria = categoria;
        this.resultado = resultado;
        this.totalDias = totalDias;
        this.mesConclusao = mesConclusao.toString();
    }

    public Long getId() {
        return id;
    }

    public TipoRequerimento getCategoria() {
        return categoria;
    }

    public void setCategoria(TipoRequerimento categoria) {
        this.categoria = categoria;
    }

    public ResultadoPrazo getResultado() {
        return resultado;
    }

    public void setResultado(ResultadoPrazo resultado) {
        this.resultado = resultado;
    }

    public long getTotalDias() {
        return totalDias;
    }

    public void setTotalDias(long totalDias) {
        this.totalDias = totalDias;
    }

    public YearMonth getMesConclusao() {
        return YearMonth.parse(mesConclusao);
    }

    public void setMesConclusao(YearMonth mesConclusao) {
        this.mesConclusao = mesConclusao.toString();
    }

    public List<EtapaPrazo> getEtapas() {
        return Collections.unmodifiableList(etapas);
    }

    public void adicionarEtapa(EtapaPrazo etapa) {
        this.etapas.add(etapa);
    }
}
