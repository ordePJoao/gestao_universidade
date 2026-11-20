package br.edu.universidade.exception;

public class NotaInvalidaException extends UniversidadeException {
    private static final long serialVersionUID = 1L;

    private final double notaInformada;

    public NotaInvalidaException(double notaInformada) {
        super("Nota inválida: " + notaInformada + ". A nota deve estar entre 0 e 10.");
        this.notaInformada = notaInformada;
    }

    public double getNotaInformada() {
        return notaInformada;
    }
}