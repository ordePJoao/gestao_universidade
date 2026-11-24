package br.edu.universidade.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Representa uma disciplina acadêmica oferecida pela universidade.
 */
public class Disciplina implements Serializable, Exibivel {

    private static final long serialVersionUID = 1L;

    private String codigo;
    private String nome;
    private int cargaHoraria;

    /**
     * Cria uma nova disciplina com código, nome e carga horária validados.
     *
     * @param codigo       código identificador da disciplina
     * @param nome         nome da disciplina (não pode ser vazio)
     * @param cargaHoraria carga horária em horas (deve ser maior que zero)
     */
    public Disciplina(String codigo, String nome, int cargaHoraria) {
        setCodigo(codigo);
        setNome(nome);
        setCargaHoraria(cargaHoraria);
    }

    public String getCodigo() {
        return codigo;
    }

    public final void setCodigo(String codigo) {
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException("Código da disciplina não pode ser vazio.");
        }
        this.codigo = codigo.trim();
    }

    public String getNome() {
        return nome;
    }

    public final void setNome(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Nome da disciplina não pode ser vazio.");
        }
        this.nome = nome.trim();
    }

    public int getCargaHoraria() {
        return cargaHoraria;
    }

    public final void setCargaHoraria(int cargaHoraria) {
        if (cargaHoraria <= 0) {
            throw new IllegalArgumentException("Carga horária deve ser maior que zero.");
        }
        this.cargaHoraria = cargaHoraria;
    }

    /**
     * Retorna os dados formatados da disciplina.
     */
    @Override
    public String exibirDados() {
        return String.format("Disciplina [%s] %s (%dh)", codigo, nome, cargaHoraria);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Disciplina that)) {
            return false;
        }
        return Objects.equals(codigo, that.codigo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(codigo);
    }

    @Override
    public String toString() {
        return codigo + " - " + nome;
    }
}
