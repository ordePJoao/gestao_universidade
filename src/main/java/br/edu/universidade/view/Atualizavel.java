package br.edu.universidade.view;

/**
 * Painéis que mostram dados da {@code Universidade} implementam esta interface
 * para recarregar suas listas quando a aba é aberta (task #18, P7).
 * Ex.: um aluno cadastrado na aba Alunos aparece na aba Matrículas sem reiniciar o sistema.
 */
public interface Atualizavel {

    /** Recarrega tabelas e combos a partir da Universidade. */
    void atualizar();
}
