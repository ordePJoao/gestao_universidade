package br.edu.universidade.view;

import br.edu.universidade.model.Universidade;
import br.edu.universidade.persistence.RepositorioArquivo;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Janela principal do sistema (usuário único: Secretaria).
 * Cada entidade fica em uma aba com seu próprio painel, e o menu
 * superior também leva a cada módulo (tasks #15 e #18, P7).
 *
 * <p>Uma única instância de {@link Universidade} é carregada do arquivo ao abrir
 * e repassada para todas as telas. Ao fechar, o sistema pergunta se deve salvar.</p>
 */
public class TelaPrincipal extends JFrame {

    private static final DateTimeFormatter FORMATO_HORA = DateTimeFormatter.ofPattern("HH:mm:ss");

    private final RepositorioArquivo repositorio = new RepositorioArquivo();
    private final Universidade universidade;

    private final JTabbedPane abas = new JTabbedPane();
    private final JLabel lblStatus = new JLabel();

    public TelaPrincipal() {
        super("Gestão Universidade - Secretaria");
        universidade = carregarUniversidade();

        // Quem decide se fecha é o confirmarSaida() (pergunta se quer salvar)
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                confirmarSaida();
            }
        });
        setMinimumSize(new Dimension(1000, 650));

        abas.setFont(Estilo.FONTE_NEGRITO);
        abas.addTab("Alunos", new AlunoPanel(universidade));
        abas.addTab("Professores", new ProfessorPanel(universidade));
        // TODO P8 (#20/#21): passar a mesma instância -> new CursoPanel(universidade), etc.
        abas.addTab("Cursos", new CursoPanel());
        abas.addTab("Disciplinas", new DisciplinaPanel());
        abas.addTab("Turmas", new TurmaPanel());
        abas.addTab("Matrículas", new MatriculaPanel());
        abas.addTab("Notas", new NotasPanel());

        // Ao trocar de aba, a tela aberta recarrega suas listas (se souber fazer isso)
        abas.addChangeListener(e -> atualizarAbaAtual());

        setJMenuBar(criarMenu());
        add(abas, BorderLayout.CENTER);
        add(criarBarraStatus(), BorderLayout.SOUTH);
        pack();
        setLocationRelativeTo(null); // centraliza na tela
    }

    /** Instância única compartilhada por todas as telas. */
    public Universidade getUniversidade() {
        return universidade;
    }

    // ---------- Persistência (salvar / carregar) ----------

    private Universidade carregarUniversidade() {
        try {
            Universidade carregada = repositorio.carregar();
            if (carregada != null) {
                return carregada;
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null,
                    "Não foi possível carregar os dados salvos.\nO sistema vai iniciar vazio.\n\n"
                            + ex.getMessage(), "Erro ao carregar", JOptionPane.ERROR_MESSAGE);
        }
        return new Universidade();
    }

    /**
     * Grava a Universidade em arquivo.
     *
     * @return true se salvou sem erro
     */
    public boolean salvar() {
        try {
            repositorio.salvar(universidade);
            lblStatus.setText("Dados salvos às " + LocalTime.now().format(FORMATO_HORA));
            return true;
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Não foi possível salvar os dados.\n\n" + ex.getMessage(),
                    "Erro ao salvar", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    /** Pergunta se quer salvar antes de sair: Sim (salva e sai), Não (sai), Cancelar (fica). */
    private void confirmarSaida() {
        int resposta = JOptionPane.showConfirmDialog(this,
                "Deseja salvar as alterações antes de sair?", "Sair do sistema",
                JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (resposta == JOptionPane.YES_OPTION) {
            if (!salvar()) {
                return; // deu erro ao salvar: não fecha para não perder dados
            }
        } else if (resposta != JOptionPane.NO_OPTION) {
            return;     // Cancelar ou fechou o diálogo
        }
        dispose();
        System.exit(0);
    }

    // ---------- Navegação ----------

    private void atualizarAbaAtual() {
        Component aba = abas.getSelectedComponent();
        if (aba instanceof Atualizavel atualizavel) {
            atualizavel.atualizar();
        }
    }

    /** Seleciona a aba pelo título (usado pelos itens do menu). */
    public void abrirAba(String titulo) {
        int indice = abas.indexOfTab(titulo);
        if (indice >= 0) {
            abas.setSelectedIndex(indice);
        }
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

        JMenuItem salvar = new JMenuItem("Salvar");
        salvar.setFont(Estilo.FONTE_PADRAO);
        salvar.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
        salvar.addActionListener(e -> salvar());
        menu.add(salvar);

        menu.addSeparator();

        JMenuItem sair = new JMenuItem("Sair");
        sair.setFont(Estilo.FONTE_PADRAO);
        sair.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_DOWN_MASK));
        sair.addActionListener(e -> confirmarSaida());
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

    // ---------- Barra inferior (botão Salvar + status) ----------

    private JPanel criarBarraStatus() {
        JButton btnSalvar = Estilo.botaoPrimario("Salvar dados");
        btnSalvar.addActionListener(e -> salvar());

        lblStatus.setFont(Estilo.FONTE_PADRAO);
        lblStatus.setForeground(Estilo.COR_SECUNDARIA);
        lblStatus.setText("Dica: Ctrl+S salva os dados a qualquer momento.");

        JPanel barra = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        barra.setBackground(Estilo.COR_FUNDO);
        barra.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Estilo.COR_SECUNDARIA));
        barra.add(btnSalvar);
        barra.add(lblStatus);
        return barra;
    }
}
