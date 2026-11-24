package br.edu.universidade.view;

import br.edu.universidade.model.Aluno;
import br.edu.universidade.model.Matricula;
import br.edu.universidade.model.Turma;
import br.edu.universidade.model.Universidade;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;

/**
 * Tela de Matrícula: matricular aluno em turma e cancelar matrícula (tasks #16 e #21, P8).
 * As regras (turma cheia, matrícula duplicada) ficam no núcleo: a tela só mostra a mensagem.
 */
public class MatriculaPanel extends JPanel implements Atualizavel {

    private final Universidade universidade;

    /** Matrículas na mesma ordem das linhas da tabela. */
    private final List<Matricula> matriculasNaTabela = new ArrayList<>();

    // Formulário
    private final JComboBox<Aluno> cbAluno = new JComboBox<>();
    private final JComboBox<Turma> cbTurma = new JComboBox<>();

    // Botões
    private final JButton btnMatricular = Estilo.botaoPrimario("Matricular");
    private final JButton btnCancelarMatricula = Estilo.botaoPerigo("Cancelar matrícula");

    // Tabela
    private final DefaultTableModel modeloMatriculas = Estilo.modeloTabela(
            "Matrícula do aluno", "Aluno", "Turma", "Disciplina", "Data", "Status");
    private final JTable tblMatriculas = Estilo.tabela(modeloMatriculas);

    public MatriculaPanel(Universidade universidade) {
        this.universidade = universidade;

        setLayout(new BorderLayout());
        JPanel tela = Estilo.painelTela();
        add(tela, BorderLayout.CENTER);

        tela.add(Estilo.titulo("Matrículas"), BorderLayout.NORTH);

        Componentes.textoDoCombo(cbAluno, a -> a.getMatricula() + " - " + a.getNome());
        Componentes.textoDoCombo(cbTurma, t -> t.getCodigo() + " - " + t.getDisciplina().getNome()
                + " (" + t.getSemestre() + ") - " + t.getVagasDisponiveis() + " vaga(s)");

        JPanel formulario = Estilo.formulario("Matricular aluno em turma");
        Estilo.adicionarCampo(formulario, 0, "Aluno", cbAluno);
        Estilo.adicionarCampo(formulario, 1, "Turma", cbTurma);

        JPanel topo = new JPanel(new BorderLayout());
        topo.setBackground(Estilo.COR_FUNDO);
        topo.add(formulario, BorderLayout.CENTER);
        topo.add(Estilo.barraBotoes(btnMatricular, btnCancelarMatricula), BorderLayout.SOUTH);

        JScrollPane scrollTabela = new JScrollPane(tblMatriculas);
        scrollTabela.setBorder(BorderFactory.createTitledBorder("Matrículas"));

        JPanel centro = new JPanel(new BorderLayout(10, 10));
        centro.setBackground(Estilo.COR_FUNDO);
        centro.add(topo, BorderLayout.NORTH);
        centro.add(scrollTabela, BorderLayout.CENTER);
        tela.add(centro, BorderLayout.CENTER);

        btnMatricular.addActionListener(e -> matricular());
        btnCancelarMatricula.addActionListener(e -> cancelar());

        atualizar();
    }

    /** Recarrega combos e tabela. As matrículas são lidas de cada turma da Universidade. */
    @Override
    public void atualizar() {
        Componentes.recarregarCombo(cbAluno, universidade.getAlunos());
        Componentes.recarregarCombo(cbTurma, universidade.getTurmas());

        modeloMatriculas.setRowCount(0);
        matriculasNaTabela.clear();
        for (Turma turma : universidade.getTurmas()) {
            for (Matricula m : turma.getMatriculas()) {
                matriculasNaTabela.add(m);
                modeloMatriculas.addRow(new Object[]{
                        m.getAluno().getMatricula(),
                        m.getAluno().getNome(),
                        turma.getCodigo(),
                        turma.getDisciplina().getNome(),
                        String.valueOf(m.getData()),
                        String.valueOf(m.getStatus())});
            }
        }
        cbTurma.repaint(); // atualiza o texto "x vaga(s)" do combo
    }

    // ---------- Ações ----------

    private void matricular() {
        Aluno aluno = (Aluno) cbAluno.getSelectedItem();
        Turma turma = (Turma) cbTurma.getSelectedItem();
        if (aluno == null) {
            Componentes.aviso(this, "Cadastre um aluno na aba Alunos primeiro.", "Sem aluno");
            return;
        }
        if (turma == null) {
            Componentes.aviso(this, "Abra uma turma na aba Turmas primeiro.", "Sem turma");
            return;
        }
        // Mesmo aluno duas vezes na mesma turma (MatriculaDuplicadaException no núcleo)
        for (Matricula existente : turma.getMatriculas()) {
            if (existente.getAluno() == aluno) {
                Componentes.aviso(this, aluno.getNome() + " já está matriculado(a) na turma "
                        + turma.getCodigo() + ".", "Matrícula duplicada");
                return;
            }
        }
        // Turma cheia (TurmaCheiaException no núcleo)
        if (!turma.temVaga()) {
            Componentes.aviso(this, "A turma " + turma.getCodigo() + " está cheia ("
                    + turma.getVagas() + " vagas).", "Turma cheia");
            return;
        }
        try {
            Matricula matricula = new Matricula(aluno, turma);
            // ocupa a vaga na turma (se o construtor da Matricula ainda não tiver feito isso)
            if (!turma.getMatriculas().contains(matricula) && !turma.adicionarMatricula(matricula)) {
                Componentes.aviso(this, "Não foi possível matricular na turma " + turma.getCodigo() + ".",
                        "Matrícula não realizada");
                return;
            }
            if (!aluno.getMatriculas().contains(matricula)) {
                aluno.getMatriculas().add(matricula);   // aparece na lista do aluno
            }
        } catch (Exception ex) {
            Componentes.mostrarErro(this, ex);
            return;
        }
        atualizar();
    }

    private void cancelar() {
        int linha = tblMatriculas.getSelectedRow();
        if (linha < 0) {
            Componentes.aviso(this, "Selecione uma matrícula na tabela.", "Cancelar matrícula");
            return;
        }
        Matricula m = matriculasNaTabela.get(tblMatriculas.convertRowIndexToModel(linha));
        if (!Componentes.confirmar(this, "Cancelar a matrícula de " + m.getAluno().getNome()
                + " na turma " + m.getTurma().getCodigo() + "?\nA vaga será liberada.",
                "Confirmar cancelamento")) {
            return;
        }
        try {
            m.cancelar();                           // status CANCELADA
            m.getTurma().removerMatricula(m);       // libera a vaga
            m.getAluno().cancelarMatricula(m);      // tira da lista do aluno
        } catch (Exception ex) {
            Componentes.mostrarErro(this, ex);
            return;
        }
        atualizar();
    }
}
