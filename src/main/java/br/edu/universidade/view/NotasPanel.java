package br.edu.universidade.view;

import br.edu.universidade.exception.NotaInvalidaException;
import br.edu.universidade.model.Aluno;
import br.edu.universidade.model.Avaliacao;
import br.edu.universidade.model.Matricula;
import br.edu.universidade.model.StatusMatricula;
import br.edu.universidade.model.Turma;
import br.edu.universidade.model.Universidade;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.List;

/**
 * Tela de Lançamento de Notas: buscar aluno, escolher a turma do aluno, lançar/alterar
 * avaliação (tipo e nota de 0 a 10), ver média simples e situação (aprovada >= 7)
 * (tasks #16, #21 e #35 / #21.1).
 */
public class NotasPanel extends JPanel implements Atualizavel {

    private static final String[] TIPOS_AVALIACAO = {"Prova", "Trabalho", "Seminário", "Lista"};

    private final Universidade universidade;

    /** Avaliações da matrícula escolhida, na mesma ordem das linhas da tabela. */
    private final List<Avaliacao> avaliacoesNaTabela = new ArrayList<>();

    // Busca de aluno + Formulário
    private final BuscaAlunoPanel buscaAluno;
    private final JComboBox<Matricula> cbMatricula = new JComboBox<>();
    private final JComboBox<String> cbTipoAvaliacao = new JComboBox<>(TIPOS_AVALIACAO);
    private final JSpinner spnNota = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 10.0, 0.5));

    // Botões
    private final JButton btnLancar = Estilo.botaoPrimario("Lançar avaliação");
    private final JButton btnAlterarNota = Estilo.botaoSecundario("Alterar nota");

    // Tabela
    private final DefaultTableModel modeloAvaliacoes = Estilo.modeloTabela("Tipo", "Nota");
    private final JTable tblAvaliacoes = Estilo.tabela(modeloAvaliacoes);

    // Resumo
    private final JLabel lblMedia = new JLabel();
    private final JLabel lblSituacao = new JLabel();

    public NotasPanel(Universidade universidade) {
        this.universidade = universidade;

        setLayout(new BorderLayout());
        JPanel tela = Estilo.painelTela();
        add(tela, BorderLayout.CENTER);

        tela.add(Estilo.titulo("Lançamento de Notas"), BorderLayout.NORTH);

        Componentes.textoDoCombo(cbMatricula, m -> m.getTurma().getCodigo() + " - "
                + m.getTurma().getDisciplina().getNome()
                + " (" + m.getTurma().getSemestre() + ")");
        cbTipoAvaliacao.setEditable(true);

        this.buscaAluno = new BuscaAlunoPanel(universidade, aluno -> carregarTurmasDoAluno(aluno));

        JPanel formulario = Estilo.formulario("Avaliação");
        Estilo.adicionarCampo(formulario, 0, "Turma do aluno", cbMatricula);
        Estilo.adicionarCampo(formulario, 1, "Tipo", cbTipoAvaliacao);
        Estilo.adicionarCampo(formulario, 2, "Nota (0 a 10)", spnNota);

        JPanel selecao = new JPanel(new BorderLayout(8, 8));
        selecao.setBackground(Estilo.COR_FUNDO);
        selecao.add(buscaAluno, BorderLayout.NORTH);
        selecao.add(formulario, BorderLayout.CENTER);

        JPanel topo = new JPanel(new BorderLayout());
        topo.setBackground(Estilo.COR_FUNDO);
        topo.add(selecao, BorderLayout.CENTER);
        topo.add(Estilo.barraBotoes(btnLancar, btnAlterarNota), BorderLayout.SOUTH);

        JScrollPane scrollTabela = new JScrollPane(tblAvaliacoes);
        scrollTabela.setBorder(BorderFactory.createTitledBorder("Avaliações da matrícula"));

        lblMedia.setFont(Estilo.FONTE_NEGRITO);
        lblSituacao.setFont(Estilo.FONTE_NEGRITO);
        JPanel resumo = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 5));
        resumo.setBackground(Estilo.COR_FUNDO);
        resumo.add(lblMedia);
        resumo.add(lblSituacao);

        JPanel centro = new JPanel(new BorderLayout(10, 10));
        centro.setBackground(Estilo.COR_FUNDO);
        centro.add(topo, BorderLayout.NORTH);
        centro.add(scrollTabela, BorderLayout.CENTER);
        centro.add(resumo, BorderLayout.SOUTH);
        tela.add(centro, BorderLayout.CENTER);

        configurarAcoes();
        atualizar();
    }

    /** Recarrega a busca de alunos e as turmas do aluno selecionado. */
    @Override
    public void atualizar() {
        buscaAluno.recarregar();
        carregarTurmasDoAluno(buscaAluno.getAlunoSelecionado());
    }

    private void carregarTurmasDoAluno(Aluno aluno) {
        List<Matricula> matriculasDoAluno = new ArrayList<>();
        if (aluno != null) {
            for (Turma turma : universidade.getTurmas()) {
                for (Matricula m : turma.getMatriculas()) {
                    if (m.getAluno().equals(aluno)) {
                        matriculasDoAluno.add(m);
                    }
                }
            }
        }
        Componentes.recarregarCombo(cbMatricula, matriculasDoAluno);
        mostrarAvaliacoes();
    }

    private void configurarAcoes() {
        cbMatricula.addActionListener(e -> mostrarAvaliacoes());
        btnLancar.addActionListener(e -> lancar());
        btnAlterarNota.addActionListener(e -> alterarNota());

        tblAvaliacoes.getSelectionModel().addListSelectionListener(e -> {
            Avaliacao a = avaliacaoSelecionada();
            if (!e.getValueIsAdjusting() && a != null) {
                cbTipoAvaliacao.setSelectedItem(a.getTipo());
                spnNota.setValue(a.getNota());
            }
        });
    }

    // ---------- Ações ----------

    private void lancar() {
        Aluno aluno = buscaAluno.getAlunoSelecionado();
        if (aluno == null) {
            Componentes.aviso(this, "Busque e selecione um aluno primeiro.", "Sem aluno");
            return;
        }
        Matricula matricula = (Matricula) cbMatricula.getSelectedItem();
        if (matricula == null) {
            Componentes.aviso(this, "O aluno selecionado não possui matrícula em nenhuma turma. Matricule-o na aba Matrículas primeiro.",
                    "Sem matrícula");
            return;
        }
        Object tipoEscolhido = cbTipoAvaliacao.getSelectedItem();
        String tipo = tipoEscolhido == null ? "" : tipoEscolhido.toString().trim();
        if (tipo.isEmpty()) {
            Componentes.aviso(this, "Informe o tipo da avaliação.", "Campo obrigatório");
            return;
        }
        try {
            matricula.adicionarAvaliacao(tipo, nota());
        } catch (NotaInvalidaException ex) {
            Componentes.mostrarErro(this, ex);
            return;
        } catch (Exception ex) {
            Componentes.mostrarErro(this, ex);
            return;
        }
        mostrarAvaliacoes();
    }

    private void alterarNota() {
        Avaliacao avaliacao = avaliacaoSelecionada();
        if (avaliacao == null) {
            Componentes.aviso(this, "Selecione uma avaliação na tabela.", "Alterar nota");
            return;
        }
        try {
            avaliacao.alterarNota(nota());
        } catch (NotaInvalidaException ex) {
            Componentes.mostrarErro(this, ex);
            return;
        } catch (Exception ex) {
            Componentes.mostrarErro(this, ex);
            return;
        }
        mostrarAvaliacoes();
    }

    // ---------- Auxiliares ----------

    /** Mostra as avaliações, a média simples e a situação (APROVADA >= 7.0) da matrícula escolhida. */
    private void mostrarAvaliacoes() {
        modeloAvaliacoes.setRowCount(0);
        avaliacoesNaTabela.clear();

        Matricula matricula = (Matricula) cbMatricula.getSelectedItem();
        if (matricula == null || matricula.getAvaliacoes().isEmpty()) {
            lblMedia.setText("Média: -");
            lblSituacao.setText("Situação: sem avaliações");
            lblSituacao.setForeground(Estilo.COR_SECUNDARIA);
            return;
        }
        for (Avaliacao a : matricula.getAvaliacoes()) {
            avaliacoesNaTabela.add(a);
            modeloAvaliacoes.addRow(new Object[]{a.getTipo(), String.format("%.1f", a.getNota())});
        }

        double media = matricula.calcularMedia();
        String situacao = matricula.getSituacao();

        lblMedia.setText(String.format("Média: %.2f", media));
        lblSituacao.setText("Situação: " + situacao);
        lblSituacao.setForeground(corDaSituacao(situacao));
    }

    /** APROVADA em verde, REPROVADA/CANCELADA em vermelho e demais em cinza. */
    private static java.awt.Color corDaSituacao(String situacao) {
        String s = situacao.toUpperCase();
        if (s.startsWith("APROVAD")) {
            return Estilo.COR_SUCESSO;
        }
        if (s.startsWith("REPROVAD") || s.startsWith("CANCELAD")) {
            return Estilo.COR_PERIGO;
        }
        return Estilo.COR_SECUNDARIA;
    }

    private Avaliacao avaliacaoSelecionada() {
        int linha = tblAvaliacoes.getSelectedRow();
        return linha >= 0 && linha < avaliacoesNaTabela.size()
                ? avaliacoesNaTabela.get(tblAvaliacoes.convertRowIndexToModel(linha)) : null;
    }

    private double nota() {
        try {
            spnNota.commitEdit();
        } catch (java.text.ParseException ignored) {
            // mantém valor atual ou lança se inválido no modelo
        }
        return ((Number) spnNota.getValue()).doubleValue();
    }
}
