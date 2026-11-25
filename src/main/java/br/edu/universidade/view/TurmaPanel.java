package br.edu.universidade.view;

import br.edu.universidade.model.Disciplina;
import br.edu.universidade.model.Professor;
import br.edu.universidade.model.Turma;
import br.edu.universidade.model.Universidade;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
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
 * Tela de Turma: abrir turma de uma disciplina com semestre, professor e vagas,
 * editar e excluir (tasks #16 e #20, P8).
 * Os dados vêm da instância única de {@link Universidade} recebida da TelaPrincipal.
 */
public class TurmaPanel extends JPanel implements Atualizavel {

    private static final String[] SEMESTRES = {"2026/2", "2027/1", "2027/2"};

    private final Universidade universidade;

    /** Turmas na mesma ordem das linhas da tabela. */
    private final List<Turma> turmasNaTabela = new ArrayList<>();

    // Formulário
    private final JTextField txtCodigo = new JTextField(10);
    private final JComboBox<Disciplina> cbDisciplina = new JComboBox<>();
    private final JComboBox<String> cbSemestre = new JComboBox<>(SEMESTRES);
    private final JComboBox<Professor> cbProfessor = new JComboBox<>();
    private final JSpinner spnVagas = new JSpinner(new SpinnerNumberModel(40, 1, 200, 1));

    // Botões
    private final JButton btnSalvar = Estilo.botaoPrimario("Abrir turma");
    private final JButton btnLimpar = Estilo.botaoSecundario("Limpar");
    private final JButton btnExcluir = Estilo.botaoPerigo("Excluir");

    // Tabela
    private final DefaultTableModel modeloTurmas = Estilo.modeloTabela(
            "Código", "Disciplina", "Semestre", "Professor", "Vagas", "Ocupadas", "Disponíveis");
    private final JTable tblTurmas = Estilo.tabela(modeloTurmas);

    public TurmaPanel(Universidade universidade) {
        this.universidade = universidade;

        setLayout(new BorderLayout());
        JPanel tela = Estilo.painelTela();
        add(tela, BorderLayout.CENTER);

        tela.add(Estilo.titulo("Turmas"), BorderLayout.NORTH);

        Componentes.textoDoCombo(cbDisciplina, Disciplina::toString);
        Componentes.textoDoCombo(cbProfessor, TurmaPanel::textoProfessor);
        cbSemestre.setEditable(true); // permite digitar outro semestre

        JPanel formulario = Estilo.formulario("Abrir turma");
        Estilo.adicionarCampo(formulario, 0, "Código", txtCodigo);
        Estilo.adicionarCampo(formulario, 1, "Disciplina", cbDisciplina);
        Estilo.adicionarCampo(formulario, 2, "Semestre", cbSemestre);
        Estilo.adicionarCampo(formulario, 3, "Professor", cbProfessor);
        Estilo.adicionarCampo(formulario, 4, "Vagas", spnVagas);

        JPanel topo = new JPanel(new BorderLayout());
        topo.setBackground(Estilo.COR_FUNDO);
        topo.add(formulario, BorderLayout.CENTER);
        topo.add(Estilo.barraBotoes(btnSalvar, btnLimpar, btnExcluir), BorderLayout.SOUTH);

        JScrollPane scrollTabela = new JScrollPane(tblTurmas);
        scrollTabela.setBorder(BorderFactory.createTitledBorder("Turmas abertas"));

        JPanel centro = new JPanel(new BorderLayout(10, 10));
        centro.setBackground(Estilo.COR_FUNDO);
        centro.add(topo, BorderLayout.NORTH);
        centro.add(scrollTabela, BorderLayout.CENTER);
        tela.add(centro, BorderLayout.CENTER);

        configurarAcoes();
        limparFormulario();
        atualizar();
    }

    /** Recarrega a tabela e os combos (disciplinas e professores) a partir da Universidade. */
    @Override
    public void atualizar() {
        Turma selecionada = turmaSelecionada();

        modeloTurmas.setRowCount(0);
        turmasNaTabela.clear();
        for (Turma t : universidade.getTurmas()) {
            turmasNaTabela.add(t);
            modeloTurmas.addRow(new Object[]{
                    t.getCodigo(),
                    t.getDisciplina().toString(),
                    t.getSemestre(),
                    textoProfessor(t.getProfessor()),
                    t.getVagas(),
                    t.getVagasOcupadas(),
                    t.getVagasDisponiveis()});
        }
        Componentes.recarregarCombo(cbDisciplina, universidade.getDisciplinas());
        Componentes.recarregarCombo(cbProfessor, universidade.getProfessores());

        // mantém a mesma linha selecionada depois de recarregar
        int linha = turmasNaTabela.indexOf(selecionada);
        if (linha >= 0) {
            tblTurmas.setRowSelectionInterval(linha, linha);
        }
    }

    private void configurarAcoes() {
        btnSalvar.addActionListener(e -> salvar());
        btnLimpar.addActionListener(e -> limparFormulario());
        btnExcluir.addActionListener(e -> excluir());

        tblTurmas.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                preencherFormulario(turmaSelecionada());
            }
        });
    }

    // ---------- Ações ----------

    private void salvar() {
        String codigo = txtCodigo.getText().trim();
        Disciplina disciplina = (Disciplina) cbDisciplina.getSelectedItem();
        Professor professor = (Professor) cbProfessor.getSelectedItem();
        Object semestreEscolhido = cbSemestre.getSelectedItem();
        String semestre = semestreEscolhido == null ? "" : semestreEscolhido.toString().trim();
        int vagas = (Integer) spnVagas.getValue();

        if (codigo.isEmpty() || semestre.isEmpty()) {
            Componentes.aviso(this, "Preencha código e semestre.", "Campos obrigatórios");
            return;
        }
        if (disciplina == null) {
            Componentes.aviso(this, "Cadastre uma disciplina na aba Disciplinas primeiro.", "Sem disciplina");
            return;
        }
        if (professor == null) {
            Componentes.aviso(this, "Cadastre um professor na aba Professores primeiro.", "Sem professor");
            return;
        }

        Turma editando = turmaSelecionada();
        try {
            if (editando != null) { // edição: o código (identificador) não muda
                Professor professorAntigo = editando.getProfessor();
                editando.setVagas(vagas); // não deixa ficar menor que as vagas já ocupadas
                editando.setDisciplina(disciplina);
                editando.setSemestre(semestre);
                editando.setProfessor(professor);
                trocarProfessor(editando, professorAntigo, professor);
            } else {
                if (buscarPorCodigo(codigo) != null) {
                    Componentes.aviso(this, "Já existe uma turma com o código " + codigo + ".",
                            "Código duplicado");
                    return;
                }
                Turma turma = new Turma(codigo, disciplina, semestre, professor, vagas);
                universidade.adicionarTurma(turma);
                trocarProfessor(turma, null, professor);
            }
        } catch (Exception ex) {
            Componentes.mostrarErro(this, ex);
            return;
        }
        atualizar();
        limparFormulario();
    }

    private void excluir() {
        Turma turma = turmaSelecionada();
        if (turma == null) {
            Componentes.aviso(this, "Selecione uma turma na tabela.", "Excluir turma");
            return;
        }
        if (turma.getVagasOcupadas() > 0) {
            Componentes.aviso(this, "A turma " + turma.getCodigo() + " tem " + turma.getVagasOcupadas()
                    + " aluno(s) matriculado(s).\nCancele as matrículas antes de excluir.",
                    "Não é possível excluir");
            return;
        }
        if (!Componentes.confirmar(this, "Excluir a turma " + turma.getCodigo() + "?", "Confirmar exclusão")) {
            return;
        }
        try {
            universidade.removerTurma(turma);
            trocarProfessor(turma, turma.getProfessor(), null);
        } catch (Exception ex) {
            Componentes.mostrarErro(this, ex);
            return;
        }
        atualizar();
        limparFormulario();
    }

    // ---------- Auxiliares ----------

    /**
     * Mantém a lista de turmas do professor (Professor.getTurmas()) em dia,
     * pois a aba Professores usa essa lista (ex.: não excluir professor com turmas).
     */
    private static void trocarProfessor(Turma turma, Professor antigo, Professor novo) {
        if (antigo != null && antigo != novo) {
            antigo.getTurmas().remove(turma);
        }
        if (novo != null && !novo.getTurmas().contains(turma)) {
            novo.getTurmas().add(turma);
        }
    }

    private static String textoProfessor(Professor p) {
        return p == null ? "" : p.getNome() + " (" + p.getSiape() + ")";
    }

    private Turma turmaSelecionada() {
        int linha = tblTurmas.getSelectedRow();
        return linha >= 0 && linha < turmasNaTabela.size()
                ? turmasNaTabela.get(tblTurmas.convertRowIndexToModel(linha)) : null;
    }

    private Turma buscarPorCodigo(String codigo) {
        for (Turma t : universidade.getTurmas()) {
            if (t.getCodigo().equalsIgnoreCase(codigo)) {
                return t;
            }
        }
        return null;
    }

    private void preencherFormulario(Turma turma) {
        if (turma == null) {
            return;
        }
        txtCodigo.setText(turma.getCodigo());
        txtCodigo.setEditable(false);
        cbDisciplina.setSelectedItem(turma.getDisciplina());
        cbSemestre.setSelectedItem(turma.getSemestre());
        cbProfessor.setSelectedItem(turma.getProfessor());
        spnVagas.setValue(turma.getVagas());
        btnSalvar.setText("Salvar alterações");
    }

    private void limparFormulario() {
        tblTurmas.clearSelection();
        txtCodigo.setText("");
        txtCodigo.setEditable(true);
        cbSemestre.setSelectedIndex(0);
        spnVagas.setValue(40);
        btnSalvar.setText("Abrir turma");
    }
}
