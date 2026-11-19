package br.edu.universidade.view;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import java.awt.Dimension;

/**
 * Janela principal do sistema (usuário único: Secretaria).
 * Cada entidade fica em uma aba com seu próprio painel.
 * As abas de Aluno e Professor (task #15, P7) podem ser adicionadas aqui também.
 */
public class TelaPrincipal extends JFrame {

    private final JTabbedPane abas = new JTabbedPane();

    public TelaPrincipal() {
        super("Gestão Universidade - Secretaria");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1000, 650));

        abas.setFont(Estilo.FONTE_NEGRITO);
        // TODO P7: abas.addTab("Alunos", new AlunoPanel());
        // TODO P7: abas.addTab("Professores", new ProfessorPanel());
        abas.addTab("Cursos", new CursoPanel());
        abas.addTab("Disciplinas", new DisciplinaPanel());
        abas.addTab("Turmas", new TurmaPanel());
        abas.addTab("Matrículas", new MatriculaPanel());
        abas.addTab("Notas", new NotasPanel());

        add(abas);
        pack();
        setLocationRelativeTo(null); // centraliza na tela
    }
}
