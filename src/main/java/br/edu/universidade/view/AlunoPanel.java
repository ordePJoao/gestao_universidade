package br.edu.universidade.view;

import br.edu.universidade.exception.CpfInvalidoException;
import br.edu.universidade.exception.UniversidadeException;
import br.edu.universidade.model.Aluno;
import br.edu.universidade.model.Curso;
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
import java.awt.Color;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Tela de Aluno: cadastrar, listar, editar e excluir alunos (Issue #33 / #19.2).
 * Integra o painel reutilizável de busca, seletor de curso e matrícula gerada automaticamente.
 */
public class AlunoPanel extends JPanel implements Atualizavel {

    private final Universidade universidade;

    /** Alunos na mesma ordem das linhas da tabela (linha i -> alunosNaTabela.get(i)). */
    private final List<Aluno> alunosNaTabela = new ArrayList<>();

    // Painel reutilizável de busca e verificação
    private final BuscaAlunoPanel buscaPanel;

    // Formulário
    private final JTextField txtMatricula = new JTextField(12);
    private final JTextField txtNome = new JTextField(25);
    private final JTextField txtCpf = new JTextField(14);
    private final JTextField txtEmail = new JTextField(25);
    private final JComboBox<Curso> cbCurso = new JComboBox<>();
    private final JTextField txtDataIngresso = new JTextField(10);

    // Botões
    private final JButton btnSalvar = Estilo.botaoPrimario("Salvar");
    private final JButton btnLimpar = Estilo.botaoSecundario("Limpar");
    private final JButton btnExcluir = Estilo.botaoPerigo("Excluir");

    // Tabela
    private final DefaultTableModel modeloAlunos =
            Estilo.modeloTabela("Matrícula", "Nome", "CPF", "E-mail", "Curso", "Ingresso", "Matrículas");
    private final JTable tblAlunos = Estilo.tabela(modeloAlunos);

    private Aluno alunoEmEdicao;

    public AlunoPanel(Universidade universidade) {
        this.universidade = universidade;

        setLayout(new BorderLayout());
        JPanel tela = Estilo.painelTela();
        add(tela, BorderLayout.CENTER);

        tela.add(Estilo.titulo("Alunos"), BorderLayout.NORTH);

        // Campo Matrícula sempre somente leitura (gerado ao salvar)
        txtMatricula.setEditable(false);
        txtMatricula.setBackground(new Color(0xF8F9FA));

        // Formatação do combo de cursos
        Componentes.textoDoCombo(cbCurso, Curso::getNome);

        // Painel de busca (task #32 / #35)
        buscaPanel = new BuscaAlunoPanel(universidade, this::selecionarNaTabela);

        // Formulário
        JPanel formulario = Estilo.formulario("Cadastro de aluno");
        Estilo.adicionarCampo(formulario, 0, "Matrícula (gerada)", txtMatricula);
        Estilo.adicionarCampo(formulario, 1, "Nome", txtNome);
        Estilo.adicionarCampo(formulario, 2, "CPF", txtCpf);
        Estilo.adicionarCampo(formulario, 3, "E-mail", txtEmail);
        Estilo.adicionarCampo(formulario, 4, "Curso", cbCurso);
        Estilo.adicionarCampo(formulario, 5, "Ingresso (dd/mm/aaaa)", txtDataIngresso);

        JPanel blocoCadastro = new JPanel(new BorderLayout(0, 6));
        blocoCadastro.setBackground(Estilo.COR_FUNDO);
        blocoCadastro.add(formulario, BorderLayout.CENTER);
        blocoCadastro.add(Estilo.barraBotoes(btnSalvar, btnLimpar, btnExcluir), BorderLayout.SOUTH);

        JPanel topo = new JPanel(new BorderLayout(0, 10));
        topo.setBackground(Estilo.COR_FUNDO);
        topo.add(buscaPanel, BorderLayout.NORTH);
        topo.add(blocoCadastro, BorderLayout.CENTER);

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

    /** Recarrega cursos, tabela e painel de busca a partir da Universidade. */
    @Override
    public void atualizar() {
        Curso cursoSelecionado = (Curso) cbCurso.getSelectedItem();
        Componentes.recarregarCombo(cbCurso, universidade.getCursos());
        if (cursoSelecionado != null) {
            cbCurso.setSelectedItem(cursoSelecionado);
        }

        modeloAlunos.setRowCount(0);
        alunosNaTabela.clear();
        for (Aluno aluno : universidade.getAlunos()) {
            alunosNaTabela.add(aluno);
            String nomeCurso = aluno.getCurso() != null ? aluno.getCurso().getNome() : "(sem curso)";
            modeloAlunos.addRow(new Object[]{
                    aluno.getMatricula(),
                    aluno.getNome(),
                    aluno.getCpf(),
                    aluno.getEmail(),
                    nomeCurso,
                    formatarData(aluno.getDataIngresso()),
                    aluno.getMatriculas().size()});
        }
        buscaPanel.recarregar();
    }

    private void configurarAcoes() {
        btnSalvar.addActionListener(e -> salvar());
        btnLimpar.addActionListener(e -> limparFormulario());
        btnExcluir.addActionListener(e -> excluir());

        // Selecionar na tabela -> carrega no formulário para editar
        tblAlunos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                preencherFormulario(alunoSelecionadoNaTabela());
            }
        });
    }

    // ---------- Ações ----------

    private void salvar() {
        String nome = txtNome.getText().trim();
        String cpf = txtCpf.getText().trim();
        String email = txtEmail.getText().trim();
        Curso curso = (Curso) cbCurso.getSelectedItem();

        if (nome.isEmpty() || cpf.isEmpty() || email.isEmpty() || txtDataIngresso.getText().isBlank()) {
            aviso("Preencha todos os campos obrigatórios.", "Campos obrigatórios");
            return;
        }
        if (curso == null) {
            aviso("Selecione um curso para o aluno.", "Curso obrigatório");
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

        if (cpfEmUso(cpf, alunoEmEdicao)) {
            aviso("Já existe um aluno com o CPF " + cpf + ".", "CPF duplicado");
            return;
        }

        try {
            validarCpf(cpf);
            if (alunoEmEdicao != null) {
                alunoEmEdicao.setNome(nome);
                alunoEmEdicao.setCpf(cpf);
                alunoEmEdicao.setEmail(email);
                alunoEmEdicao.setCurso(curso);
                alunoEmEdicao.setDataIngresso(dataIngresso);
            } else {
                // Construtor sem matrícula: a Universidade gera automaticamente ao cadastrar
                Aluno novoAluno = new Aluno(nome, cpf, email, dataIngresso, curso);
                universidade.cadastrarAluno(novoAluno);
                txtMatricula.setText(novoAluno.getMatricula());
                JOptionPane.showMessageDialog(this,
                        "Aluno cadastrado com sucesso!\nMatrícula gerada: " + novoAluno.getMatricula(),
                        "Cadastro concluído", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (CpfInvalidoException ex) {
            mostrarErro(ex);
            return;
        } catch (Exception ex) {
            // Captura CpfInvalidoException e demais exceções de validação do domínio
            mostrarErro(ex);
            return;
        }

        atualizar();
        limparFormulario();
    }

    private void excluir() {
        Aluno aluno = alunoEmEdicao != null ? alunoEmEdicao : alunoSelecionadoNaTabela();
        if (aluno == null) {
            aviso("Selecione um aluno na tabela ou na busca.", "Excluir aluno");
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

    private void selecionarNaTabela(Aluno aluno) {
        if (aluno == null) {
            return;
        }
        int indice = alunosNaTabela.indexOf(aluno);
        if (indice >= 0) {
            int linhaView = tblAlunos.convertRowIndexToView(indice);
            tblAlunos.setRowSelectionInterval(linhaView, linhaView);
            tblAlunos.scrollRectToVisible(tblAlunos.getCellRect(linhaView, 0, true));
        }
        preencherFormulario(aluno);
    }

    private Aluno alunoSelecionadoNaTabela() {
        int linha = tblAlunos.getSelectedRow();
        return linha >= 0 ? alunosNaTabela.get(tblAlunos.convertRowIndexToModel(linha)) : null;
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

    private void validarCpf(String cpf) throws CpfInvalidoException {
        if (cpf == null || cpf.replaceAll("\\D", "").length() != 11) {
            throw new CpfInvalidoException(cpf);
        }
    }

    private void preencherFormulario(Aluno aluno) {
        if (aluno == null) {
            return;
        }
        this.alunoEmEdicao = aluno;
        txtMatricula.setText(aluno.getMatricula());
        txtNome.setText(aluno.getNome());
        txtCpf.setText(aluno.getCpf());
        txtEmail.setText(aluno.getEmail());
        cbCurso.setSelectedItem(aluno.getCurso());
        txtDataIngresso.setText(formatarData(aluno.getDataIngresso()));
    }

    private void limparFormulario() {
        this.alunoEmEdicao = null;
        tblAlunos.clearSelection();
        txtMatricula.setText("(gerada ao salvar)");
        txtNome.setText("");
        txtCpf.setText("");
        txtEmail.setText("");
        if (cbCurso.getItemCount() > 0) {
            cbCurso.setSelectedIndex(0);
        }
        txtDataIngresso.setText(formatarData(new Date()));
        txtNome.requestFocusInWindow();
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

    /** Exceções do domínio (CpfInvalidoException etc.) e de validação viram mensagem na tela. */
    private void mostrarErro(Exception ex) {
        String titulo = ex instanceof UniversidadeException || ex instanceof IllegalArgumentException
                ? "Dados inválidos" : "Erro";
        JOptionPane.showMessageDialog(this, ex.getMessage(), titulo, JOptionPane.ERROR_MESSAGE);
    }
}
