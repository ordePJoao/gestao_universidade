package br.edu.universidade.exception;

/**
 * Lançada quando há tentativa de matricular um aluno que já está matriculado na mesma turma.
 */
public class MatriculaDuplicadaException extends UniversidadeException {
    private static final long serialVersionUID = 1L;

    private final String identificadorAluno;
    private final String codigoTurma;

    public MatriculaDuplicadaException(String identificadorAluno, String codigoTurma) {
        super("Matrícula duplicada: o aluno " + identificadorAluno + " já está matriculado na turma " + codigoTurma + ".");
        this.identificadorAluno = identificadorAluno;
        this.codigoTurma = codigoTurma;
    }

    public MatriculaDuplicadaException(String mensagem) {
        super(mensagem);
        this.identificadorAluno = "";
        this.codigoTurma = "";
    }

    public String getIdentificadorAluno() {
        return identificadorAluno;
    }

    public String getCodigoTurma() {
        return codigoTurma;
    }
}
