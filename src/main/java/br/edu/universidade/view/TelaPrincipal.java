package br.edu.universidade.view;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import java.awt.Dimension;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

/**
 * Janela principal do sistema (usuário único: Secretaria).
 * Cada entidade fica em uma aba com seu próprio painel, e o menu
 * superior também leva a cada módulo (tasks #15 e #18, P7).
 */
public class TelaPrincipal extends JFrame {

    private final JTabbedPane abas = new JTabbedPane();

    public TelaPrincipal() {
        super("Gestão Universidade - Secretaria");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1000, 650));

        abas.setFont(Estilo.FONTE_NEGRITO);
        abas.addTab("Alunos", new AlunoPanel());
        abas.addTab("Professores", new ProfessorPanel());
        abas.addTab("Cursos", new CursoPanel());
        abas.addTab("Disciplinas", new DisciplinaPanel());
        abas.addTab("Turmas", new TurmaPanel());
        abas.addTab("Matrículas", new MatriculaPanel());
        abas.addTab("Notas", new NotasPanel());

        setJMenuBar(criarMenu());
        add(abas);
        pack();
        setLocationRelativeTo(null); // centraliza na tela
    }

    // ---------- Menu ----------

    private JMenuBar criarMenu() {
        JMenuBar barra = new JMenuBar();
        barra.add(criarMenuArquivo());
        barra.add(criarMenuModulos("Cadastros", KeyEvent.VK_C,
                "Alunos", "Professores", "Cursos", "Disciplinas", "Turmas"));
        barra.add(criarMenuModulos("Acadêmico", KeyEvent.VK_D, "Matrículas", "Notas"));
        barra.add(criarMenuAjuda());
        return barra;
    }

    private JMenu criarMenuArquivo() {
        JMenu menu = new JMenu("Arquivo");
        menu.setFont(Estilo.FONTE_PADRAO);
        menu.setMnemonic(KeyEvent.VK_A);

        JMenuItem sair = new JMenuItem("Sair");
        sair.setFont(Estilo.FONTE_PADRAO);
        sair.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_DOWN_MASK));
        sair.addActionListener(e -> dispose());
        menu.add(sair);
        return menu;
    }

    /** Cria um menu cujos itens selecionam a aba de mesmo nome. */
    private JMenu criarMenuModulos(String titulo, int mnemonico, String... nomesAbas) {
        JMenu menu = new JMenu(titulo);
        menu.setFont(Estilo.FONTE_PADRAO);
        menu.setMnemonic(mnemonico);
        for (String nome : nomesAbas) {
            JMenuItem item = new JMenuItem(nome);
            item.setFont(Estilo.FONTE_PADRAO);
            item.addActionListener(e -> abrirAba(nome));
            menu.add(item);
        }
        return menu;
    }

    private JMenu criarMenuAjuda() {
        JMenu menu = new JMenu("Ajuda");
        menu.setFont(Estilo.FONTE_PADRAO);
        JMenuItem sobre = new JMenuItem("Sobre");
        sobre.setFont(Estilo.FONTE_PADRAO);
        sobre.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "Sistema de Gestão Universitária\nProjeto Final de POO",
                "Sobre", JOptionPane.INFORMATION_MESSAGE));
        menu.add(sobre);
        return menu;
    }

    /** Seleciona a aba pelo título (usado pelos itens do menu). */
    public void abrirAba(String titulo) {
        int indice = abas.indexOfTab(titulo);
        if (indice >= 0) {
            abas.setSelectedIndex(indice);
        }
    }
}
