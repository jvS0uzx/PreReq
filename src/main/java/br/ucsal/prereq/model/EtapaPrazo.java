package br.ucsal.prereq.model;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class EtapaPrazo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "setor_id")
    private Setor setor;

    private String setorNaoCadastrado;

    private long dias;

    private int ordem;

    protected EtapaPrazo() {
    }

    public EtapaPrazo(Setor setor, String setorNaoCadastrado, long dias, int ordem) {
        this.setor = setor;
        this.setorNaoCadastrado = setorNaoCadastrado;
        this.dias = dias;
        this.ordem = ordem;
    }

    public Long getId() {
        return id;
    }

    public Setor getSetor() {
        return setor;
    }

    public void setSetor(Setor setor) {
        this.setor = setor;
    }

    public String getSetorNaoCadastrado() {
        return setorNaoCadastrado;
    }

    public void setSetorNaoCadastrado(String setorNaoCadastrado) {
        this.setorNaoCadastrado = setorNaoCadastrado;
    }

    public long getDias() {
        return dias;
    }

    public void setDias(long dias) {
        this.dias = dias;
    }

    public int getOrdem() {
        return ordem;
    }

    public void setOrdem(int ordem) {
        this.ordem = ordem;
    }
}
