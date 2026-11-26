package br.edu.universidade.model;

import br.edu.universidade.exception.MatriculaDuplicadaException;
import br.edu.universidade.exception.TurmaCheiaException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Representa uma turma de uma disciplina ministrada por um professor em um semestre,
 * controlando vagas e matrículas associadas.
 */
public class Turma implements Serializable, Exibivel {

    private static final long serialVersionUID = 1L;

    private String codigo;
    private String semestre;
    private int vagas;
    private Disciplina disciplina;
    private Professor professor;
    private final List<Matricula> matriculas;

    /**
     * Cria uma nova turma vinculada a uma disciplina e a um professor.
     *
     * @param codigo     código identificador da turma
     * @param disciplina disciplina ofertada na turma
     * @param semestre   semestre letivo (ex.: "2026/2")
     * @param professor  professor responsável pela turma
     * @param vagas      quantidade máxima de vagas (deve ser maior que zero)
     */
    public Turma(String codigo, Disciplina disciplina, String semestre, Professor professor, int vagas) {
        setCodigo(codigo);
        setDisciplina(disciplina);
        setSemestre(semestre);
        setProfessor(professor);
        setVagas(vagas);
        this.matriculas = new ArrayList<>();
    }

    public String getCodigo() {
        return codigo;
    }

    public final void setCodigo(String codigo) {
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException("Código da turma não pode ser vazio.");
        }
        this.codigo = codigo.trim();
    }

    public String getSemestre() {
        return semestre;
    }

    public final void setSemestre(String semestre) {
        if (semestre == null || semestre.isBlank()) {
            throw new IllegalArgumentException("Semestre da turma não pode ser vazio.");
        }
        this.semestre = semestre.trim();
    }

    public int getVagas() {
        return vagas;
    }

    public final void setVagas(int vagas) {
        if (vagas <= 0) {
            throw new IllegalArgumentException("Quantidade de vagas deve ser maior que zero.");
        }
        if (this.matriculas != null && vagas < this.matriculas.size()) {
            throw new IllegalArgumentException(
                    "Quantidade de vagas não pode ser menor que o total de matrículas ocupadas.");
        }
        this.vagas = vagas;
    }

    public Disciplina getDisciplina() {
        return disciplina;
    }

    public final void setDisciplina(Disciplina disciplina) {
        if (disciplina == null) {
            throw new IllegalArgumentException("Disciplina da turma não pode ser nula.");
        }
        this.disciplina = disciplina;
    }

    public Professor getProfessor() {
        return professor;
    }

    public final void setProfessor(Professor professor) {
        if (professor == null) {
            throw new IllegalArgumentException("Professor da turma não pode ser nulo.");
        }
        this.professor = professor;
    }

    /**
     * Retorna uma visão somente leitura da lista de matrículas da turma.
     */
    public List<Matricula> getMatriculas() {
        return Collections.unmodifiableList(matriculas);
    }

    /**
     * Retorna a quantidade de vagas atualmente ocupadas.
     */
    public int getVagasOcupadas() {
        return matriculas.size();
    }

    /**
     * Retorna a quantidade de vagas ainda disponíveis.
     */
    public int getVagasDisponiveis() {
        return Math.max(0, vagas - getVagasOcupadas());
    }

    /**
     * Informa se ainda há vaga disponível na turma.
     */
    public boolean temVaga() {
        return getVagasOcupadas() < vagas;
    }

    /**
     * Realiza a matrícula de um aluno na turma (conforme diagrama UML do projeto),
     * validando disponibilidade de vagas e duplicidade.
     *
     * @param aluno aluno a ser matriculado
     * @return a instância de {@link Matricula} criada e vinculada
     * @throws TurmaCheiaException caso a turma já tenha atingido o limite de vagas
     * @throws MatriculaDuplicadaException caso o aluno já esteja matriculado nesta turma
     */
    public Matricula matricular(Aluno aluno) throws TurmaCheiaException, MatriculaDuplicadaException {
        if (aluno == null) {
            throw new IllegalArgumentException("Aluno não pode ser nulo.");
        }
        if (!temVaga()) {
            throw new TurmaCheiaException(codigo, vagas);
        }
        for (Matricula m : matriculas) {
            if (m.getAluno().equals(aluno)) {
                throw new MatriculaDuplicadaException(aluno.getNome() + " (" + aluno.getMatricula() + ")", codigo);
            }
        }
        Matricula nova = new Matricula(aluno, this);
        matriculas.add(nova);
        if (!aluno.getMatriculas().contains(nova)) {
            aluno.getMatriculas().add(nova);
        }
        return nova;
    }

    /**
     * Adiciona uma matrícula à turma caso haja vaga disponível e a matrícula não esteja duplicada.
     *
     * @param matricula matrícula a ser registrada
     * @return true se a matrícula foi adicionada; false caso já exista na turma ou não haja vagas
     */
    public boolean adicionarMatricula(Matricula matricula) {
        if (matricula == null) {
            throw new IllegalArgumentException("Matrícula não pode ser nula.");
        }
        if (!temVaga() || matriculas.contains(matricula)) {
            return false;
        }
        return matriculas.add(matricula);
    }

    /**
     * Remove uma matrícula da turma, liberando a respectiva vaga.
     *
     * @param matricula matrícula a ser removida
     * @return true se a matrícula estava na turma e foi removida
     */
    public boolean removerMatricula(Matricula matricula) {
        if (matricula == null) {
            return false;
        }
        return matriculas.remove(matricula);
    }

    /**
     * Retorna os dados formatados da turma, incluindo disciplina, professor e ocupação de vagas.
     */
    @Override
    public String exibirDados() {
        return String.format("Turma [%s] - %s | Semestre: %s | Prof.: %s | Vagas: %d/%d",
                codigo,
                disciplina.getNome(),
                semestre,
                professor.getNome(),
                getVagasOcupadas(),
                vagas);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Turma that)) {
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
        return codigo + " (" + semestre + ")";
    }
}
