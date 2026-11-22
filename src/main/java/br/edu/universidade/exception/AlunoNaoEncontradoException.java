package br.edu.universidade.exception;

/**
 * Lançada quando uma busca por aluno (por matrícula ou identificador) não retorna resultados.
 */
public class AlunoNaoEncontradoException extends UniversidadeException {
    private static final long serialVersionUID = 1L;

    private final String identificador;

    public AlunoNaoEncontradoException(String identificador) {
        super("Aluno não encontrado: nenhum aluno localizado com a matrícula/identificador '" + identificador + "'.");
        this.identificador = identificador;
    }

    public String getIdentificador() {
        return identificador;
    }
}
