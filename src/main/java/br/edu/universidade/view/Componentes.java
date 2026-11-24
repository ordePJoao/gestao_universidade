package br.edu.universidade.view;

import br.edu.universidade.exception.UniversidadeException;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JOptionPane;
import java.awt.Component;
import java.util.List;
import java.util.function.Function;

/**
 * Funções de apoio usadas pelas telas de Curso, Disciplina, Turma, Matrícula e Notas
 * (tasks #20 e #21, P8): mensagens em JOptionPane e combos com objetos reais do núcleo.
 */
public final class Componentes {

    private Componentes() {
    }

    // ---------- Mensagens ----------

    public static void aviso(Component pai, String mensagem, String titulo) {
        JOptionPane.showMessageDialog(pai, mensagem, titulo, JOptionPane.WARNING_MESSAGE);
    }

    /** Exceções do domínio (turma cheia, nota inválida...) e de validação viram mensagem na tela. */
    public static void mostrarErro(Component pai, Exception ex) {
        String titulo = ex instanceof UniversidadeException || ex instanceof IllegalArgumentException
                ? "Dados inválidos" : "Erro";
        JOptionPane.showMessageDialog(pai, ex.getMessage(), titulo, JOptionPane.ERROR_MESSAGE);
    }

    public static boolean confirmar(Component pai, String mensagem, String titulo) {
        return JOptionPane.showConfirmDialog(pai, mensagem, titulo, JOptionPane.YES_NO_OPTION)
                == JOptionPane.YES_OPTION;
    }

    // ---------- Combos com objetos ----------

    /**
     * Define o texto mostrado para cada objeto do combo.
     * Ex.: textoDoCombo(cbProfessor, p -> p.getNome() + " (" + p.getSiape() + ")");
     */
    public static <T> void textoDoCombo(JComboBox<T> combo, Function<T, String> texto) {
        combo.setFont(Estilo.FONTE_PADRAO);
        combo.setRenderer(new DefaultListCellRenderer() {
            @Override
            @SuppressWarnings("unchecked")
            public Component getListCellRendererComponent(JList<?> lista, Object valor, int indice,
                                                          boolean selecionado, boolean foco) {
                String rotulo = valor == null ? "" : texto.apply((T) valor);
                return super.getListCellRendererComponent(lista, rotulo, indice, selecionado, foco);
            }
        });
    }

    /** Troca os itens do combo mantendo o item selecionado (se ele ainda existir). */
    public static <T> void recarregarCombo(JComboBox<T> combo, List<T> itens) {
        Object selecionado = combo.getSelectedItem();
        combo.removeAllItems();
        for (T item : itens) {
            combo.addItem(item);
        }
        if (selecionado != null && itens.contains(selecionado)) {
            combo.setSelectedItem(selecionado);
        }
    }
}
