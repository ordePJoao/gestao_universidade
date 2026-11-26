package br.edu.universidade.view;

import br.edu.universidade.model.Departamento;
import br.edu.universidade.model.Professor;
import br.edu.universidade.model.Universidade;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

/**
 * Tela de Departamento: cadastrar, listar, editar e excluir departamentos e
 * vincular/remover professores de um departamento (Issue #34 / #19.3).
 */
public class DepartamentoPanel extends JPanel implements Atualizavel {

    private final Universidade universidade;

    /** Departamentos na mesma ordem das linhas da tabela. */
    private final List<Departamento> departamentosNaTabela = new ArrayList<>();

    // Formulário
    private final JTextField txtNome = new JTextField(25);

    // Botões do cadastro
    private final JButton btnSalvar = Estilo.botaoPrimario("Salvar");
    private final JButton btnLimpar = Estilo.botaoSecundario("Limpar");
    private final JButton btnExcluir = Estilo.botaoPerigo("Excluir");

    // Tabela de departamentos (esquerda)
    private final DefaultTableModel modeloDepartamentos =
            Estilo.modeloTabela("Nome", "Professores");
    private final JTable tblDepartamentos = Estilo.tabela(modeloDepartamentos);

    // Professores do departamento selecionado (direita)
    private final JComboBox<Professor> cbProfessor = new JComboBox<>();
    private final DefaultListModel<Professor> modeloProfessoresDepto = new DefaultListModel<>();
    private final JList<Professor> lstProfessoresDepto = new JList<>(modeloProfessoresDepto);
    private final JButton btnAdicionarProfessor = Estilo.botaoPrimario("Adicionar professor");
    private final JButton btnRemoverProfessor = Estilo.botaoPerigo("Remover professor");

