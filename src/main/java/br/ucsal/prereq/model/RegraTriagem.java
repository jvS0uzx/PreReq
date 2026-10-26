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
public class RegraTriagem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 1000)
    private String palavrasChave;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id")
    private TipoRequerimento categoria;

    @Column(length = 2000)
    private String canalAlternativo;

    @Column(length = 2000)
    private String explicacao;

    private int prioridade;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrigemRegra origem;

    private boolean ativa = true;

    private LocalDate dataRevisao;

    protected RegraTriagem() {
    }

    public RegraTriagem(String palavrasChave, OrigemRegra origem) {
        this.palavrasChave = palavrasChave;
        this.origem = origem;
    }

    public Long getId() {
        return id;
    }

    public String getPalavrasChave() {
        return palavrasChave;
    }

    public void setPalavrasChave(String palavrasChave) {
        this.palavrasChave = palavrasChave;
    }

    public TipoRequerimento getCategoria() {
        return categoria;
    }

    public void setCategoria(TipoRequerimento categoria) {
        this.categoria = categoria;
    }

    public String getCanalAlternativo() {
        return canalAlternativo;
    }

    public void setCanalAlternativo(String canalAlternativo) {
        this.canalAlternativo = canalAlternativo;
    }

    public String getExplicacao() {
        return explicacao;
    }

    public void setExplicacao(String explicacao) {
        this.explicacao = explicacao;
    }

    public int getPrioridade() {
        return prioridade;
    }

    public void setPrioridade(int prioridade) {
        this.prioridade = prioridade;
    }

    public OrigemRegra getOrigem() {
        return origem;
    }

    public void setOrigem(OrigemRegra origem) {
        this.origem = origem;
    }

    public boolean isAtiva() {
        return ativa;
    }

    public void setAtiva(boolean ativa) {
        this.ativa = ativa;
    }

    public LocalDate getDataRevisao() {
        return dataRevisao;
    }

    public void setDataRevisao(LocalDate dataRevisao) {
        this.dataRevisao = dataRevisao;
    }
}
