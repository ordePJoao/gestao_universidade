package br.edu.universidade.view;

import br.edu.universidade.model.Aluno;
import br.edu.universidade.model.Universidade;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.List;
import java.util.function.Consumer;

/**
 * Painel reutilizável de busca e conferência de aluno (task #35 / #21.1).
 * Busca por matrícula, CPF ou parte do nome via {@link Universidade#buscarAluno(String)}.
 */
public class BuscaAlunoPanel extends JPanel {

    private final Universidade universidade;
    private final Consumer<Aluno> onAlunoSelecionado;

    private final JTextField txtBusca = new JTextField(22);
    private final JButton btnBuscar = Estilo.botaoPrimario("Buscar");
    private final JButton btnLimpar = Estilo.botaoSecundario("Limpar");
    private final JComboBox<Aluno> cbResultados = new JComboBox<>();
    private final JLabel lblDados = new JLabel("Nenhum aluno selecionado.");

    private boolean silenciarEventos = false;

    public BuscaAlunoPanel(Universidade universidade, Consumer<Aluno> onAlunoSelecionado) {
        this.universidade = universidade;
        this.onAlunoSelecionado = onAlunoSelecionado;

        setLayout(new BorderLayout(6, 6));
        setBackground(Estilo.COR_FUNDO);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(Estilo.COR_PRIMARIA),
                        "Buscar aluno (matrícula, CPF ou nome)",
                        0, 0, Estilo.FONTE_NEGRITO, Estilo.COR_PRIMARIA),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)));

        txtBusca.setFont(Estilo.FONTE_PADRAO);
        Componentes.textoDoCombo(cbResultados, a -> a.getMatricula() + " - " + a.getNome());

        JPanel linhaBusca = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 2));
        linhaBusca.setBackground(Estilo.COR_FUNDO);
        JLabel lblTermo = new JLabel("Termo:");
        lblTermo.setFont(Estilo.FONTE_PADRAO);
        linhaBusca.add(lblTermo);
        linhaBusca.add(txtBusca);
        linhaBusca.add(btnBuscar);
        linhaBusca.add(btnLimpar);

        JPanel linhaResultado = new JPanel(new BorderLayout(8, 4));
        linhaResultado.setBackground(Estilo.COR_FUNDO);
        JLabel lblSel = new JLabel("Aluno:");
        lblSel.setFont(Estilo.FONTE_PADRAO);
        linhaResultado.add(lblSel, BorderLayout.WEST);
        linhaResultado.add(cbResultados, BorderLayout.CENTER);

        lblDados.setFont(Estilo.FONTE_PADRAO);
        lblDados.setForeground(Estilo.COR_SECUNDARIA);

        JPanel corpo = new JPanel(new GridLayout(3, 1, 4, 4));
        corpo.setBackground(Estilo.COR_FUNDO);
        corpo.add(linhaBusca);
        corpo.add(linhaResultado);
        corpo.add(lblDados);

        add(corpo, BorderLayout.CENTER);

        btnBuscar.addActionListener(e -> executarBusca(true));
        txtBusca.addActionListener(e -> executarBusca(true));
        btnLimpar.addActionListener(e -> {
            txtBusca.setText("");
            recarregar();
        });
        cbResultados.addActionListener(e -> {
            if (!silenciarEventos) {
                atualizarDadosENotificar();
            }
        });

        recarregar();
    }

    /** Recarrega a lista respeitando o termo digitado (ou todos os alunos se vazio). */
    public void recarregar() {
        executarBusca(false);
    }

    public Aluno getAlunoSelecionado() {
        return (Aluno) cbResultados.getSelectedItem();
    }

    private void executarBusca(boolean avisarSeVazio) {
        String termo = txtBusca.getText().trim();
        List<Aluno> encontrados = termo.isEmpty()
                ? universidade.getAlunos()
                : universidade.buscarAluno(termo);

        silenciarEventos = true;
        Componentes.recarregarCombo(cbResultados, encontrados);
        silenciarEventos = false;

        if (avisarSeVazio && !termo.isEmpty() && encontrados.isEmpty()) {
            Componentes.aviso(this, "Nenhum aluno encontrado para \"" + termo + "\".", "Busca de aluno");
        }
        atualizarDadosENotificar();
    }

    private void atualizarDadosENotificar() {
        Aluno aluno = getAlunoSelecionado();
        if (aluno == null) {
            lblDados.setText("Nenhum aluno selecionado.");
            lblDados.setForeground(Estilo.COR_SECUNDARIA);
        } else {
            String curso = aluno.getCurso() != null ? aluno.getCurso().getNome() : "Sem curso";
            lblDados.setText("Dados: " + aluno.getNome()
                    + " | Matrícula: " + aluno.getMatricula()
                    + " | CPF: " + aluno.getCpf()
                    + " | Curso: " + curso);
            lblDados.setForeground(Estilo.COR_PRIMARIA);
        }
        if (onAlunoSelecionado != null) {
            onAlunoSelecionado.accept(aluno);
        }
    }
}
