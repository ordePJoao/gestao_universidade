package br.edu.universidade.view;

import br.edu.universidade.exception.UniversidadeException;
import br.edu.universidade.model.Aluno;
import br.edu.universidade.model.Universidade;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Tela de Aluno: cadastrar, listar, editar e excluir alunos (tasks #15 e #19, P7).
 * Os dados vêm da instância única de {@link Universidade} recebida da TelaPrincipal.
 */
public class AlunoPanel extends JPanel implements Atualizavel {

    private final Universidade universidade;

    /** Alunos na mesma ordem das linhas da tabela (linha i -> alunosNaTabela.get(i)). */
    private final List<Aluno> alunosNaTabela = new ArrayList<>();

    // Formulário
    private final JTextField txtMatricula = new JTextField(12);
    private final JTextField txtNome = new JTextField(25);
    private final JTextField txtCpf = new JTextField(14);
    private final JTextField txtEmail = new JTextField(25);
    private final JTextField txtDataIngresso = new JTextField(10);

    // Botões
    private final JButton btnSalvar = Estilo.botaoPrimario("Salvar");
    private final JButton btnLimpar = Estilo.botaoSecundario("Limpar");
    private final JButton btnExcluir = Estilo.botaoPerigo("Excluir");

    // Tabela
    private final DefaultTableModel modeloAlunos =
            Estilo.modeloTabela("Matrícula", "Nome", "CPF", "E-mail", "Ingresso", "Matrículas");
    private final JTable tblAlunos = Estilo.tabela(modeloAlunos);

    public AlunoPanel(Universidade universidade) {
        this.universidade = universidade;

        setLayout(new BorderLayout());
        JPanel tela = Estilo.painelTela();
        add(tela, BorderLayout.CENTER);

        tela.add(Estilo.titulo("Alunos"), BorderLayout.NORTH);

        JPanel formulario = Estilo.formulario("Cadastro de aluno");
        Estilo.adicionarCampo(formulario, 0, "Matrícula", txtMatricula);
        Estilo.adicionarCampo(formulario, 1, "Nome", txtNome);
        Estilo.adicionarCampo(formulario, 2, "CPF", txtCpf);
        Estilo.adicionarCampo(formulario, 3, "E-mail", txtEmail);
        Estilo.adicionarCampo(formulario, 4, "Ingresso (dd/mm/aaaa)", txtDataIngresso);

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

        configurarAcoes();
        limparFormulario();
        atualizar();
    }

    /** Recarrega a tabela a partir da Universidade. */
    @Override
    public void atualizar() {
        modeloAlunos.setRowCount(0);
        alunosNaTabela.clear();
        for (Aluno aluno : universidade.getAlunos()) {
            alunosNaTabela.add(aluno);
            modeloAlunos.addRow(new Object[]{
                    aluno.getMatricula(),
                    aluno.getNome(),
                    aluno.getCpf(),
                    aluno.getEmail(),
                    formatarData(aluno.getDataIngresso()),
                    aluno.getMatriculas().size()});
        }
    }

    private void configurarAcoes() {
        btnSalvar.addActionListener(e -> salvar());
        btnLimpar.addActionListener(e -> limparFormulario());
        btnExcluir.addActionListener(e -> excluir());

        // Selecionar na tabela -> carrega no formulário para editar
        tblAlunos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                preencherFormulario(alunoSelecionado());
            }
        });
    }

    // ---------- Ações ----------

    private void salvar() {
        String matricula = txtMatricula.getText().trim();
        String nome = txtNome.getText().trim();
        String cpf = txtCpf.getText().trim();
        String email = txtEmail.getText().trim();

        if (matricula.isEmpty() || nome.isEmpty() || cpf.isEmpty() || email.isEmpty()
                || txtDataIngresso.getText().isBlank()) {
            aviso("Preencha todos os campos.", "Campos obrigatórios");
            return;
        }
        if (!email.contains("@")) {
            aviso("Informe um e-mail válido.", "E-mail inválido");
            return;
        }
        Date dataIngresso = lerData(txtDataIngresso.getText().trim());
        if (dataIngresso == null) {
            aviso("Data de ingresso inválida. Use o formato dd/mm/aaaa.", "Data inválida");
            return;
        }

        Aluno editando = alunoSelecionado();
        if (cpfEmUso(cpf, editando)) {
            aviso("Já existe um aluno com o CPF " + cpf + ".", "CPF duplicado");
            return;
        }

        try {
            if (editando != null) { // edição: a matrícula (identificador) não muda
                editando.setNome(nome);
                editando.setCpf(cpf);
                editando.setEmail(email);
                editando.setDataIngresso(dataIngresso);
            } else {
                if (buscarPorMatricula(matricula) != null) {
                    aviso("Já existe um aluno com a matrícula " + matricula + ".", "Matrícula duplicada");
                    return;
                }
                universidade.cadastrarAluno(new Aluno(nome, cpf, email, matricula, dataIngresso));
            }
        } catch (Exception ex) {
            mostrarErro(ex);
            return;
        }
        atualizar();
        limparFormulario();
    }

    private void excluir() {
        Aluno aluno = alunoSelecionado();
        if (aluno == null) {
            aviso("Selecione um aluno na tabela.", "Excluir aluno");
            return;
        }
        if (!aluno.getMatriculas().isEmpty()) {
            aviso("O aluno " + aluno.getNome() + " possui " + aluno.getMatriculas().size()
                    + " matrícula(s) em turmas.\nCancele as matrículas antes de excluir.",
                    "Não é possível excluir");
            return;
        }
        int resposta = JOptionPane.showConfirmDialog(this,
                "Excluir o aluno " + aluno.getNome() + " (" + aluno.getMatricula() + ")?",
                "Confirmar exclusão", JOptionPane.YES_NO_OPTION);
        if (resposta != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            universidade.removerAluno(aluno);
        } catch (Exception ex) {
            mostrarErro(ex);
            return;
        }
        atualizar();
        limparFormulario();
    }

    // ---------- Auxiliares ----------

    private Aluno alunoSelecionado() {
        int linha = tblAlunos.getSelectedRow();
        return linha >= 0 ? alunosNaTabela.get(tblAlunos.convertRowIndexToModel(linha)) : null;
    }

    private Aluno buscarPorMatricula(String matricula) {
        for (Aluno a : universidade.getAlunos()) {
            if (a.getMatricula().equalsIgnoreCase(matricula)) {
                return a;
            }
        }
        return null;
    }

    /** CPF já usado por outro aluno (ignora o próprio aluno em edição e a pontuação). */
    private boolean cpfEmUso(String cpf, Aluno ignorar) {
        String digitos = cpf.replaceAll("\\D", "");
        for (Aluno a : universidade.getAlunos()) {
            if (a != ignorar && a.getCpf() != null && a.getCpf().replaceAll("\\D", "").equals(digitos)) {
                return true;
            }
        }
        return false;
    }

    private void preencherFormulario(Aluno aluno) {
        if (aluno == null) {
            return;
        }
        txtMatricula.setText(aluno.getMatricula());
        txtMatricula.setEditable(false);
        txtNome.setText(aluno.getNome());
        txtCpf.setText(aluno.getCpf());
        txtEmail.setText(aluno.getEmail());
        txtDataIngresso.setText(formatarData(aluno.getDataIngresso()));
    }

    private void limparFormulario() {
        tblAlunos.clearSelection();
        txtMatricula.setText("");
        txtMatricula.setEditable(true);
        txtNome.setText("");
        txtCpf.setText("");
        txtEmail.setText("");
        txtDataIngresso.setText(formatarData(new Date()));
        txtMatricula.requestFocusInWindow();
    }

    private static String formatarData(Date data) {
        return data == null ? "" : novoFormatoData().format(data);
    }

    /** Converte "dd/mm/aaaa" em Date; devolve null se a data não existir (ex.: 31/02). */
    private static Date lerData(String texto) {
        try {
            return novoFormatoData().parse(texto);
        } catch (ParseException ex) {
            return null;
        }
    }

    private static SimpleDateFormat novoFormatoData() {
        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
        formato.setLenient(false);
        return formato;
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
