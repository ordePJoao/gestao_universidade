package br.edu.universidade.view;

import br.edu.universidade.model.Avaliacao;
import br.edu.universidade.model.Matricula;
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
 * Tela de Lançamento de Notas: lançar avaliação (tipo e nota), alterar nota,
 * ver média e situação (tasks #16 e #21, P8).
 * Média e situação vêm da própria Matricula (calcularMedia() e getSituacao()).
 */
public class NotasPanel extends JPanel implements Atualizavel {

    private static final String[] TIPOS_AVALIACAO = {"Prova", "Trabalho", "Seminário", "Lista"};

    private final Universidade universidade;

    /** Avaliações da matrícula escolhida, na mesma ordem das linhas da tabela. */
    private final List<Avaliacao> avaliacoesNaTabela = new ArrayList<>();

    // Formulário
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

        Componentes.textoDoCombo(cbMatricula, m -> m.getAluno().getNome() + " ("
                + m.getAluno().getMatricula() + ") - " + m.getTurma().getCodigo() + " "
                + m.getTurma().getDisciplina().getNome());
        cbTipoAvaliacao.setEditable(true); // permite digitar outro tipo

        JPanel formulario = Estilo.formulario("Avaliação");
        Estilo.adicionarCampo(formulario, 0, "Matrícula", cbMatricula);
        Estilo.adicionarCampo(formulario, 1, "Tipo", cbTipoAvaliacao);
        Estilo.adicionarCampo(formulario, 2, "Nota (0 a 10)", spnNota);

        JPanel topo = new JPanel(new BorderLayout());
        topo.setBackground(Estilo.COR_FUNDO);
        topo.add(formulario, BorderLayout.CENTER);
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

    /** Recarrega a lista de matrículas (de todas as turmas) e as avaliações da escolhida. */
    @Override
    public void atualizar() {
        List<Matricula> todas = new ArrayList<>();
        for (Turma turma : universidade.getTurmas()) {
            todas.addAll(turma.getMatriculas());
        }
        Componentes.recarregarCombo(cbMatricula, todas);
        mostrarAvaliacoes();
    }

    private void configurarAcoes() {
        cbMatricula.addActionListener(e -> mostrarAvaliacoes());
        btnLancar.addActionListener(e -> lancar());
        btnAlterarNota.addActionListener(e -> alterarNota());

        // Selecionar avaliação -> carrega tipo e nota no formulário
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
        Matricula matricula = (Matricula) cbMatricula.getSelectedItem();
        if (matricula == null) {
            Componentes.aviso(this, "Matricule um aluno numa turma (aba Matrículas) primeiro.",
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
            // Avaliacao valida a nota (0 a 10) e lança NotaInvalidaException se estiver fora
            matricula.adicionarAvaliacao(new Avaliacao(tipo, nota()));
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
        } catch (Exception ex) {
            Componentes.mostrarErro(this, ex);
            return;
        }
        mostrarAvaliacoes();
    }

    // ---------- Auxiliares ----------

    /** Mostra as avaliações, a média e a situação da matrícula escolhida no combo. */
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

        String situacao = String.valueOf(matricula.getSituacao());
        lblMedia.setText(String.format("Média: %.2f", matricula.calcularMedia()));
        lblSituacao.setText("Situação: " + situacao);
        lblSituacao.setForeground(situacao.toUpperCase().startsWith("APROVADO")
                ? Estilo.COR_SUCESSO : Estilo.COR_PERIGO);
    }

    private Avaliacao avaliacaoSelecionada() {
        int linha = tblAvaliacoes.getSelectedRow();
        return linha >= 0 && linha < avaliacoesNaTabela.size()
                ? avaliacoesNaTabela.get(tblAvaliacoes.convertRowIndexToModel(linha)) : null;
    }

    private double nota() {
        return ((Number) spnNota.getValue()).doubleValue();
    }
}
