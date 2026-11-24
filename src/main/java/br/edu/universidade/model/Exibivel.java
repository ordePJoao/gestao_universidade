package br.edu.universidade.model;

/**
 * Contrato para objetos do sistema que sabem se apresentar em texto.
 *
 * Implementada por Pessoa (e, por herança, Aluno e Professor), Curso, Disciplina e Turma.
 * Assim qualquer um deles pode ser tratado como Exibivel, por exemplo:
 *
 * <pre>
 * List&lt;Exibivel&gt; itens = List.of(aluno, curso, disciplina, turma);
 * for (Exibivel item : itens) {
 *     System.out.println(item.exibirDados());
 * }
 * </pre>
 */
public interface Exibivel {

    /** Retorna os dados principais do objeto formatados em uma linha de texto. */
    String exibirDados();
}
