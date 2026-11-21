package br.edu.universidade.view;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;

/**
 * Tela de Professor: cadastrar, listar, editar e excluir professores.
 * Sprint 1 (task #15, P7): usa dados falsos. Ligação com o núcleo na task #19.
 */
public class ProfessorPanel extends JPanel {

    static final String[] TITULACOES = {"Graduado", "Especialista", "Mestre", "Doutor", "Pós-Doutor"};

    // Colunas: SIAPE, Nome, CPF, E-mail, Titulação
    private static final Object[][] PROFESSORES_FALSOS = {
            {"1234567", DadosFalsos.PROFESSORES[0], "999.888.777-66", "ana.souza@ufu.br", "Doutor"},
            {"2345678", DadosFalsos.PROFESSORES[1], "888.777.666-55", "carlos.lima@ufu.br", "Mestre"},
            {"3456789", DadosFalsos.PROFESSORES[2], "777.666.555-44", "marina.rocha@ufu.br", "Doutor"},
            {"4567890", DadosFalsos.PROFESSORES[3], "666.555.444-33", "paulo.mendes@ufu.br", "Especialista"}
    };

    // Formulário
    private final JTextField txtSiape = new JTextField(10);
    private final JTextField txtNome = new JTextField(25);
    private final JTextField txtCpf = new JTextField(14);
    private final JTextField txtEmail = new JTextField(25);
    private final JComboBox<String> cbTitulacao = new JComboBox<>(TITULACOES);

    // Botões
    private final JButton btnSalvar = Estilo.botaoPrimario("Salvar");
    private final JButton btnLimpar = Estilo.botaoSecundario("Limpar");
    private final JButton btnExcluir = Estilo.botaoPerigo("Excluir");

    // Tabela
    private final DefaultTableModel modeloProfessores =
            Estilo.modeloTabela("SIAPE", "Nome", "CPF", "E-mail", "Titulação");
    private final JTable tblProfessores = Estilo.tabela(modeloProfessores);

    public ProfessorPanel() {
        setLayout(new BorderLayout());
        JPanel tela = Estilo.painelTela();
        add(tela, BorderLayout.CENTER);

        tela.add(Estilo.titulo("Professores"), BorderLayout.NORTH);

        JPanel formulario = Estilo.formulario("Cadastro de professor");
        Estilo.adicionarCampo(formulario, 0, "SIAPE", txtSiape);
        Estilo.adicionarCampo(formulario, 1, "Nome", txtNome);
        Estilo.adicionarCampo(formulario, 2, "CPF", txtCpf);
        Estilo.adicionarCampo(formulario, 3, "E-mail", txtEmail);
        Estilo.adicionarCampo(formulario, 4, "Titulação", cbTitulacao);

        JPanel topo = new JPanel(new BorderLayout());
        topo.setBackground(Estilo.COR_FUNDO);
        topo.add(formulario, BorderLayout.CENTER);
        topo.add(Estilo.barraBotoes(btnSalvar, btnLimpar, btnExcluir), BorderLayout.SOUTH);

        JScrollPane scrollTabela = new JScrollPane(tblProfessores);
        scrollTabela.setBorder(BorderFactory.createTitledBorder("Professores cadastrados"));

        JPanel centro = new JPanel(new BorderLayout(10, 10));
        centro.setBackground(Estilo.COR_FUNDO);
        centro.add(topo, BorderLayout.NORTH);
        centro.add(scrollTabela, BorderLayout.CENTER);
        tela.add(centro, BorderLayout.CENTER);

        carregarDadosFalsos();
        configurarAcoes();
    }

    private void carregarDadosFalsos() {
        for (Object[] professor : PROFESSORES_FALSOS) {
            modeloProfessores.addRow(professor);
        }
    }

    private void configurarAcoes() {
        // TODO task #19: chamar Universidade em vez de mexer só na tabela
        btnSalvar.addActionListener(e -> {
            if (txtSiape.getText().isBlank() || txtNome.getText().isBlank()
                    || txtCpf.getText().isBlank() || txtEmail.getText().isBlank()) {
                JOptionPane.showMessageDialog(this, "Preencha todos os campos.",
                        "Campos obrigatórios", JOptionPane.WARNING_MESSAGE);
                return;
            }
            Object[] linhaNova = {txtSiape.getText().trim(), txtNome.getText().trim(),
                    txtCpf.getText().trim(), txtEmail.getText().trim(), cbTitulacao.getSelectedItem()};

            int linha = tblProfessores.getSelectedRow();
            if (linha >= 0) { // editando um professor existente
                for (int col = 0; col < linhaNova.length; col++) {
                    modeloProfessores.setValueAt(linhaNova[col], linha, col);
                }
            } else {          // cadastrando um novo
                modeloProfessores.addRow(linhaNova);
            }
            limparFormulario();
        });

        btnLimpar.addActionListener(e -> limparFormulario());

        btnExcluir.addActionListener(e -> {
            int linha = tblProfessores.getSelectedRow();
            if (linha < 0) {
                JOptionPane.showMessageDialog(this, "Selecione um professor na tabela.");
                return;
            }
            int resposta = JOptionPane.showConfirmDialog(this,
                    "Excluir o professor " + modeloProfessores.getValueAt(linha, 1) + "?",
                    "Confirmar exclusão", JOptionPane.YES_NO_OPTION);
            if (resposta == JOptionPane.YES_OPTION) {
                modeloProfessores.removeRow(linha);
                limparFormulario();
            }
        });

        // Selecionar na tabela -> carrega no formulário para editar
        tblProfessores.getSelectionModel().addListSelectionListener(e -> {
            int linha = tblProfessores.getSelectedRow();
            if (e.getValueIsAdjusting() || linha < 0) {
                return;
            }
            txtSiape.setText(String.valueOf(modeloProfessores.getValueAt(linha, 0)));
            txtSiape.setEditable(false);
            txtNome.setText(String.valueOf(modeloProfessores.getValueAt(linha, 1)));
            txtCpf.setText(String.valueOf(modeloProfessores.getValueAt(linha, 2)));
            txtEmail.setText(String.valueOf(modeloProfessores.getValueAt(linha, 3)));
            cbTitulacao.setSelectedItem(modeloProfessores.getValueAt(linha, 4));
        });
    }

    private void limparFormulario() {
        txtSiape.setText("");
        txtSiape.setEditable(true);
        txtNome.setText("");
        txtCpf.setText("");
        txtEmail.setText("");
        cbTitulacao.setSelectedIndex(0);
        tblProfessores.clearSelection();
    }
}
