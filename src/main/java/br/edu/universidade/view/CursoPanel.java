package br.edu.universidade.view;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;

/**
 * Tela de Curso: cadastrar/listar/excluir cursos e
 * adicionar/remover disciplinas de um curso.
 * Sprint 1: usa dados falsos (DadosFalsos). Ligação com o núcleo na Sprint 2.
 */
public class CursoPanel extends JPanel {

    // Formulário
    private final JTextField txtCodigo = new JTextField(10);
    private final JTextField txtNome = new JTextField(25);
    private final JSpinner spnCargaHoraria = new JSpinner(new SpinnerNumberModel(3000, 0, 10000, 60));

    // Botões do cadastro
    private final JButton btnSalvar = Estilo.botaoPrimario("Salvar");
    private final JButton btnLimpar = Estilo.botaoSecundario("Limpar");
    private final JButton btnExcluir = Estilo.botaoPerigo("Excluir");

    // Tabela de cursos
    private final DefaultTableModel modeloCursos =
            Estilo.modeloTabela("Código", "Nome", "Carga horária (h)");
    private final JTable tblCursos = Estilo.tabela(modeloCursos);

    // Disciplinas do curso selecionado
    private final JComboBox<String> cbDisciplina = new JComboBox<>(DadosFalsos.codigosDisciplinas());
    private final DefaultListModel<String> modeloDisciplinasCurso = new DefaultListModel<>();
    private final JList<String> lstDisciplinasCurso = new JList<>(modeloDisciplinasCurso);
    private final JButton btnAdicionarDisciplina = Estilo.botaoPrimario("Adicionar disciplina");
    private final JButton btnRemoverDisciplina = Estilo.botaoPerigo("Remover disciplina");

    public CursoPanel() {
        setLayout(new BorderLayout());
        JPanel tela = Estilo.painelTela();
        add(tela, BorderLayout.CENTER);

        tela.add(Estilo.titulo("Cursos"), BorderLayout.NORTH);

        // ----- Formulário + botões (topo) -----
        JPanel formulario = Estilo.formulario("Cadastro de curso");
        Estilo.adicionarCampo(formulario, 0, "Código", txtCodigo);
        Estilo.adicionarCampo(formulario, 1, "Nome", txtNome);
        spnCargaHoraria.setEditor(new JSpinner.NumberEditor(spnCargaHoraria, "#")); // sem "3,000"
        Estilo.adicionarCampo(formulario, 2, "Carga horária (h)", spnCargaHoraria);

        JPanel topo = new JPanel(new BorderLayout());
        topo.setBackground(Estilo.COR_FUNDO);
        topo.add(formulario, BorderLayout.CENTER);
        topo.add(Estilo.barraBotoes(btnSalvar, btnLimpar, btnExcluir), BorderLayout.SOUTH);

        // ----- Disciplinas do curso (direita) -----
        JPanel painelDisciplinas = Estilo.formulario("Disciplinas do curso selecionado");
        painelDisciplinas.setLayout(new BorderLayout(5, 5));
        JPanel linhaAdicionar = new JPanel(new GridLayout(1, 1));
        linhaAdicionar.add(cbDisciplina);
        painelDisciplinas.add(linhaAdicionar, BorderLayout.NORTH);
        lstDisciplinasCurso.setFont(Estilo.FONTE_PADRAO);
        painelDisciplinas.add(new JScrollPane(lstDisciplinasCurso), BorderLayout.CENTER);
        painelDisciplinas.add(Estilo.barraBotoes(btnAdicionarDisciplina, btnRemoverDisciplina),
                BorderLayout.SOUTH);
        painelDisciplinas.setPreferredSize(new Dimension(380, 0));

        // ----- Centro: tabela + disciplinas -----
        JScrollPane scrollTabela = new JScrollPane(tblCursos);
        scrollTabela.setBorder(BorderFactory.createTitledBorder("Cursos cadastrados"));

        JPanel centro = new JPanel(new BorderLayout(10, 10));
        centro.setBackground(Estilo.COR_FUNDO);
        centro.add(topo, BorderLayout.NORTH);
        centro.add(scrollTabela, BorderLayout.CENTER);
        centro.add(painelDisciplinas, BorderLayout.EAST);
        tela.add(centro, BorderLayout.CENTER);

        carregarDadosFalsos();
        configurarAcoes();
    }

    private void carregarDadosFalsos() {
        for (Object[] curso : DadosFalsos.CURSOS) {
            modeloCursos.addRow(curso);
        }
    }

    private void configurarAcoes() {
        // TODO Sprint 2: chamar o service de Curso em vez de mexer só na tabela
        btnSalvar.addActionListener(e -> {
            if (txtCodigo.getText().isBlank() || txtNome.getText().isBlank()) {
                JOptionPane.showMessageDialog(this, "Preencha código e nome.");
                return;
            }
            modeloCursos.addRow(new Object[]{
                    txtCodigo.getText(), txtNome.getText(), spnCargaHoraria.getValue()});
            limparFormulario();
        });

        btnLimpar.addActionListener(e -> limparFormulario());

        btnExcluir.addActionListener(e -> {
            int linha = tblCursos.getSelectedRow();
            if (linha < 0) {
                JOptionPane.showMessageDialog(this, "Selecione um curso na tabela.");
                return;
            }
            modeloCursos.removeRow(linha);
            modeloDisciplinasCurso.clear();
        });

        // Ao selecionar um curso, preenche o formulário e mostra disciplinas (falsas)
        tblCursos.getSelectionModel().addListSelectionListener(e -> {
            int linha = tblCursos.getSelectedRow();
            if (e.getValueIsAdjusting() || linha < 0) {
                return;
            }
            txtCodigo.setText(String.valueOf(modeloCursos.getValueAt(linha, 0)));
            txtNome.setText(String.valueOf(modeloCursos.getValueAt(linha, 1)));
            spnCargaHoraria.setValue(modeloCursos.getValueAt(linha, 2));
            modeloDisciplinasCurso.clear();
            if (linha == 0) { // só o primeiro curso tem disciplinas no mock
                modeloDisciplinasCurso.addElement("POO");
                modeloDisciplinasCurso.addElement("ED2");
            }
        });

        btnAdicionarDisciplina.addActionListener(e -> {
            if (tblCursos.getSelectedRow() < 0) {
                JOptionPane.showMessageDialog(this, "Selecione um curso na tabela.");
                return;
            }
            String disciplina = (String) cbDisciplina.getSelectedItem();
            if (!modeloDisciplinasCurso.contains(disciplina)) {
                modeloDisciplinasCurso.addElement(disciplina);
            }
        });

        btnRemoverDisciplina.addActionListener(e -> {
            int indice = lstDisciplinasCurso.getSelectedIndex();
            if (indice >= 0) {
                modeloDisciplinasCurso.remove(indice);
            }
        });
    }

    private void limparFormulario() {
        txtCodigo.setText("");
        txtNome.setText("");
        spnCargaHoraria.setValue(3000);
        tblCursos.clearSelection();
    }
}
