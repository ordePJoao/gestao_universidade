package br.edu.universidade.view;

import br.edu.universidade.model.Curso;
import br.edu.universidade.model.Disciplina;
import br.edu.universidade.model.Universidade;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

/**
 * Tela de Curso: cadastrar, listar, editar e excluir cursos e
 * adicionar/remover disciplinas de um curso (tasks #16 e #20, P8).
 * Os dados vêm da instância única de {@link Universidade} recebida da TelaPrincipal.
 */
public class CursoPanel extends JPanel implements Atualizavel {

    private final Universidade universidade;

    /** Cursos na mesma ordem das linhas da tabela (linha i -> cursosNaTabela.get(i)). */
    private final List<Curso> cursosNaTabela = new ArrayList<>();

    // Formulário
    private final JTextField txtCodigo = new JTextField(10);
    private final JTextField txtNome = new JTextField(25);
    private final JSpinner spnCargaHoraria = new JSpinner(new SpinnerNumberModel(3000, 1, 10000, 60));

    // Botões do cadastro
    private final JButton btnSalvar = Estilo.botaoPrimario("Salvar");
    private final JButton btnLimpar = Estilo.botaoSecundario("Limpar");
    private final JButton btnExcluir = Estilo.botaoPerigo("Excluir");

    // Tabela de cursos
    private final DefaultTableModel modeloCursos =
            Estilo.modeloTabela("Código", "Nome", "Carga horária (h)", "Disciplinas");
    private final JTable tblCursos = Estilo.tabela(modeloCursos);

    // Disciplinas do curso selecionado
    private final JComboBox<Disciplina> cbDisciplina = new JComboBox<>();
    private final DefaultListModel<Disciplina> modeloDisciplinasCurso = new DefaultListModel<>();
    private final JList<Disciplina> lstDisciplinasCurso = new JList<>(modeloDisciplinasCurso);
    private final JButton btnAdicionarDisciplina = Estilo.botaoPrimario("Adicionar disciplina");
    private final JButton btnRemoverDisciplina = Estilo.botaoPerigo("Remover disciplina");

    public CursoPanel(Universidade universidade) {
        this.universidade = universidade;

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
        Componentes.textoDoCombo(cbDisciplina, Disciplina::toString);
        lstDisciplinasCurso.setFont(Estilo.FONTE_PADRAO);

        JPanel painelDisciplinas = Estilo.formulario("Disciplinas do curso selecionado");
        painelDisciplinas.setLayout(new BorderLayout(5, 5));
        painelDisciplinas.add(cbDisciplina, BorderLayout.NORTH);
        painelDisciplinas.add(new JScrollPane(lstDisciplinasCurso), BorderLayout.CENTER);
        painelDisciplinas.add(Estilo.barraBotoes(btnAdicionarDisciplina, btnRemoverDisciplina),
                BorderLayout.SOUTH);
        painelDisciplinas.setPreferredSize(new Dimension(400, 0));

        // ----- Centro: tabela + disciplinas -----
        JScrollPane scrollTabela = new JScrollPane(tblCursos);
        scrollTabela.setBorder(BorderFactory.createTitledBorder("Cursos cadastrados"));

        JPanel centro = new JPanel(new BorderLayout(10, 10));
        centro.setBackground(Estilo.COR_FUNDO);
        centro.add(topo, BorderLayout.NORTH);
        centro.add(scrollTabela, BorderLayout.CENTER);
        centro.add(painelDisciplinas, BorderLayout.EAST);
        tela.add(centro, BorderLayout.CENTER);

        configurarAcoes();
        limparFormulario();
        atualizar();
    }

    /** Recarrega a tabela e o combo de disciplinas a partir da Universidade. */
    @Override
    public void atualizar() {
        Curso selecionado = cursoSelecionado();

        modeloCursos.setRowCount(0);
        cursosNaTabela.clear();
        for (Curso curso : universidade.getCursos()) {
            cursosNaTabela.add(curso);
            modeloCursos.addRow(new Object[]{
                    curso.getCodigo(), curso.getNome(), curso.getCargaHoraria(),
                    curso.getDisciplinas().size()});
        }
        Componentes.recarregarCombo(cbDisciplina, universidade.getDisciplinas());

        // mantém a mesma linha selecionada depois de recarregar
        int linha = cursosNaTabela.indexOf(selecionado);
        if (linha >= 0) {
            tblCursos.setRowSelectionInterval(linha, linha);
        }
        atualizarDisciplinasDoCurso();
    }

