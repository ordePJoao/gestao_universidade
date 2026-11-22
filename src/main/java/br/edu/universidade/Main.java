package br.edu.universidade;

import br.edu.universidade.view.TelaPrincipal;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        // Toda tela Swing deve ser criada na thread de eventos (EDT)
        SwingUtilities.invokeLater(() -> new TelaPrincipal().setVisible(true));
    }
}
