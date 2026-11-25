package br.edu.universidade.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Representa um curso de graduação, agregando suas disciplinas.
 */
public class Curso implements Serializable, Exibivel {

    private static final long serialVersionUID = 1L;

    private String codigo;
    private String nome;
    private int cargaHoraria;
    private final List<Disciplina> disciplinas;

    /**
     * Cria um novo curso com código, nome e carga horária validados.
     *
     * @param codigo       código identificador do curso
     * @param nome         nome do curso
     * @param cargaHoraria carga horária total em horas
     */
    public Curso(String codigo, String nome, int cargaHoraria) {
        setCodigo(codigo);
        setNome(nome);
        setCargaHoraria(cargaHoraria);
        this.disciplinas = new ArrayList<>();
    }

    public String getCodigo() {
        return codigo;
    }

    public final void setCodigo(String codigo) {
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException("Código do curso não pode ser vazio.");
        }
        this.codigo = codigo.trim();
    }

    public String getNome() {
        return nome;
    }

    public final void setNome(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Nome do curso não pode ser vazio.");
        }
        this.nome = nome.trim();
    }

    public int getCargaHoraria() {
        return cargaHoraria;
    }

    public final void setCargaHoraria(int cargaHoraria) {
        if (cargaHoraria <= 0) {
            throw new IllegalArgumentException("Carga horária do curso deve ser maior que zero.");
        }
        this.cargaHoraria = cargaHoraria;
    }

    /**
     * Retorna uma visão somente leitura da lista de disciplinas do curso.
     */
    public List<Disciplina> getDisciplinas() {
        return Collections.unmodifiableList(disciplinas);
    }

    /**
     * Agrega uma disciplina existente ao curso, evitando duplicatas.
     *
     * @param d disciplina a ser adicionada
     * @return true se a disciplina foi adicionada; false se já estava associada
     */
    public boolean adicionarDisciplina(Disciplina d) {
        if (d == null) {
            throw new IllegalArgumentException("Disciplina não pode ser nula.");
        }
        if (disciplinas.contains(d)) {
            return false;
        }
        return disciplinas.add(d);
    }

    /**
     * Remove a associação da disciplina com este curso sem destruir a instância de {@link Disciplina}.
     *
     * @param d disciplina a ser desvinculada
     * @return true se a disciplina estava no curso e foi removida
     */
    public boolean removerDisciplina(Disciplina d) {
        if (d == null) {
            return false;
        }
        return disciplinas.remove(d);
    }

    /**
     * Retorna os dados formatados do curso e a quantidade de disciplinas vinculadas.
     */
    @Override
    public String exibirDados() {
        return String.format("Curso [%s] %s (%dh) - %d disciplina(s)",
                codigo, nome, cargaHoraria, disciplinas.size());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Curso that)) {
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
