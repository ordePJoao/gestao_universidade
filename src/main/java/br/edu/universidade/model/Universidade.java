package br.edu.universidade.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Objeto raiz do sistema de Gestão Universitária.
 * Atua como ponto de entrada único para o modelo de domínio, agregando
 * todas as entidades e permitindo persistência em cascata (Task #13).
 */
public class Universidade implements Serializable {

    private static final long serialVersionUID = 1L;

    private String nome;
    private final List<Aluno> alunos;
    private final List<Professor> professores;
    private final List<Curso> cursos;
    private final List<Disciplina> disciplinas;
    private final List<Turma> turmas;
    private final List<Departamento> departamentos;

    public Universidade() {
        this("Universidade Federal");
    }

    public Universidade(String nome) {
        this.nome = (nome == null || nome.isBlank()) ? "Universidade Federal" : nome.trim();
        this.alunos = new ArrayList<>();
        this.professores = new ArrayList<>();
        this.cursos = new ArrayList<>();
        this.disciplinas = new ArrayList<>();
        this.turmas = new ArrayList<>();
        this.departamentos = new ArrayList<>();
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        if (nome != null && !nome.isBlank()) {
            this.nome = nome.trim();
        }
    }

    // ==========================================
    // ALUNOS
    // ==========================================

    public List<Aluno> getAlunos() {
        return Collections.unmodifiableList(alunos);
    }

    public boolean cadastrarAluno(Aluno aluno) {
        return adicionarAluno(aluno);
    }

    public boolean adicionarAluno(Aluno aluno) {
        if (aluno == null || buscarAlunoPorMatricula(aluno.getMatricula()).isPresent()) {
            return false;
        }
        return alunos.add(aluno);
    }

    public boolean removerAluno(Aluno aluno) {
        if (aluno == null) {
            return false;
        }
        return alunos.remove(aluno);
    }

    public boolean removerAluno(String matricula) {
        if (matricula == null) {
            return false;
        }
        return alunos.removeIf(a -> matricula.equalsIgnoreCase(a.getMatricula()));
    }

    public Optional<Aluno> buscarAlunoPorMatricula(String matricula) {
        if (matricula == null) {
            return Optional.empty();
        }
        return alunos.stream()
                .filter(a -> matricula.equalsIgnoreCase(a.getMatricula()))
                .findFirst();
    }

    // ==========================================
    // PROFESSORES
    // ==========================================

    public List<Professor> getProfessores() {
        return Collections.unmodifiableList(professores);
    }

    public boolean cadastrarProfessor(Professor professor) {
        return adicionarProfessor(professor);
    }

    public boolean adicionarProfessor(Professor professor) {
        if (professor == null || buscarProfessorPorSiape(professor.getSiape()).isPresent()) {
            return false;
        }
        return professores.add(professor);
    }

    public boolean removerProfessor(Professor professor) {
        if (professor == null) {
            return false;
        }
        return professores.remove(professor);
    }

    public boolean removerProfessor(String siape) {
        if (siape == null) {
            return false;
        }
        return professores.removeIf(p -> siape.equalsIgnoreCase(p.getSiape()));
    }

    public Optional<Professor> buscarProfessorPorSiape(String siape) {
        if (siape == null) {
            return Optional.empty();
        }
        return professores.stream()
                .filter(p -> siape.equalsIgnoreCase(p.getSiape()))
                .findFirst();
    }

    // ==========================================
    // CURSOS
    // ==========================================

    public List<Curso> getCursos() {
        return Collections.unmodifiableList(cursos);
    }

    public boolean cadastrarCurso(Curso curso) {
        return adicionarCurso(curso);
    }

    public boolean adicionarCurso(Curso curso) {
        if (curso == null || buscarCursoPorCodigo(curso.getCodigo()).isPresent()) {
            return false;
        }
        return cursos.add(curso);
    }

    public boolean removerCurso(Curso curso) {
        if (curso == null) {
            return false;
        }
        return cursos.remove(curso);
    }

    public boolean removerCurso(String codigo) {
        if (codigo == null) {
            return false;
        }
        return cursos.removeIf(c -> codigo.equalsIgnoreCase(c.getCodigo()));
    }

    public Optional<Curso> buscarCursoPorCodigo(String codigo) {
        if (codigo == null) {
            return Optional.empty();
        }
        return cursos.stream()
                .filter(c -> codigo.equalsIgnoreCase(c.getCodigo()))
                .findFirst();
    }

    // ==========================================
    // DISCIPLINAS
    // ==========================================

    public List<Disciplina> getDisciplinas() {
        return Collections.unmodifiableList(disciplinas);
    }

    public boolean cadastrarDisciplina(Disciplina disciplina) {
        return adicionarDisciplina(disciplina);
    }

    public boolean adicionarDisciplina(Disciplina disciplina) {
        if (disciplina == null || buscarDisciplinaPorCodigo(disciplina.getCodigo()).isPresent()) {
            return false;
        }
        return disciplinas.add(disciplina);
    }

    public boolean removerDisciplina(Disciplina disciplina) {
        if (disciplina == null) {
            return false;
        }
        return disciplinas.remove(disciplina);
    }

    public boolean removerDisciplina(String codigo) {
        if (codigo == null) {
            return false;
        }
        return disciplinas.removeIf(d -> codigo.equalsIgnoreCase(d.getCodigo()));
    }

    public Optional<Disciplina> buscarDisciplinaPorCodigo(String codigo) {
        if (codigo == null) {
            return Optional.empty();
        }
        return disciplinas.stream()
                .filter(d -> codigo.equalsIgnoreCase(d.getCodigo()))
                .findFirst();
    }

    // ==========================================
    // TURMAS
    // ==========================================

    public List<Turma> getTurmas() {
        return Collections.unmodifiableList(turmas);
    }

    public boolean cadastrarTurma(Turma turma) {
        return adicionarTurma(turma);
    }

    public boolean adicionarTurma(Turma turma) {
        if (turma == null || buscarTurmaPorCodigo(turma.getCodigo()).isPresent()) {
            return false;
        }
        return turmas.add(turma);
    }

    public boolean removerTurma(Turma turma) {
        if (turma == null) {
            return false;
        }
        return turmas.remove(turma);
    }

    public boolean removerTurma(String codigo) {
        if (codigo == null) {
            return false;
        }
        return turmas.removeIf(t -> codigo.equalsIgnoreCase(t.getCodigo()));
    }

    public Optional<Turma> buscarTurmaPorCodigo(String codigo) {
        if (codigo == null) {
            return Optional.empty();
        }
        return turmas.stream()
                .filter(t -> codigo.equalsIgnoreCase(t.getCodigo()))
                .findFirst();
    }

    // ==========================================
    // DEPARTAMENTOS
    // ==========================================

    public List<Departamento> getDepartamentos() {
        return Collections.unmodifiableList(departamentos);
    }

    public boolean adicionarDepartamento(Departamento departamento) {
        if (departamento == null || departamentos.contains(departamento)) {
            return false;
        }
        return departamentos.add(departamento);
    }

    public boolean removerDepartamento(Departamento departamento) {
        if (departamento == null) {
            return false;
        }
        return departamentos.remove(departamento);
    }
}