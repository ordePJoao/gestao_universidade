package br.edu.universidade.exception;

/**
 * Lançada quando um CPF fornecido não atende ao formato ou validação requerida.
 */
public class CpfInvalidoException extends UniversidadeException {
    private static final long serialVersionUID = 1L;

    private final String cpfInformado;

    public CpfInvalidoException(String cpfInformado) {
        super("CPF inválido: '" + cpfInformado + "'. O CPF deve conter 11 dígitos numéricos válidos.");
        this.cpfInformado = cpfInformado;
    }

    public String getCpfInformado() {
        return cpfInformado;
    }
}
