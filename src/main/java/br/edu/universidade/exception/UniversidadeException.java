package br.edu.universidade.exception;

public class UniversidadeException extends Exception {
    private static final long serialVersionUID = 1L;

    public UniversidadeException(String mensagem) {
        super(mensagem);
    }
}