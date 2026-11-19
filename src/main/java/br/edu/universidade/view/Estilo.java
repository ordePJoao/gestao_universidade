package br.edu.universidade.view;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

/**
 * Padrão visual do sistema (fontes, cores e botões).
 * Tudo que é visual fica centralizado aqui: se o grupo (P7/P8) decidir
 * mudar uma cor ou fonte, basta alterar nesta classe.
 */
public final class Estilo {

    // ---------- Fontes ----------
    public static final Font FONTE_PADRAO = new Font("SansSerif", Font.PLAIN, 14);
    public static final Font FONTE_NEGRITO = new Font("SansSerif", Font.BOLD, 14);
    public static final Font FONTE_TITULO = new Font("SansSerif", Font.BOLD, 20);

    // ---------- Cores ----------
    public static final Color COR_PRIMARIA = new Color(0x1E5AA8);   // azul
    public static final Color COR_SECUNDARIA = new Color(0x6C757D); // cinza
    public static final Color COR_PERIGO = new Color(0xC0392B);     // vermelho
    public static final Color COR_SUCESSO = new Color(0x2E8B57);    // verde
    public static final Color COR_FUNDO = Color.WHITE;
    public static final Color COR_TEXTO_BOTAO = Color.WHITE;

    private Estilo() {
    }

    // ---------- Botões ----------
    public static JButton botaoPrimario(String texto) {
        return criarBotao(texto, COR_PRIMARIA);
    }

    public static JButton botaoSecundario(String texto) {
        return criarBotao(texto, COR_SECUNDARIA);
    }

    public static JButton botaoPerigo(String texto) {
        return criarBotao(texto, COR_PERIGO);
    }

    private static JButton criarBotao(String texto, Color cor) {
        JButton botao = new JButton(texto);
        botao.setFont(FONTE_NEGRITO);
        botao.setBackground(cor);
        botao.setForeground(COR_TEXTO_BOTAO);
        botao.setFocusPainted(false);
        botao.setOpaque(true);
        botao.setBorderPainted(false);
        botao.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        botao.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return botao;
    }

    // ---------- Textos ----------
    public static JLabel titulo(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(FONTE_TITULO);
        label.setForeground(COR_PRIMARIA);
        label.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        return label;
    }

    // ---------- Tabelas ----------
    /** Modelo de tabela em que o usuário não edita as células direto. */
    public static DefaultTableModel modeloTabela(String... colunas) {
        return new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int linha, int coluna) {
                return false;
            }
        };
    }

    public static JTable tabela(DefaultTableModel modelo) {
        JTable tabela = new JTable(modelo);
        tabela.setFont(FONTE_PADRAO);
        tabela.setRowHeight(26);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.setFillsViewportHeight(true);
        JTableHeader cabecalho = tabela.getTableHeader();
        cabecalho.setFont(FONTE_NEGRITO);
        cabecalho.setReorderingAllowed(false);
        return tabela;
    }

    // ---------- Formulários ----------
    /** Painel com GridBagLayout e borda com título, para os campos do formulário. */
    public static JPanel formulario(String titulo) {
        JPanel painel = new JPanel(new GridBagLayout());
        painel.setBackground(COR_FUNDO);
        painel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(COR_PRIMARIA), titulo,
                        0, 0, FONTE_NEGRITO, COR_PRIMARIA),
                BorderFactory.createEmptyBorder(6, 6, 6, 6)));
        return painel;
    }

    /** Adiciona uma linha "Rótulo: [campo]" no formulário. */
    public static void adicionarCampo(JPanel formulario, int linha, String rotulo, JComponent campo) {
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 4, 4, 4);
        c.gridy = linha;

        c.gridx = 0;
        c.anchor = GridBagConstraints.LINE_END;
        JLabel label = new JLabel(rotulo + ":");
        label.setFont(FONTE_PADRAO);
        formulario.add(label, c);

        c.gridx = 1;
        c.anchor = GridBagConstraints.LINE_START;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;
        campo.setFont(FONTE_PADRAO);
        formulario.add(campo, c);
    }

    /** Painel padrão de cada tela (fundo branco e margem). */
    public static JPanel painelTela() {
        JPanel painel = new JPanel(new java.awt.BorderLayout(10, 10));
        painel.setBackground(COR_FUNDO);
        painel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        return painel;
    }

    /** Painel horizontal para agrupar botões. */
    public static JPanel barraBotoes(JButton... botoes) {
        JPanel barra = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 8, 4));
        barra.setBackground(COR_FUNDO);
        for (JButton b : botoes) {
            barra.add(b);
        }
        return barra;
    }
}