    private void configurarAcoes() {
        btnSalvar.addActionListener(e -> salvar());
        btnLimpar.addActionListener(e -> limparFormulario());
        btnExcluir.addActionListener(e -> excluir());
        btnAdicionarDisciplina.addActionListener(e -> adicionarDisciplina());
        btnRemoverDisciplina.addActionListener(e -> removerDisciplina());

        // Selecionar um curso -> preenche o formulário e mostra as disciplinas dele
        tblCursos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                preencherFormulario(cursoSelecionado());
                atualizarDisciplinasDoCurso();
            }
        });
    }

    // ---------- Ações do curso ----------

    private void salvar() {
        String codigo = txtCodigo.getText().trim();
        String nome = txtNome.getText().trim();
        int cargaHoraria = (Integer) spnCargaHoraria.getValue();

        if (codigo.isEmpty() || nome.isEmpty()) {
            Componentes.aviso(this, "Preencha código e nome.", "Campos obrigatórios");
            return;
        }

        Curso editando = cursoSelecionado();
        try {
            if (editando != null) { // edição: o código (identificador) não muda
                editando.setNome(nome);
                editando.setCargaHoraria(cargaHoraria);
            } else {
                if (buscarPorCodigo(codigo) != null) {
                    Componentes.aviso(this, "Já existe um curso com o código " + codigo + ".",
                            "Código duplicado");
                    return;
                }
                universidade.adicionarCurso(new Curso(codigo, nome, cargaHoraria));
            }
        } catch (Exception ex) {
            Componentes.mostrarErro(this, ex);
            return;
        }
        limparFormulario();
        atualizar();
    }

    private void excluir() {
        Curso curso = cursoSelecionado();
        if (curso == null) {
            Componentes.aviso(this, "Selecione um curso na tabela.", "Excluir curso");
            return;
        }
        if (!Componentes.confirmar(this, "Excluir o curso " + curso + "?\n"
                + "As disciplinas continuam cadastradas, só deixam de fazer parte do curso.",
                "Confirmar exclusão")) {
            return;
        }
        try {
            universidade.removerCurso(curso);
        } catch (Exception ex) {
            Componentes.mostrarErro(this, ex);
            return;
        }
        limparFormulario();
        atualizar();
    }

    // ---------- Disciplinas do curso ----------

    private void adicionarDisciplina() {
        Curso curso = cursoSelecionado();
        Disciplina disciplina = (Disciplina) cbDisciplina.getSelectedItem();
        if (curso == null) {
            Componentes.aviso(this, "Selecione um curso na tabela.", "Adicionar disciplina");
            return;
        }
        if (disciplina == null) {
            Componentes.aviso(this, "Cadastre uma disciplina na aba Disciplinas primeiro.",
                    "Nenhuma disciplina");
            return;
        }
        try {
            if (!curso.adicionarDisciplina(disciplina)) {
                Componentes.aviso(this, disciplina + " já faz parte do curso " + curso.getCodigo() + ".",
                        "Disciplina repetida");
                return;
            }
        } catch (Exception ex) {
            Componentes.mostrarErro(this, ex);
            return;
        }
        atualizar();
    }

    private void removerDisciplina() {
        Curso curso = cursoSelecionado();
        Disciplina disciplina = lstDisciplinasCurso.getSelectedValue();
        if (curso == null || disciplina == null) {
            Componentes.aviso(this, "Selecione um curso e uma disciplina da lista.", "Remover disciplina");
            return;
        }
        curso.removerDisciplina(disciplina);
        atualizar();
    }

    /** Mostra na lista da direita as disciplinas do curso selecionado. */
    private void atualizarDisciplinasDoCurso() {
        modeloDisciplinasCurso.clear();
        Curso curso = cursoSelecionado();
        if (curso != null) {
            for (Disciplina d : curso.getDisciplinas()) {
                modeloDisciplinasCurso.addElement(d);
            }
        }
    }

    // ---------- Auxiliares ----------

    private Curso cursoSelecionado() {
        int linha = tblCursos.getSelectedRow();
        return linha >= 0 && linha < cursosNaTabela.size()
                ? cursosNaTabela.get(tblCursos.convertRowIndexToModel(linha)) : null;
    }

    private Curso buscarPorCodigo(String codigo) {
        for (Curso c : universidade.getCursos()) {
            if (c.getCodigo().equalsIgnoreCase(codigo)) {
                return c;
            }
        }
        return null;
    }

    private void preencherFormulario(Curso curso) {
        if (curso == null) {
            return;
        }
        txtCodigo.setText(curso.getCodigo());
        txtCodigo.setEditable(false);
        txtNome.setText(curso.getNome());
        spnCargaHoraria.setValue(curso.getCargaHoraria());
    }

    private void limparFormulario() {
        tblCursos.clearSelection();
        txtCodigo.setText("");
        txtCodigo.setEditable(true);
        txtNome.setText("");
        spnCargaHoraria.setValue(3000);
        modeloDisciplinasCurso.clear();
    }
}
