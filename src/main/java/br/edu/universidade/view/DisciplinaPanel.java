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
 * Tela de Disciplina: cadastrar, listar, editar e excluir disciplinas.
 * Sprint 1: usa dados falsos (DadosFalsos). Ligação com o núcleo na Sprint 2.
 */
public class DisciplinaPanel extends JPanel {

    // Formulário
    private final JTextField txtCodigo = new JTextField(10);
    private final JTextField txtNome = new JTextField(25);
    private final JSpinner spnCargaHoraria = new JSpinner(new SpinnerNumberModel(60, 0, 500, 15));
    private final JComboBox<String> cbDepartamento = new JComboBox<>(DadosFalsos.DEPARTAMENTOS);

    // Botões
    private final JButton btnSalvar = Estilo.botaoPrimario("Salvar");
    private final JButton btnLimpar = Estilo.botaoSecundario("Limpar");
    private final JButton btnExcluir = Estilo.botaoPerigo("Excluir");

    // Tabela
    private final DefaultTableModel modeloDisciplinas =
            Estilo.modeloTabela("Código", "Nome", "Carga horária (h)", "Departamento");
    private final JTable tblDisciplinas = Estilo.tabela(modeloDisciplinas);

    public DisciplinaPanel() {
        setLayout(new BorderLayout());
        JPanel tela = Estilo.painelTela();
        add(tela, BorderLayout.CENTER);

        tela.add(Estilo.titulo("Disciplinas"), BorderLayout.NORTH);

        JPanel formulario = Estilo.formulario("Cadastro de disciplina");
        Estilo.adicionarCampo(formulario, 0, "Código", txtCodigo);
        Estilo.adicionarCampo(formulario, 1, "Nome", txtNome);
        Estilo.adicionarCampo(formulario, 2, "Carga horária (h)", spnCargaHoraria);
        Estilo.adicionarCampo(formulario, 3, "Departamento", cbDepartamento);

        JPanel topo = new JPanel(new BorderLayout());
        topo.setBackground(Estilo.COR_FUNDO);
        topo.add(formulario, BorderLayout.CENTER);
        topo.add(Estilo.barraBotoes(btnSalvar, btnLimpar, btnExcluir), BorderLayout.SOUTH);

        JScrollPane scrollTabela = new JScrollPane(tblDisciplinas);
        scrollTabela.setBorder(BorderFactory.createTitledBorder("Disciplinas cadastradas"));

        JPanel centro = new JPanel(new BorderLayout(10, 10));
        centro.setBackground(Estilo.COR_FUNDO);
        centro.add(topo, BorderLayout.NORTH);
        centro.add(scrollTabela, BorderLayout.CENTER);
        tela.add(centro, BorderLayout.CENTER);

        carregarDadosFalsos();
        configurarAcoes();
    }

    private void carregarDadosFalsos() {
        for (Object[] disciplina : DadosFalsos.DISCIPLINAS) {
            modeloDisciplinas.addRow(disciplina);
        }
    }

    private void configurarAcoes() {
        // TODO Sprint 2: chamar o service de Disciplina em vez de mexer só na tabela
        btnSalvar.addActionListener(e -> {
            if (txtCodigo.getText().isBlank() || txtNome.getText().isBlank()) {
                JOptionPane.showMessageDialog(this, "Preencha código e nome.");
                return;
            }
            Object[] linhaNova = {txtCodigo.getText(), txtNome.getText(),
                    spnCargaHoraria.getValue(), cbDepartamento.getSelectedItem()};

            int linha = tblDisciplinas.getSelectedRow();
            if (linha >= 0) { // editando uma disciplina existente
                for (int col = 0; col < linhaNova.length; col++) {
                    modeloDisciplinas.setValueAt(linhaNova[col], linha, col);
                }
            } else {          // cadastrando uma nova
                modeloDisciplinas.addRow(linhaNova);
            }
            limparFormulario();
        });

        btnLimpar.addActionListener(e -> limparFormulario());

        btnExcluir.addActionListener(e -> {
            int linha = tblDisciplinas.getSelectedRow();
            if (linha < 0) {
                JOptionPane.showMessageDialog(this, "Selecione uma disciplina na tabela.");
                return;
            }
            modeloDisciplinas.removeRow(linha);
            limparFormulario();
        });

        // Selecionar na tabela -> carrega no formulário para editar
        tblDisciplinas.getSelectionModel().addListSelectionListener(e -> {
            int linha = tblDisciplinas.getSelectedRow();
            if (e.getValueIsAdjusting() || linha < 0) {
                return;
            }
            txtCodigo.setText(String.valueOf(modeloDisciplinas.getValueAt(linha, 0)));
            txtNome.setText(String.valueOf(modeloDisciplinas.getValueAt(linha, 1)));
            spnCargaHoraria.setValue(modeloDisciplinas.getValueAt(linha, 2));
            cbDepartamento.setSelectedItem(modeloDisciplinas.getValueAt(linha, 3));
        });
    }

    private void limparFormulario() {
        txtCodigo.setText("");
        txtNome.setText("");
        spnCargaHoraria.setValue(60);
        cbDepartamento.setSelectedIndex(0);
        tblDisciplinas.clearSelection();
    }
}
