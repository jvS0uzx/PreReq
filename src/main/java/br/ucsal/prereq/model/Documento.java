package br.ucsal.prereq.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Documento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nome;

    @Column(length = 2000)
    private String descricao;

    @Column(length = 2000)
    private String ondeConseguir;

    protected Documento() {
    }

    public Documento(String nome, String descricao, String ondeConseguir) {
        this.nome = nome;
        this.descricao = descricao;
        this.ondeConseguir = ondeConseguir;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getOndeConseguir() {
        return ondeConseguir;
    }

    public void setOndeConseguir(String ondeConseguir) {
        this.ondeConseguir = ondeConseguir;
    }
}
