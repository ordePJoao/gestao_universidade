package br.edu.universidade.view;

import br.edu.universidade.exception.UniversidadeException;
import br.edu.universidade.model.Professor;
import br.edu.universidade.model.Universidade;

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
import java.util.ArrayList;
import java.util.List;

/**
 * Tela de Professor: cadastrar, listar, editar e excluir professores (tasks #15 e #19, P7).
 * Os dados vêm da instância única de {@link Universidade} recebida da TelaPrincipal.
 */
public class ProfessorPanel extends JPanel implements Atualizavel {

    private static final String[] TITULACOES = {"Graduado", "Especialista", "Mestre", "Doutor", "Pós-Doutor"};

    private final Universidade universidade;

    /** Professores na mesma ordem das linhas da tabela. */
    private final List<Professor> professoresNaTabela = new ArrayList<>();

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
            Estilo.modeloTabela("SIAPE", "Nome", "CPF", "E-mail", "Titulação", "Turmas");
    private final JTable tblProfessores = Estilo.tabela(modeloProfessores);

    public ProfessorPanel(Universidade universidade) {
        this.universidade = universidade;

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

        configurarAcoes();
        limparFormulario();
        atualizar();
    }

    /** Recarrega a tabela a partir da Universidade. */
    @Override
    public void atualizar() {
        modeloProfessores.setRowCount(0);
        professoresNaTabela.clear();
        for (Professor professor : universidade.getProfessores()) {
            professoresNaTabela.add(professor);
            modeloProfessores.addRow(new Object[]{
                    professor.getSiape(),
                    professor.getNome(),
                    professor.getCpf(),
                    professor.getEmail(),
                    professor.getTitulacao(),
                    professor.getTurmas().size()});
        }
    }

    private void configurarAcoes() {
        btnSalvar.addActionListener(e -> salvar());
        btnLimpar.addActionListener(e -> limparFormulario());
        btnExcluir.addActionListener(e -> excluir());

        // Selecionar na tabela -> carrega no formulário para editar
        tblProfessores.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                preencherFormulario(professorSelecionado());
            }
        });
    }

    // ---------- Ações ----------

    private void salvar() {
        String siape = txtSiape.getText().trim();
        String nome = txtNome.getText().trim();
        String cpf = txtCpf.getText().trim();
        String email = txtEmail.getText().trim();
        String titulacao = (String) cbTitulacao.getSelectedItem();

        if (siape.isEmpty() || nome.isEmpty() || cpf.isEmpty() || email.isEmpty()) {
            aviso("Preencha todos os campos.", "Campos obrigatórios");
            return;
        }
        if (!email.contains("@")) {
            aviso("Informe um e-mail válido.", "E-mail inválido");
            return;
        }

        Professor editando = professorSelecionado();
        if (cpfEmUso(cpf, editando)) {
            aviso("Já existe um professor com o CPF " + cpf + ".", "CPF duplicado");
            return;
        }

        try {
            if (editando != null) { // edição: o SIAPE (identificador) não muda
                editando.setNome(nome);
                editando.setCpf(cpf);
                editando.setEmail(email);
                editando.setTitulacao(titulacao);
            } else {
                if (buscarPorSiape(siape) != null) {
                    aviso("Já existe um professor com o SIAPE " + siape + ".", "SIAPE duplicado");
                    return;
                }
                universidade.cadastrarProfessor(new Professor(nome, cpf, email, siape, titulacao));
            }
        } catch (Exception ex) {
            mostrarErro(ex);
            return;
        }
        atualizar();
        limparFormulario();
    }

    private void excluir() {
        Professor professor = professorSelecionado();
        if (professor == null) {
            aviso("Selecione um professor na tabela.", "Excluir professor");
            return;
        }
        if (!professor.getTurmas().isEmpty()) {
            aviso("O professor " + professor.getNome() + " ministra " + professor.getTurmas().size()
                    + " turma(s).\nTroque o professor dessas turmas antes de excluir.",
                    "Não é possível excluir");
            return;
        }
        int resposta = JOptionPane.showConfirmDialog(this,
                "Excluir o professor " + professor.getNome() + " (SIAPE " + professor.getSiape() + ")?",
                "Confirmar exclusão", JOptionPane.YES_NO_OPTION);
        if (resposta != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            universidade.removerProfessor(professor);
        } catch (Exception ex) {
            mostrarErro(ex);
            return;
        }
        atualizar();
        limparFormulario();
    }

    // ---------- Auxiliares ----------

    private Professor professorSelecionado() {
        int linha = tblProfessores.getSelectedRow();
        return linha >= 0 ? professoresNaTabela.get(tblProfessores.convertRowIndexToModel(linha)) : null;
    }

    private Professor buscarPorSiape(String siape) {
        for (Professor p : universidade.getProfessores()) {
            if (p.getSiape().equalsIgnoreCase(siape)) {
                return p;
            }
        }
        return null;
    }

    /** CPF já usado por outro professor (ignora o próprio professor em edição e a pontuação). */
    private boolean cpfEmUso(String cpf, Professor ignorar) {
        String digitos = cpf.replaceAll("\\D", "");
        for (Professor p : universidade.getProfessores()) {
            if (p != ignorar && p.getCpf() != null && p.getCpf().replaceAll("\\D", "").equals(digitos)) {
                return true;
            }
        }
        return false;
    }

    private void preencherFormulario(Professor professor) {
        if (professor == null) {
            return;
        }
        txtSiape.setText(professor.getSiape());
        txtSiape.setEditable(false);
        txtNome.setText(professor.getNome());
        txtCpf.setText(professor.getCpf());
        txtEmail.setText(professor.getEmail());
        cbTitulacao.setSelectedItem(professor.getTitulacao());
    }

    private void limparFormulario() {
        tblProfessores.clearSelection();
        txtSiape.setText("");
        txtSiape.setEditable(true);
        txtNome.setText("");
        txtCpf.setText("");
        txtEmail.setText("");
        cbTitulacao.setSelectedIndex(0);
        txtSiape.requestFocusInWindow();
    }

    private void aviso(String mensagem, String titulo) {
        JOptionPane.showMessageDialog(this, mensagem, titulo, JOptionPane.WARNING_MESSAGE);
    }

    /** Exceções do domínio (CPF inválido etc.) e de validação viram mensagem na tela. */
    private void mostrarErro(Exception ex) {
        String titulo = ex instanceof UniversidadeException || ex instanceof IllegalArgumentException
                ? "Dados inválidos" : "Erro";
        JOptionPane.showMessageDialog(this, ex.getMessage(), titulo, JOptionPane.ERROR_MESSAGE);
    }
}
