package br.edu.universidade.view;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Tela de Aluno: cadastrar, listar, editar e excluir alunos.
 * Sprint 1 (task #15, P7): usa dados falsos. Ligação com o núcleo na task #19.
 */
public class AlunoPanel extends JPanel {

    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // Colunas: Matrícula, Nome, CPF, E-mail, Ingresso
    private static final Object[][] ALUNOS_FALSOS = {
            {"2026BSI4821", DadosFalsos.ALUNOS[0], "111.222.333-44", "joao@ufu.br", "10/02/2026"},
            {"2026BSI1307", DadosFalsos.ALUNOS[1], "222.333.444-55", "maria@ufu.br", "10/02/2026"},
            {"2026BCC5590", DadosFalsos.ALUNOS[2], "333.444.555-66", "lucas@ufu.br", "11/02/2026"},
            {"2025BCC0042", DadosFalsos.ALUNOS[3], "444.555.666-77", "beatriz@ufu.br", "05/08/2025"},
            {"2025MAT7714", DadosFalsos.ALUNOS[4], "555.666.777-88", "pedro@ufu.br", "05/08/2025"}
    };

    // Formulário
    private final JTextField txtMatricula = new JTextField(12);
    private final JTextField txtNome = new JTextField(25);
    private final JTextField txtCpf = new JTextField(14);
    private final JTextField txtEmail = new JTextField(25);

    // Botões
    private final JButton btnSalvar = Estilo.botaoPrimario("Salvar");
    private final JButton btnLimpar = Estilo.botaoSecundario("Limpar");
    private final JButton btnExcluir = Estilo.botaoPerigo("Excluir");

    // Tabela
    private final DefaultTableModel modeloAlunos =
            Estilo.modeloTabela("Matrícula", "Nome", "CPF", "E-mail", "Ingresso");
    private final JTable tblAlunos = Estilo.tabela(modeloAlunos);

    public AlunoPanel() {
        setLayout(new BorderLayout());
        JPanel tela = Estilo.painelTela();
        add(tela, BorderLayout.CENTER);

        tela.add(Estilo.titulo("Alunos"), BorderLayout.NORTH);

        JPanel formulario = Estilo.formulario("Cadastro de aluno");
        Estilo.adicionarCampo(formulario, 0, "Matrícula", txtMatricula);
        Estilo.adicionarCampo(formulario, 1, "Nome", txtNome);
        Estilo.adicionarCampo(formulario, 2, "CPF", txtCpf);
        Estilo.adicionarCampo(formulario, 3, "E-mail", txtEmail);

        JPanel topo = new JPanel(new BorderLayout());
        topo.setBackground(Estilo.COR_FUNDO);
        topo.add(formulario, BorderLayout.CENTER);
        topo.add(Estilo.barraBotoes(btnSalvar, btnLimpar, btnExcluir), BorderLayout.SOUTH);

        JScrollPane scrollTabela = new JScrollPane(tblAlunos);
        scrollTabela.setBorder(BorderFactory.createTitledBorder("Alunos cadastrados"));

        JPanel centro = new JPanel(new BorderLayout(10, 10));
        centro.setBackground(Estilo.COR_FUNDO);
        centro.add(topo, BorderLayout.NORTH);
        centro.add(scrollTabela, BorderLayout.CENTER);
        tela.add(centro, BorderLayout.CENTER);

        carregarDadosFalsos();
        configurarAcoes();
    }

    private void carregarDadosFalsos() {
        for (Object[] aluno : ALUNOS_FALSOS) {
            modeloAlunos.addRow(aluno);
        }
    }

    private void configurarAcoes() {
        // TODO task #19: chamar Universidade em vez de mexer só na tabela
        btnSalvar.addActionListener(e -> {
            if (txtMatricula.getText().isBlank() || txtNome.getText().isBlank()
                    || txtCpf.getText().isBlank() || txtEmail.getText().isBlank()) {
                JOptionPane.showMessageDialog(this, "Preencha todos os campos.",
                        "Campos obrigatórios", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int linha = tblAlunos.getSelectedRow();
            if (linha >= 0) { // editando um aluno existente (matrícula não muda)
                modeloAlunos.setValueAt(txtNome.getText().trim(), linha, 1);
                modeloAlunos.setValueAt(txtCpf.getText().trim(), linha, 2);
                modeloAlunos.setValueAt(txtEmail.getText().trim(), linha, 3);
            } else {          // cadastrando um novo
                modeloAlunos.addRow(new Object[]{
                        txtMatricula.getText().trim(), txtNome.getText().trim(),
                        txtCpf.getText().trim(), txtEmail.getText().trim(),
                        LocalDate.now().format(FORMATO_DATA)});
            }
            limparFormulario();
        });

        btnLimpar.addActionListener(e -> limparFormulario());

        btnExcluir.addActionListener(e -> {
            int linha = tblAlunos.getSelectedRow();
            if (linha < 0) {
                JOptionPane.showMessageDialog(this, "Selecione um aluno na tabela.");
                return;
            }
            int resposta = JOptionPane.showConfirmDialog(this,
                    "Excluir o aluno " + modeloAlunos.getValueAt(linha, 1) + "?",
                    "Confirmar exclusão", JOptionPane.YES_NO_OPTION);
            if (resposta == JOptionPane.YES_OPTION) {
                modeloAlunos.removeRow(linha);
                limparFormulario();
            }
        });

        // Selecionar na tabela -> carrega no formulário para editar
        tblAlunos.getSelectionModel().addListSelectionListener(e -> {
            int linha = tblAlunos.getSelectedRow();
            if (e.getValueIsAdjusting() || linha < 0) {
                return;
            }
            txtMatricula.setText(String.valueOf(modeloAlunos.getValueAt(linha, 0)));
            txtMatricula.setEditable(false);
            txtNome.setText(String.valueOf(modeloAlunos.getValueAt(linha, 1)));
            txtCpf.setText(String.valueOf(modeloAlunos.getValueAt(linha, 2)));
            txtEmail.setText(String.valueOf(modeloAlunos.getValueAt(linha, 3)));
        });
    }

    private void limparFormulario() {
        txtMatricula.setText("");
        txtMatricula.setEditable(true);
        txtNome.setText("");
        txtCpf.setText("");
        txtEmail.setText("");
        tblAlunos.clearSelection();
    }
}
