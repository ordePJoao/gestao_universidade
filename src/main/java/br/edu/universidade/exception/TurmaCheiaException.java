package br.edu.universidade.exception;

/**
 * Lançada quando há tentativa de matricular um aluno em uma turma sem vagas disponíveis.
 */
public class TurmaCheiaException extends UniversidadeException {
    private static final long serialVersionUID = 1L;

    private final String codigoTurma;
    private final int vagasTotais;

    public TurmaCheiaException(String codigoTurma, int vagasTotais) {
        super("Turma cheia: a turma " + codigoTurma + " atingiu o limite de " + vagasTotais + " vaga(s).");
        this.codigoTurma = codigoTurma;
        this.vagasTotais = vagasTotais;
    }

    public TurmaCheiaException(String codigoTurma) {
        super("Turma cheia: a turma " + codigoTurma + " não possui vagas disponíveis.");
        this.codigoTurma = codigoTurma;
        this.vagasTotais = 0;
    }

    public String getCodigoTurma() {
        return codigoTurma;
    }

    public int getVagasTotais() {
        return vagasTotais;
    }
}