    public DepartamentoPanel(Universidade universidade) {
        this.universidade = universidade;

        setLayout(new BorderLayout());
        JPanel tela = Estilo.painelTela();
        add(tela, BorderLayout.CENTER);

        tela.add(Estilo.titulo("Departamentos"), BorderLayout.NORTH);

        // ----- Formulário + botões (topo) -----
        JPanel formulario = Estilo.formulario("Cadastro de departamento");
        Estilo.adicionarCampo(formulario, 0, "Nome", txtNome);

        JPanel topo = new JPanel(new BorderLayout());
        topo.setBackground(Estilo.COR_FUNDO);
        topo.add(formulario, BorderLayout.CENTER);
        topo.add(Estilo.barraBotoes(btnSalvar, btnLimpar, btnExcluir), BorderLayout.SOUTH);

        // ----- Professores vinculados (direita) -----
        Componentes.textoDoCombo(cbProfessor, p -> p.getSiape() + " - " + p.getNome());
        lstProfessoresDepto.setFont(Estilo.FONTE_PADRAO);
        lstProfessoresDepto.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Professor p) {
                    setText(p.getSiape() + " - " + p.getNome());
                }
                return this;
            }
        });

        JPanel painelProfessores = Estilo.formulario("Professores do departamento selecionado");
        painelProfessores.setLayout(new BorderLayout(5, 5));
        painelProfessores.add(cbProfessor, BorderLayout.NORTH);
        painelProfessores.add(new JScrollPane(lstProfessoresDepto), BorderLayout.CENTER);
        painelProfessores.add(Estilo.barraBotoes(btnAdicionarProfessor, btnRemoverProfessor),
                BorderLayout.SOUTH);
        painelProfessores.setPreferredSize(new Dimension(400, 0));

        // ----- Centro: tabela + professores -----
        JScrollPane scrollTabela = new JScrollPane(tblDepartamentos);
        scrollTabela.setBorder(BorderFactory.createTitledBorder("Departamentos cadastrados"));

        JPanel centro = new JPanel(new BorderLayout(10, 10));
        centro.setBackground(Estilo.COR_FUNDO);
        centro.add(topo, BorderLayout.NORTH);
        centro.add(scrollTabela, BorderLayout.CENTER);
        centro.add(painelProfessores, BorderLayout.EAST);
        tela.add(centro, BorderLayout.CENTER);

        configurarAcoes();
        limparFormulario();
        atualizar();
    }

    /** Recarrega a tabela e o combo de professores a partir da Universidade. */
    @Override
    public void atualizar() {
        Departamento selecionado = departamentoSelecionado();

        modeloDepartamentos.setRowCount(0);
        departamentosNaTabela.clear();
        for (Departamento depto : universidade.getDepartamentos()) {
            departamentosNaTabela.add(depto);
            modeloDepartamentos.addRow(new Object[]{
                    depto.getNome(),
                    depto.getProfessores().size()});
        }
        Componentes.recarregarCombo(cbProfessor, universidade.getProfessores());

        int linha = departamentosNaTabela.indexOf(selecionado);
        if (linha >= 0) {
            tblDepartamentos.setRowSelectionInterval(linha, linha);
        }
        atualizarProfessoresDoDepartamento();
    }

    private void configurarAcoes() {
        btnSalvar.addActionListener(e -> salvar());
        btnLimpar.addActionListener(e -> limparFormulario());
        btnExcluir.addActionListener(e -> excluir());
        btnAdicionarProfessor.addActionListener(e -> adicionarProfessor());
        btnRemoverProfessor.addActionListener(e -> removerProfessor());

        tblDepartamentos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                preencherFormulario(departamentoSelecionado());
                atualizarProfessoresDoDepartamento();
            }
        });
    }

    // ---------- Ações do departamento ----------

    private void salvar() {
        String nome = txtNome.getText().trim();
        if (nome.isEmpty()) {
            Componentes.aviso(this, "Informe o nome do departamento.", "Campo obrigatório");
            return;
        }

        Departamento editando = departamentoSelecionado();
        if (nomeEmUso(nome, editando)) {
            Componentes.aviso(this, "Já existe um departamento com o nome \"" + nome + "\".",
                    "Nome duplicado");
            return;
        }

        try {
            if (editando != null) {
                editando.setNome(nome);
            } else {
                universidade.adicionarDepartamento(new Departamento(nome));
            }
        } catch (Exception ex) {
            Componentes.mostrarErro(this, ex);
            return;
        }
        limparFormulario();
        atualizar();
    }

    private void excluir() {
        Departamento depto = departamentoSelecionado();
        if (depto == null) {
            Componentes.aviso(this, "Selecione um departamento na tabela.", "Excluir departamento");
            return;
        }
        if (!Componentes.confirmar(this, "Excluir o departamento \"" + depto.getNome() + "\"?\n"
                + "Os professores continuam cadastrados, só deixam de fazer parte do departamento.",
                "Confirmar exclusão")) {
            return;
        }
        try {
            universidade.removerDepartamento(depto);
        } catch (Exception ex) {
            Componentes.mostrarErro(this, ex);
            return;
        }
        limparFormulario();
        atualizar();
    }

    // ---------- Professores do departamento ----------

    private void adicionarProfessor() {
        Departamento depto = departamentoSelecionado();
        Professor professor = (Professor) cbProfessor.getSelectedItem();
        if (depto == null) {
            Componentes.aviso(this, "Selecione um departamento na tabela.", "Adicionar professor");
            return;
        }
        if (professor == null) {
            Componentes.aviso(this, "Cadastre um professor na aba Professores primeiro.",
                    "Nenhum professor");
            return;
        }
        if (depto.getProfessores().contains(professor)) {
            Componentes.aviso(this, professor.getNome() + " já faz parte do departamento "
                    + depto.getNome() + ".", "Professor repetido");
            return;
        }
        try {
            depto.adicionarProfessor(professor);
        } catch (Exception ex) {
            Componentes.mostrarErro(this, ex);
            return;
        }
        atualizar();
    }

    private void removerProfessor() {
        Departamento depto = departamentoSelecionado();
        Professor professor = lstProfessoresDepto.getSelectedValue();
        if (depto == null || professor == null) {
            Componentes.aviso(this, "Selecione um departamento e um professor da lista.",
                    "Remover professor");
            return;
        }
        depto.removerProfessor(professor);
        atualizar();
    }

    private void atualizarProfessoresDoDepartamento() {
        modeloProfessoresDepto.clear();
        Departamento depto = departamentoSelecionado();
        if (depto != null) {
            for (Professor p : depto.getProfessores()) {
                modeloProfessoresDepto.addElement(p);
            }
        }
    }

    // ---------- Auxiliares ----------

    private Departamento departamentoSelecionado() {
        int linha = tblDepartamentos.getSelectedRow();
        return linha >= 0 && linha < departamentosNaTabela.size()
                ? departamentosNaTabela.get(tblDepartamentos.convertRowIndexToModel(linha)) : null;
    }

    private boolean nomeEmUso(String nome, Departamento ignorar) {
        for (Departamento d : universidade.getDepartamentos()) {
            if (d != ignorar && d.getNome() != null && d.getNome().equalsIgnoreCase(nome)) {
                return true;
            }
        }
        return false;
    }

    private void preencherFormulario(Departamento depto) {
        if (depto == null) {
            return;
        }
        txtNome.setText(depto.getNome());
    }

    private void limparFormulario() {
        tblDepartamentos.clearSelection();
        txtNome.setText("");
        modeloProfessoresDepto.clear();
        txtNome.requestFocusInWindow();
    }
}