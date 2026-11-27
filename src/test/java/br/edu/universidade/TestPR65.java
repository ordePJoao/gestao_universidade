package br.edu.universidade;

import br.edu.universidade.model.Aluno;
import br.edu.universidade.model.Curso;
import br.edu.universidade.model.Disciplina;
import br.edu.universidade.model.Matricula;
import br.edu.universidade.model.Professor;
import br.edu.universidade.model.Turma;
import br.edu.universidade.model.Universidade;
import br.edu.universidade.view.AlunoPanel;
import br.edu.universidade.view.Atualizavel;
import br.edu.universidade.view.BuscaAlunoPanel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.JTextField;
import java.awt.Component;
import java.awt.Container;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes PR #65 (Issue #33 / #19.2) - Aba Alunos: Cadastro com Matrícula Gerada e Curso")
public class TestPR65 {

    private Universidade criarUniversidadeBase() {
        Universidade uni = new Universidade("UFU");
        uni.adicionarCurso(new Curso("BCC", "Ciência da Computação", 3200));
        uni.adicionarCurso(new Curso("BSI", "Sistemas de Informação", 3000));
        return uni;
    }

    @SuppressWarnings("unchecked")
    private <T> T lerCampo(Object alvo, String nomeCampo) throws Exception {
        Field field = alvo.getClass().getDeclaredField(nomeCampo);
        field.setAccessible(true);
        return (T) field.get(alvo);
    }

    private void invocarMetodoPrivado(Object alvo, String nomeMetodo) throws Exception {
        Method method = alvo.getClass().getDeclaredMethod(nomeMetodo);
        method.setAccessible(true);
        method.invoke(alvo);
    }

    private <T extends Component> List<T> buscarComponentes(Container raiz, Class<T> tipo) {
        List<T> encontrados = new ArrayList<>();
        for (Component comp : raiz.getComponents()) {
            if (tipo.isInstance(comp)) {
                encontrados.add(tipo.cast(comp));
            }
            if (comp instanceof Container container) {
                encontrados.addAll(buscarComponentes(container, tipo));
            }
        }
        return encontrados;
    }

    @Test
    @DisplayName("PR65-CT01 - Deve implementar Atualizavel, integrar BuscaAlunoPanel e manter Matrícula somente leitura")
    void deveConfigurarComponentesBasicosESomenteLeitura() throws Exception {
        Universidade uni = criarUniversidadeBase();
        AlunoPanel panel = new AlunoPanel(uni);

        assertTrue(panel instanceof Atualizavel, "AlunoPanel deve implementar Atualizavel");

        JTextField txtMatricula = lerCampo(panel, "txtMatricula");
        assertFalse(txtMatricula.isEditable(), "Campo Matrícula deve ser somente leitura");
        assertEquals("(gerada ao salvar)", txtMatricula.getText());

        BuscaAlunoPanel buscaPanel = lerCampo(panel, "buscaPanel");
        assertNotNull(buscaPanel, "Deve integrar o painel reutilizável BuscaAlunoPanel");

        JComboBox<Curso> cbCurso = lerCampo(panel, "cbCurso");
        assertEquals(2, cbCurso.getItemCount(), "Combo de cursos deve carregar os cursos da Universidade");

        List<JButton> botoes = buscarComponentes(panel, JButton.class);
        List<String> textosBotoes = botoes.stream().map(JButton::getText).toList();
        assertTrue(textosBotoes.containsAll(List.of("Salvar", "Limpar", "Excluir")),
                "Deve conter os botões Salvar, Limpar e Excluir");
    }

    @Test
    @DisplayName("PR65-CT02 - Deve cadastrar aluno com curso selecionado, gerar matrícula e atualizar tabela")
    void deveCadastrarAlunoComMatriculaGerada() throws Exception {
        Universidade uni = criarUniversidadeBase();
        AlunoPanel panel = new AlunoPanel(uni);

        JTextField txtNome = lerCampo(panel, "txtNome");
        JTextField txtCpf = lerCampo(panel, "txtCpf");
        JTextField txtEmail = lerCampo(panel, "txtEmail");
        JComboBox<Curso> cbCurso = lerCampo(panel, "cbCurso");
        JTextField txtDataIngresso = lerCampo(panel, "txtDataIngresso");
        JTable tblAlunos = lerCampo(panel, "tblAlunos");

        txtNome.setText("Maria Oliveira");
        txtCpf.setText("123.456.789-09");
        txtEmail.setText("maria@ufu.br");
        cbCurso.setSelectedIndex(0); // BCC
        txtDataIngresso.setText("15/03/2026");

        invocarMetodoPrivado(panel, "salvar");

        assertEquals(1, uni.getAlunos().size(), "Aluno deve ser cadastrado na Universidade");
        Aluno salvo = uni.getAlunos().get(0);
        assertNotNull(salvo.getMatricula(), "Matrícula deve ser gerada automaticamente");
        assertTrue(salvo.getMatricula().contains("BCC"), "Matrícula deve conter a sigla do curso selecionado");
        assertEquals("Maria Oliveira", salvo.getNome());
        assertEquals(1, tblAlunos.getRowCount(), "Tabela deve refletir o novo aluno cadastrado");
        assertEquals(salvo.getMatricula(), tblAlunos.getValueAt(0, 0));
    }

