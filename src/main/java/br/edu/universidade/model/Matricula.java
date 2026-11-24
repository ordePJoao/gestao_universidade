package br.edu.universidade.model;

import br.edu.universidade.exception.NotaInvalidaException;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

//Classe associativa entre Aluno e Turma.
public class Matricula implements Serializable {

    private static final long serialVersionUID = 1L;
    public static final double MEDIA_APROVACAO = 6.0;

    private final Aluno aluno;
    private final Turma turma;
    private final String data;
    private StatusMatricula status;
    private final List<Avaliacao> avaliacoes = new ArrayList<>();

    public Matricula(Aluno aluno, Turma turma) {
        this.aluno = Objects.requireNonNull(aluno, "O aluno não pode ser nulo");
        this.turma = Objects.requireNonNull(turma, "A turma não pode ser nula");
        this.data = LocalDate.now().toString();
        this.status = StatusMatricula.ATIVA;
    }

    //Avaliacao internamente: ela pertence a esta matricula.
    public void adicionarAvaliacao(String tipo, double nota) throws NotaInvalidaException {
        if (this.status == StatusMatricula.CANCELADA) {
            throw new IllegalStateException("Não é possível lançar avaliação em matrícula cancelada.");
        }
        avaliacoes.add(new Avaliacao(tipo, nota));
        consolidarSituacao();
    }

    // Media simples das notas. Sem avaliacoes, retorna 0.0
    public double calcularMedia() {
        return avaliacoes.stream()
                .mapToDouble(Avaliacao::getNota)
                .average()
                .orElse(0.0);
    }

    public void cancelar() {
        this.status = StatusMatricula.CANCELADA;
    }

    public StatusMatricula consolidarSituacao() {
        if (this.status == StatusMatricula.CANCELADA) {
            return this.status;
        }
        if (avaliacoes.isEmpty()) {
            this.status = StatusMatricula.ATIVA;
            return this.status;
        }
        this.status = (calcularMedia() >= MEDIA_APROVACAO)
                ? StatusMatricula.APROVADA
                : StatusMatricula.REPROVADA;
        return this.status;
    }

    public String getSituacao() {
        return consolidarSituacao().name();
    }

    public Aluno getAluno() {
        return aluno;
    }

    public Turma getTurma() {
        return turma;
    }

    public String getData() {
        return data;
    }

    public StatusMatricula getStatus() {
        return consolidarSituacao();
    }

    public List<Avaliacao> getAvaliacoes() {
        return Collections.unmodifiableList(avaliacoes);
    }

    // Usado por Turma.adicionarMatricula (contains) para detectar matricula duplicada.
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Matricula that)) {
            return false;
        }
        return aluno.equals(that.aluno) && turma.equals(that.turma);
    }

    @Override
    public int hashCode() {
        return Objects.hash(aluno, turma);
    }
}