package br.ucsal.prereq.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class DadosSolicitacao {

    @NotBlank(message = "O curso é obrigatório")
    @Size(max = 100)
    private String curso;

    @NotBlank(message = "O semestre é obrigatório")
    @Size(max = 20)
    private String semestre;

    @NotBlank(message = "O motivo é obrigatório")
    @Size(max = 1000)
    private String motivo;

    @Size(max = 100)
    private String disciplina;

    @Size(max = 100)
    private String empresaEstagio;

    public String getCurso() {
        return curso;
    }

    public void setCurso(String curso) {
        this.curso = curso;
    }

    public String getSemestre() {
        return semestre;
    }

    public void setSemestre(String semestre) {
        this.semestre = semestre;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public String getDisciplina() {
        return disciplina;
    }

    public void setDisciplina(String disciplina) {
        this.disciplina = disciplina;
    }

    public String getEmpresaEstagio() {
        return empresaEstagio;
    }

    public void setEmpresaEstagio(String empresaEstagio) {
        this.empresaEstagio = empresaEstagio;
    }
}
