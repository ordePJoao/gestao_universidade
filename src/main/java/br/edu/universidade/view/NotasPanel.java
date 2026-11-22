package br.edu.universidade.view;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;

/**
 * Tela de Lançamento de Notas: lançar avaliação, alterar nota,
 * ver média e situação (aprovado/reprovado).
 * Sprint 1: usa dados falsos. Na Sprint 2 a média vem de Matricula
 * e a validação de NotaInvalidaException vem do núcleo.
 */
public class NotasPanel extends JPanel {

    // Corte de aprovação (confirmar o valor definido na reunião)
    private static final double MEDIA_APROVACAO = 7.0;

    // Formulário
    private final JComboBox<String> cbMatricula = new JComboBox<>(DadosFalsos.descricoesMatriculas());
    private final JComboBox<String> cbTipoAvaliacao = new JComboBox<>(DadosFalsos.TIPOS_AVALIACAO);
    // O spinner já limita a nota entre 0 e 10
    private final JSpinner spnNota = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 10.0, 0.5));

    // Botões
    private final JButton btnLancar = Estilo.botaoPrimario("Lançar avaliação");
    private final JButton btnAlterarNota = Estilo.botaoSecundario("Alterar nota");
    private final JButton btnExcluir = Estilo.botaoPerigo("Excluir avaliação");

    // Tabela de avaliações da matrícula selecionada
    private final DefaultTableModel modeloAvaliacoes = Estilo.modeloTabela("Tipo", "Nota");
    private final JTable tblAvaliacoes = Estilo.tabela(modeloAvaliacoes);

    // Resumo
    private final JLabel lblMedia = new JLabel();
    private final JLabel lblSituacao = new JLabel();

    public NotasPanel() {
        setLayout(new BorderLayout());
        JPanel tela = Estilo.painelTela();
        add(tela, BorderLayout.CENTER);

        tela.add(Estilo.titulo("Lançamento de Notas"), BorderLayout.NORTH);

        JPanel formulario = Estilo.formulario("Avaliação");
        Estilo.adicionarCampo(formulario, 0, "Matrícula", cbMatricula);
        Estilo.adicionarCampo(formulario, 1, "Tipo", cbTipoAvaliacao);
        Estilo.adicionarCampo(formulario, 2, "Nota (0 a 10)", spnNota);

        JPanel topo = new JPanel(new BorderLayout());
        topo.setBackground(Estilo.COR_FUNDO);
        topo.add(formulario, BorderLayout.CENTER);
        topo.add(Estilo.barraBotoes(btnLancar, btnAlterarNota, btnExcluir), BorderLayout.SOUTH);

        JScrollPane scrollTabela = new JScrollPane(tblAvaliacoes);
        scrollTabela.setBorder(BorderFactory.createTitledBorder("Avaliações da matrícula"));

        // Rodapé com média e situação
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

        carregarDadosFalsos();
        configurarAcoes();
        atualizarResumo();
    }

    private void carregarDadosFalsos() {
        modeloAvaliacoes.setRowCount(0);
        // No mock só a primeira matrícula tem avaliações
        if (cbMatricula.getSelectedIndex() == 0) {
            for (Object[] avaliacao : DadosFalsos.AVALIACOES) {
                modeloAvaliacoes.addRow(avaliacao);
            }
        }
    }

    private void configurarAcoes() {
        // TODO Sprint 2: buscar as avaliações reais da Matricula escolhida
        cbMatricula.addActionListener(e -> {
            carregarDadosFalsos();
            atualizarResumo();
        });

        // TODO Sprint 2: chamar o núcleo e tratar NotaInvalidaException
        btnLancar.addActionListener(e -> {
            modeloAvaliacoes.addRow(new Object[]{cbTipoAvaliacao.getSelectedItem(), spnNota.getValue()});
            atualizarResumo();
        });

        btnAlterarNota.addActionListener(e -> {
            int linha = tblAvaliacoes.getSelectedRow();
            if (linha < 0) {
                JOptionPane.showMessageDialog(this, "Selecione uma avaliação na tabela.");
                return;
            }
            modeloAvaliacoes.setValueAt(spnNota.getValue(), linha, 1);
            atualizarResumo();
        });

        btnExcluir.addActionListener(e -> {
            int linha = tblAvaliacoes.getSelectedRow();
            if (linha < 0) {
                JOptionPane.showMessageDialog(this, "Selecione uma avaliação na tabela.");
                return;
            }
            modeloAvaliacoes.removeRow(linha);
            atualizarResumo();
        });

        // Selecionar avaliação -> carrega tipo e nota no formulário
        tblAvaliacoes.getSelectionModel().addListSelectionListener(e -> {
            int linha = tblAvaliacoes.getSelectedRow();
            if (e.getValueIsAdjusting() || linha < 0) {
                return;
            }
            cbTipoAvaliacao.setSelectedItem(modeloAvaliacoes.getValueAt(linha, 0));
            spnNota.setValue(modeloAvaliacoes.getValueAt(linha, 1));
        });
    }

    /** Calcula a média só para exibição (na Sprint 2 virá de Matricula). */
    private void atualizarResumo() {
        int quantidade = modeloAvaliacoes.getRowCount();
        if (quantidade == 0) {
            lblMedia.setText("Média: -");
            lblSituacao.setText("Situação: sem avaliações");
            lblSituacao.setForeground(Estilo.COR_SECUNDARIA);
            return;
        }
        double soma = 0;
        for (int i = 0; i < quantidade; i++) {
            soma += ((Number) modeloAvaliacoes.getValueAt(i, 1)).doubleValue();
        }
        double media = soma / quantidade;
        boolean aprovado = media >= MEDIA_APROVACAO;

        lblMedia.setText(String.format("Média: %.2f", media));
        lblSituacao.setText("Situação: " + (aprovado ? "APROVADO" : "REPROVADO"));
        lblSituacao.setForeground(aprovado ? Estilo.COR_SUCESSO : Estilo.COR_PERIGO);
    }
}
