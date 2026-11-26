package br.edu.universidade.view;

import br.edu.universidade.exception.MatriculaDuplicadaException;
import br.edu.universidade.exception.TurmaCheiaException;
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
 * Tela de Matrícula: buscar aluno, conferir dados, escolher turma e matricular/cancelar
 * (tasks #16, #21 e #35 / #21.1).
 */
public class MatriculaPanel extends JPanel implements Atualizavel {

    private final Universidade universidade;

    /** Matrículas na mesma ordem das linhas da tabela. */
    private final List<Matricula> matriculasNaTabela = new ArrayList<>();

    // Busca de aluno + Turma
    private final BuscaAlunoPanel buscaAluno;
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

        Componentes.textoDoCombo(cbTurma, t -> t.getCodigo() + " - " + t.getDisciplina().getNome()
                + " (" + t.getSemestre() + ") - " + t.getVagasDisponiveis() + " vaga(s)");

        this.buscaAluno = new BuscaAlunoPanel(universidade, this::atualizarTabela);

        JPanel formulario = Estilo.formulario("Turma para matrícula");
        Estilo.adicionarCampo(formulario, 0, "Turma", cbTurma);

        JPanel selecao = new JPanel(new BorderLayout(8, 8));
        selecao.setBackground(Estilo.COR_FUNDO);
        selecao.add(buscaAluno, BorderLayout.NORTH);
        selecao.add(formulario, BorderLayout.CENTER);

        JPanel topo = new JPanel(new BorderLayout());
        topo.setBackground(Estilo.COR_FUNDO);
        topo.add(selecao, BorderLayout.CENTER);
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

    /** Recarrega busca de alunos, turmas e tabela de matrículas. */
    @Override
    public void atualizar() {
        buscaAluno.recarregar();
        Componentes.recarregarCombo(cbTurma, universidade.getTurmas());
        atualizarTabela();
        cbTurma.repaint(); // atualiza o texto "x vaga(s)" do combo
    }

    private void atualizarTabela() {
        atualizarTabela((buscaAluno != null) ? buscaAluno.getAlunoSelecionado() : null);
    }

    private void atualizarTabela(Aluno alunoSelecionado) {
        modeloMatriculas.setRowCount(0);
        matriculasNaTabela.clear();
        for (Turma turma : universidade.getTurmas()) {
            for (Matricula m : turma.getMatriculas()) {
                if (alunoSelecionado != null && !m.getAluno().equals(alunoSelecionado)) {
                    continue;
                }
                matriculasNaTabela.add(m);
                modeloMatriculas.addRow(new Object[]{
                        m.getAluno().getMatricula(),
                        m.getAluno().getNome(),
                        turma.getCodigo(),
                        turma.getDisciplina().getNome(),
                        String.valueOf(m.getData()),
                        String.valueOf(m.getStatus())
                });
            }
        }
    }

    // ---------- Ações ----------

    private void matricular() {
        Aluno aluno = buscaAluno.getAlunoSelecionado();
        Turma turma = (Turma) cbTurma.getSelectedItem();
        if (aluno == null) {
            Componentes.aviso(this, "Busque e selecione um aluno primeiro.", "Sem aluno");
            return;
        }
        if (turma == null) {
            Componentes.aviso(this, "Abra uma turma na aba Turmas primeiro.", "Sem turma");
            return;
        }

        try {
            turma.matricular(aluno);
            JOptionPane.showMessageDialog(this, "Aluno matriculado com sucesso na turma " + turma.getCodigo() + "!",
                    "Matrícula realizada", JOptionPane.INFORMATION_MESSAGE);
        } catch (MatriculaDuplicadaException | TurmaCheiaException ex) {
            Componentes.mostrarErro(this, ex);
            return;
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
            m.cancelar();
            m.getTurma().removerMatricula(m);
            m.getAluno().cancelarMatricula(m);
        } catch (Exception ex) {
            Componentes.mostrarErro(this, ex);
            return;
        }
        atualizar();
    }
}
