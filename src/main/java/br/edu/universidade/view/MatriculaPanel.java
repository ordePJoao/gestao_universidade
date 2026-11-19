package br.edu.universidade.view;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Tela de Matrícula: matricular aluno em turma e cancelar matrícula.
 * Sprint 1: usa dados falsos (DadosFalsos). Na Sprint 2 as regras
 * (MatriculaDuplicadaException, TurmaCheiaException) vêm do núcleo.
 */
public class MatriculaPanel extends JPanel {

    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // Formulário
    private final JComboBox<String> cbAluno = new JComboBox<>(DadosFalsos.ALUNOS);
    private final JComboBox<String> cbTurma = new JComboBox<>(DadosFalsos.codigosTurmas());

    // Botões
    private final JButton btnMatricular = Estilo.botaoPrimario("Matricular");
    private final JButton btnCancelarMatricula = Estilo.botaoPerigo("Cancelar matrícula");

    // Tabela
    private final DefaultTableModel modeloMatriculas =
            Estilo.modeloTabela("Aluno", "Turma", "Data", "Status");
    private final JTable tblMatriculas = Estilo.tabela(modeloMatriculas);

    public MatriculaPanel() {
        setLayout(new BorderLayout());
        JPanel tela = Estilo.painelTela();
        add(tela, BorderLayout.CENTER);

        tela.add(Estilo.titulo("Matrículas"), BorderLayout.NORTH);

        JPanel formulario = Estilo.formulario("Matricular aluno em turma");
        Estilo.adicionarCampo(formulario, 0, "Aluno", cbAluno);
        Estilo.adicionarCampo(formulario, 1, "Turma", cbTurma);

        JPanel topo = new JPanel(new BorderLayout());
        topo.setBackground(Estilo.COR_FUNDO);
        topo.add(formulario, BorderLayout.CENTER);
        topo.add(Estilo.barraBotoes(btnMatricular, btnCancelarMatricula), BorderLayout.SOUTH);

        JScrollPane scrollTabela = new JScrollPane(tblMatriculas);
        scrollTabela.setBorder(BorderFactory.createTitledBorder("Matrículas"));

        JPanel centro = new JPanel(new BorderLayout(10, 10));
        centro.setBackground(Estilo.COR_FUNDO);
        centro.add(topo, BorderLayout.NORTH);
        centro.add(scrollTabela, BorderLayout.CENTER);
        tela.add(centro, BorderLayout.CENTER);

        carregarDadosFalsos();
        configurarAcoes();
    }

    private void carregarDadosFalsos() {
        for (Object[] matricula : DadosFalsos.MATRICULAS) {
            modeloMatriculas.addRow(matricula);
        }
    }

    private void configurarAcoes() {
        // TODO Sprint 2: trocar pela chamada ao service, tratando
        // MatriculaDuplicadaException e TurmaCheiaException
        btnMatricular.addActionListener(e -> {
            String aluno = (String) cbAluno.getSelectedItem();
            String turma = (String) cbTurma.getSelectedItem();

            // Simulação visual da regra "não matricular duas vezes"
            for (int i = 0; i < modeloMatriculas.getRowCount(); i++) {
                boolean mesmoAluno = aluno.equals(modeloMatriculas.getValueAt(i, 0));
                boolean mesmaTurma = turma.equals(modeloMatriculas.getValueAt(i, 1));
                boolean ativa = "ATIVA".equals(modeloMatriculas.getValueAt(i, 3));
                if (mesmoAluno && mesmaTurma && ativa) {
                    JOptionPane.showMessageDialog(this,
                            "Aluno já matriculado nesta turma.", "Erro", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            modeloMatriculas.addRow(new Object[]{
                    aluno, turma, LocalDate.now().format(FORMATO_DATA), "ATIVA"});
        });

        btnCancelarMatricula.addActionListener(e -> {
            int linha = tblMatriculas.getSelectedRow();
            if (linha < 0) {
                JOptionPane.showMessageDialog(this, "Selecione uma matrícula na tabela.");
                return;
            }
            int resposta = JOptionPane.showConfirmDialog(this,
                    "Cancelar a matrícula selecionada?", "Confirmar", JOptionPane.YES_NO_OPTION);
            if (resposta == JOptionPane.YES_OPTION) {
                modeloMatriculas.setValueAt("CANCELADA", linha, 3);
            }
        });
    }
}
