package br.edu.universidade.view;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;

/**
 * Tela de Turma: abrir turma de uma disciplina (semestre, professor e vagas).
 * Sprint 1: usa dados falsos (DadosFalsos). Ligação com o núcleo na Sprint 2.
 */
public class TurmaPanel extends JPanel {

    // Formulário
    private final JTextField txtCodigo = new JTextField(10);
    private final JComboBox<String> cbDisciplina = new JComboBox<>(DadosFalsos.codigosDisciplinas());
    private final JComboBox<String> cbSemestre = new JComboBox<>(DadosFalsos.SEMESTRES);
    private final JComboBox<String> cbProfessor = new JComboBox<>(DadosFalsos.PROFESSORES);
    private final JSpinner spnVagas = new JSpinner(new SpinnerNumberModel(40, 1, 200, 1));

    // Botões
    private final JButton btnAbrirTurma = Estilo.botaoPrimario("Abrir turma");
    private final JButton btnLimpar = Estilo.botaoSecundario("Limpar");
    private final JButton btnExcluir = Estilo.botaoPerigo("Excluir");

    // Tabela
    private final DefaultTableModel modeloTurmas = Estilo.modeloTabela(
            "Código", "Disciplina", "Semestre", "Professor", "Vagas", "Ocupadas");
    private final JTable tblTurmas = Estilo.tabela(modeloTurmas);

    public TurmaPanel() {
        setLayout(new BorderLayout());
        JPanel tela = Estilo.painelTela();
        add(tela, BorderLayout.CENTER);

        tela.add(Estilo.titulo("Turmas"), BorderLayout.NORTH);

        JPanel formulario = Estilo.formulario("Abrir turma");
        Estilo.adicionarCampo(formulario, 0, "Código", txtCodigo);
        Estilo.adicionarCampo(formulario, 1, "Disciplina", cbDisciplina);
        Estilo.adicionarCampo(formulario, 2, "Semestre", cbSemestre);
        Estilo.adicionarCampo(formulario, 3, "Professor", cbProfessor);
        Estilo.adicionarCampo(formulario, 4, "Vagas", spnVagas);

        JPanel topo = new JPanel(new BorderLayout());
        topo.setBackground(Estilo.COR_FUNDO);
        topo.add(formulario, BorderLayout.CENTER);
        topo.add(Estilo.barraBotoes(btnAbrirTurma, btnLimpar, btnExcluir), BorderLayout.SOUTH);

        JScrollPane scrollTabela = new JScrollPane(tblTurmas);
        scrollTabela.setBorder(BorderFactory.createTitledBorder("Turmas abertas"));

        JPanel centro = new JPanel(new BorderLayout(10, 10));
        centro.setBackground(Estilo.COR_FUNDO);
        centro.add(topo, BorderLayout.NORTH);
        centro.add(scrollTabela, BorderLayout.CENTER);
        tela.add(centro, BorderLayout.CENTER);

        carregarDadosFalsos();
        configurarAcoes();
    }

    private void carregarDadosFalsos() {
        for (Object[] turma : DadosFalsos.TURMAS) {
            modeloTurmas.addRow(turma);
        }
    }

    private void configurarAcoes() {
        // TODO Sprint 2: chamar o service de Turma em vez de mexer só na tabela
        btnAbrirTurma.addActionListener(e -> {
            if (txtCodigo.getText().isBlank()) {
                JOptionPane.showMessageDialog(this, "Informe o código da turma.");
                return;
            }
            modeloTurmas.addRow(new Object[]{
                    txtCodigo.getText(),
                    cbDisciplina.getSelectedItem(),
                    cbSemestre.getSelectedItem(),
                    cbProfessor.getSelectedItem(),
                    spnVagas.getValue(),
                    0});
            limparFormulario();
        });

        btnLimpar.addActionListener(e -> limparFormulario());

        btnExcluir.addActionListener(e -> {
            int linha = tblTurmas.getSelectedRow();
            if (linha < 0) {
                JOptionPane.showMessageDialog(this, "Selecione uma turma na tabela.");
                return;
            }
            modeloTurmas.removeRow(linha);
        });
    }

    private void limparFormulario() {
        txtCodigo.setText("");
        cbDisciplina.setSelectedIndex(0);
        cbSemestre.setSelectedIndex(0);
        cbProfessor.setSelectedIndex(0);
        spnVagas.setValue(40);
        tblTurmas.clearSelection();
    }
}