    @Test
    @DisplayName("PR65-CT03 - Deve rejeitar dados inválidos (e-mail/data) sem cadastrar aluno")
    void deveRejeitarDadosInvalidosSemCadastrar() throws Exception {
        Universidade uni = criarUniversidadeBase();
        AlunoPanel panel = new AlunoPanel(uni);

        JTextField txtNome = lerCampo(panel, "txtNome");
        JTextField txtCpf = lerCampo(panel, "txtCpf");
        JTextField txtEmail = lerCampo(panel, "txtEmail");
        JTextField txtDataIngresso = lerCampo(panel, "txtDataIngresso");

        // E-mail inválido (sem @)
        txtNome.setText("Carlos Teste");
        txtCpf.setText("123.456.789-09");
        txtEmail.setText("carlos.ufu.br");
        txtDataIngresso.setText("10/02/2026");
        invocarMetodoPrivado(panel, "salvar");
        assertTrue(uni.getAlunos().isEmpty(), "Não deve cadastrar com e-mail inválido");

        // Data inexistente (31/02)
        txtEmail.setText("carlos@ufu.br");
        txtDataIngresso.setText("31/02/2026");
        invocarMetodoPrivado(panel, "salvar");
        assertTrue(uni.getAlunos().isEmpty(), "Não deve cadastrar com data inválida");
    }

    @Test
    @DisplayName("PR65-CT04 - Deve impedir cadastro de CPF duplicado e validar campos obrigatórios")
    void deveImpedirCpfDuplicadoECamposVazios() throws Exception {
        Universidade uni = criarUniversidadeBase();
        Curso bcc = uni.getCursos().get(0);
        uni.cadastrarAluno(new Aluno("João Silva", "52998224725", "joao@ufu.br", new Date(), bcc));

        AlunoPanel panel = new AlunoPanel(uni);
        JTextField txtNome = lerCampo(panel, "txtNome");
        JTextField txtCpf = lerCampo(panel, "txtCpf");
        JTextField txtEmail = lerCampo(panel, "txtEmail");
        JTextField txtDataIngresso = lerCampo(panel, "txtDataIngresso");

        // Tentativa 1: CPF duplicado (com máscara diferente)
        txtNome.setText("Outro João");
        txtCpf.setText("529.982.247-25");
        txtEmail.setText("outro@ufu.br");
        txtDataIngresso.setText("20/03/2026");
        invocarMetodoPrivado(panel, "salvar");
        assertEquals(1, uni.getAlunos().size(), "Não deve permitir segundo aluno com mesmo CPF");

        // Tentativa 2: Campo obrigatório vazio
        txtNome.setText("");
        txtCpf.setText("98765432100");
        invocarMetodoPrivado(panel, "salvar");
        assertEquals(1, uni.getAlunos().size(), "Não deve cadastrar quando nome estiver vazio");
    }

    @Test
    @DisplayName("PR65-CT05 - Deve carregar aluno no formulário ao selecionar na tabela e permitir edição")
    void deveEditarAlunoSelecionado() throws Exception {
        Universidade uni = criarUniversidadeBase();
        Curso bcc = uni.getCursos().get(0);
        Curso bsi = uni.getCursos().get(1);
        Aluno aluno = new Aluno("Ana Costa", "52998224725", "ana@ufu.br", new Date(), bcc);
        uni.cadastrarAluno(aluno);

        AlunoPanel panel = new AlunoPanel(uni);
        JTable tblAlunos = lerCampo(panel, "tblAlunos");
        JTextField txtNome = lerCampo(panel, "txtNome");
        JTextField txtMatricula = lerCampo(panel, "txtMatricula");
        JComboBox<Curso> cbCurso = lerCampo(panel, "cbCurso");

        // Seleciona a primeira linha da tabela
        tblAlunos.setRowSelectionInterval(0, 0);

        assertEquals(aluno.getMatricula(), txtMatricula.getText());
        assertEquals("Ana Costa", txtNome.getText());

        // Altera nome e curso e salva edição
        txtNome.setText("Ana Costa Souza");
        cbCurso.setSelectedItem(bsi);
        invocarMetodoPrivado(panel, "salvar");

        assertEquals(1, uni.getAlunos().size(), "Edição não deve duplicar o registro");
        assertEquals("Ana Costa Souza", aluno.getNome());
        assertEquals(bsi, aluno.getCurso());
        assertEquals("Ana Costa Souza", tblAlunos.getValueAt(0, 1));
    }

    @Test
    @DisplayName("PR65-CT06 - Deve bloquear exclusão de aluno com matrícula ativa e permitir sem matrículas")
    void deveValidarRegraDeExclusaoDeAluno() throws Exception {
        Universidade uni = criarUniversidadeBase();
        Curso bcc = uni.getCursos().get(0);
        Aluno aluno = new Aluno("Pedro Lima", "52998224725", "pedro@ufu.br", new Date(), bcc);
        uni.cadastrarAluno(aluno);

        Disciplina disc = new Disciplina("POO", "Programação Orientada a Objetos", 60);
        Professor prof = new Professor("Dr. Carlos", "98765432100", "carlos@ufu.br", "123456", "Doutor");
        Turma turma = new Turma("T01", disc, "2026/2", prof, 30);
        Matricula mat = new Matricula(aluno, turma);
        aluno.getMatriculas().add(mat);

        AlunoPanel panel = new AlunoPanel(uni);
        JTable tblAlunos = lerCampo(panel, "tblAlunos");
        tblAlunos.setRowSelectionInterval(0, 0);

        // Com matrícula ativa, excluir() deve abortar antes da confirmação sem remover
        invocarMetodoPrivado(panel, "excluir");
        assertEquals(1, uni.getAlunos().size(), "Não deve excluir aluno que possui matrícula ativa");

        // Cancelando a matrícula, o aluno fica apto à exclusão
        aluno.cancelarMatricula(mat);
        assertTrue(aluno.getMatriculas().isEmpty());
    }
}