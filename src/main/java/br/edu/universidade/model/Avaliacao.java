package br.edu.universidade.model;

import br.edu.universidade.exception.NotaInvalidaException;

import java.io.Serializable;
import java.util.Objects;

public class Avaliacao implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final double NOTA_MIN = 0.0;
    public static final double NOTA_MAX = 10.0;

    private final String tipo;
    private double nota;

    public Avaliacao(String tipo, double nota) throws NotaInvalidaException {
        this.tipo = Objects.requireNonNull(tipo, "O tipo da avaliação não pode ser nulo");
        validarNota(nota);
        this.nota = nota;
    }

    public void alterarNota(double n) throws NotaInvalidaException {
        validarNota(n);   // valida ANTES de atribuir
        this.nota = n;
    }

    private static void validarNota(double nota) throws NotaInvalidaException {
        if (!(nota >= NOTA_MIN && nota <= NOTA_MAX)) {
            throw new NotaInvalidaException(nota);
        }
    }

    public String getTipo() {
        return tipo;
    }

    public double getNota() {
        return nota;
    }
}