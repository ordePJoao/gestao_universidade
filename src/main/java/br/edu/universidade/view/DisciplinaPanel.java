package br.edu.universidade.view;

import br.edu.universidade.model.Curso;
import br.edu.universidade.model.Disciplina;
import br.edu.universidade.model.Turma;
import br.edu.universidade.model.Universidade;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;

/**
 * Tela de Disciplina: cadastrar, listar, editar e excluir disciplinas (tasks #16 e #20, P8).
 * Os dados vêm da instância única de {@link Universidade} recebida da TelaPrincipal.
 */
public class DisciplinaPanel extends JPanel implements Atualizavel {

    private final Universidade universidade;

    /** Disciplinas na mesma ordem das linhas da tabela. */
    private final List<Disciplina> disciplinasNaTabela = new ArrayList<>();

    // Formulário
    private final JTextField txtCodigo = new JTextField(10);
    private final JTextField txtNome = new JTextField(25);
    private final JSpinner spnCargaHoraria = new JSpinner(new SpinnerNumberModel(60, 1, 500, 15));

    // Botões
    private final JButton btnSalvar = Estilo.botaoPrimario("Salvar");
    private final JButton btnLimpar = Estilo.botaoSecundario("Limpar");
    private final JButton btnExcluir = Estilo.botaoPerigo("Excluir");

    // Tabela
    private final DefaultTableModel modeloDisciplinas =
            Estilo.modeloTabela("Código", "Nome", "Carga horária (h)", "Cursos", "Turmas");
    private final JTable tblDisciplinas = Estilo.tabela(modeloDisciplinas);

    public DisciplinaPanel(Universidade universidade) {
        this.universidade = universidade;

        setLayout(new BorderLayout());
        JPanel tela = Estilo.painelTela();
        add(tela, BorderLayout.CENTER);

        tela.add(Estilo.titulo("Disciplinas"), BorderLayout.NORTH);

        JPanel formulario = Estilo.formulario("Cadastro de disciplina");
        Estilo.adicionarCampo(formulario, 0, "Código", txtCodigo);
        Estilo.adicionarCampo(formulario, 1, "Nome", txtNome);
        Estilo.adicionarCampo(formulario, 2, "Carga horária (h)", spnCargaHoraria);

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

        configurarAcoes();
        limparFormulario();
        atualizar();
    }

    /** Recarrega a tabela a partir da Universidade. */
    @Override
    public void atualizar() {
        Disciplina selecionada = disciplinaSelecionada();

        modeloDisciplinas.setRowCount(0);
        disciplinasNaTabela.clear();
        for (Disciplina d : universidade.getDisciplinas()) {
            disciplinasNaTabela.add(d);
            modeloDisciplinas.addRow(new Object[]{
                    d.getCodigo(), d.getNome(), d.getCargaHoraria(),
                    cursosCom(d).size(), turmasDe(d).size()});
        }

        // mantém a mesma linha selecionada depois de recarregar
        int linha = disciplinasNaTabela.indexOf(selecionada);
        if (linha >= 0) {
            tblDisciplinas.setRowSelectionInterval(linha, linha);
        }
    }

    private void configurarAcoes() {
        btnSalvar.addActionListener(e -> salvar());
        btnLimpar.addActionListener(e -> limparFormulario());
        btnExcluir.addActionListener(e -> excluir());

        // Selecionar na tabela -> carrega no formulário para editar
        tblDisciplinas.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                preencherFormulario(disciplinaSelecionada());
            }
        });
    }

    // ---------- Ações ----------

    private void salvar() {
        String codigo = txtCodigo.getText().trim();
        String nome = txtNome.getText().trim();
        int cargaHoraria = (Integer) spnCargaHoraria.getValue();

        if (codigo.isEmpty() || nome.isEmpty()) {
            Componentes.aviso(this, "Preencha código e nome.", "Campos obrigatórios");
            return;
        }

        Disciplina editando = disciplinaSelecionada();
        try {
            if (editando != null) { // edição: o código (identificador) não muda
                editando.setNome(nome);
                editando.setCargaHoraria(cargaHoraria);
            } else {
                if (buscarPorCodigo(codigo) != null) {
                    Componentes.aviso(this, "Já existe uma disciplina com o código " + codigo + ".",
                            "Código duplicado");
                    return;
                }
                universidade.adicionarDisciplina(new Disciplina(codigo, nome, cargaHoraria));
            }
        } catch (Exception ex) {
            Componentes.mostrarErro(this, ex);
            return;
        }
        atualizar();
        limparFormulario();
    }

    private void excluir() {
        Disciplina disciplina = disciplinaSelecionada();
        if (disciplina == null) {
            Componentes.aviso(this, "Selecione uma disciplina na tabela.", "Excluir disciplina");
            return;
        }
        // Não deixa a disciplina "sumir" de dentro de um curso ou de uma turma
        int cursos = cursosCom(disciplina).size();
        int turmas = turmasDe(disciplina).size();
        if (cursos > 0 || turmas > 0) {
            Componentes.aviso(this, disciplina + " está em " + cursos + " curso(s) e "
                    + turmas + " turma(s).\nRemova dos cursos e exclua as turmas antes.",
                    "Não é possível excluir");
            return;
        }
        if (!Componentes.confirmar(this, "Excluir a disciplina " + disciplina + "?", "Confirmar exclusão")) {
            return;
        }
        try {
            universidade.removerDisciplina(disciplina);
        } catch (Exception ex) {
            Componentes.mostrarErro(this, ex);
            return;
        }
        atualizar();
        limparFormulario();
    }

    // ---------- Auxiliares ----------

    private Disciplina disciplinaSelecionada() {
        int linha = tblDisciplinas.getSelectedRow();
        return linha >= 0 && linha < disciplinasNaTabela.size()
                ? disciplinasNaTabela.get(tblDisciplinas.convertRowIndexToModel(linha)) : null;
    }

    private Disciplina buscarPorCodigo(String codigo) {
        for (Disciplina d : universidade.getDisciplinas()) {
            if (d.getCodigo().equalsIgnoreCase(codigo)) {
                return d;
            }
        }
        return null;
    }

    private List<Curso> cursosCom(Disciplina disciplina) {
        List<Curso> cursos = new ArrayList<>();
        for (Curso c : universidade.getCursos()) {
            if (c.getDisciplinas().contains(disciplina)) {
                cursos.add(c);
            }
        }
        return cursos;
    }

    private List<Turma> turmasDe(Disciplina disciplina) {
        List<Turma> turmas = new ArrayList<>();
        for (Turma t : universidade.getTurmas()) {
            if (disciplina.equals(t.getDisciplina())) {
                turmas.add(t);
            }
        }
        return turmas;
    }

    private void preencherFormulario(Disciplina disciplina) {
        if (disciplina == null) {
            return;
        }
        txtCodigo.setText(disciplina.getCodigo());
        txtCodigo.setEditable(false);
        txtNome.setText(disciplina.getNome());
        spnCargaHoraria.setValue(disciplina.getCargaHoraria());
    }

    private void limparFormulario() {
        tblDisciplinas.clearSelection();
        txtCodigo.setText("");
        txtCodigo.setEditable(true);
        txtNome.setText("");
        spnCargaHoraria.setValue(60);
    }
}
