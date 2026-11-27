package br.edu.universidade;

import br.edu.universidade.model.Departamento;
import br.edu.universidade.model.Professor;
import br.edu.universidade.model.Universidade;
import br.edu.universidade.view.Atualizavel;
import br.edu.universidade.view.DepartamentoPanel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.JTextField;
import java.awt.Component;
import java.awt.Container;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes PR #68 (Issue #34 / #19.3) - Aba Departamentos e Vinculação de Professores")
public class TestPR68 {

    private Universidade criarUniversidadeBase() {
        Universidade uni = new Universidade("UFU");
        uni.cadastrarProfessor(new Professor("Dra. Ana Paula", "52998224725", "ana@ufu.br", "SIAPE01", "Doutora"));
        uni.cadastrarProfessor(new Professor("Dr. Carlos Silva", "11144477735", "carlos@ufu.br", "SIAPE02", "Doutor"));
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
    @DisplayName("PR68-CT01 - Deve validar removerProfessor e toString na classe Departamento")
    void deveValidarMetodosAdicionadosEmDepartamento() {
        Departamento depto = new Departamento("FACOM");
        Professor p1 = new Professor("Dra. Ana Paula", "52998224725", "ana@ufu.br", "SIAPE01", "Doutora");
        Professor p2 = new Professor("Dr. Carlos Silva", "11144477735", "carlos@ufu.br", "SIAPE02", "Doutor");

        assertEquals("FACOM", depto.toString(), "toString() deve retornar o nome do departamento");

        depto.adicionarProfessor(p1);
        assertEquals(1, depto.getProfessores().size());

        assertFalse(depto.removerProfessor(p2), "Deve retornar false ao tentar remover professor não vinculado");
        assertTrue(depto.removerProfessor(p1), "Deve retornar true ao remover professor existente");
        assertTrue(depto.getProfessores().isEmpty());
    }

    @Test
    @DisplayName("PR68-CT02 - Deve implementar Atualizavel, carregar combo de professores e exibir botões esperados")
    void deveConfigurarComponentesDaTelaDepartamentos() throws Exception {
        Universidade uni = criarUniversidadeBase();
        DepartamentoPanel panel = new DepartamentoPanel(uni);

        assertTrue(panel instanceof Atualizavel, "DepartamentoPanel deve implementar Atualizavel");

        JComboBox<Professor> cbProfessor = lerCampo(panel, "cbProfessor");
        assertEquals(2, cbProfessor.getItemCount(), "Combo deve listar os professores cadastrados na Universidade");

        List<JButton> botoes = buscarComponentes(panel, JButton.class);
        List<String> textos = botoes.stream().map(JButton::getText).toList();
        assertTrue(textos.containsAll(List.of("Salvar", "Limpar", "Excluir", "Adicionar professor", "Remover professor")),
                "Deve conter todos os botões de CRUD e vinculação de professores");
    }

    @Test
    @DisplayName("PR68-CT03 - Deve cadastrar departamento e bloquear nome vazio ou duplicado")
    void deveCadastrarDepartamentoEValidarDuplicidade() throws Exception {
        Universidade uni = criarUniversidadeBase();
        DepartamentoPanel panel = new DepartamentoPanel(uni);

        JTextField txtNome = lerCampo(panel, "txtNome");
        JTable tblDepartamentos = lerCampo(panel, "tblDepartamentos");

        // Tentativa com nome vazio
        txtNome.setText("   ");
        invocarMetodoPrivado(panel, "salvar");
        assertTrue(uni.getDepartamentos().isEmpty(), "Não deve cadastrar departamento com nome vazio");

        // Cadastro válido
        txtNome.setText("FACOM");
        invocarMetodoPrivado(panel, "salvar");
        assertEquals(1, uni.getDepartamentos().size());
        assertEquals("FACOM", uni.getDepartamentos().get(0).getNome());
        assertEquals(1, tblDepartamentos.getRowCount());

        // Tentativa de nome duplicado (case-insensitive)
        txtNome.setText("facom");
        invocarMetodoPrivado(panel, "salvar");
        assertEquals(1, uni.getDepartamentos().size(), "Não deve permitir departamento com nome duplicado");
    }

    @Test
    @DisplayName("PR68-CT04 - Deve vincular e desvincular professor ao departamento selecionado bloqueando repetidos")
    void deveVincularEDesvincularProfessorNoDepartamento() throws Exception {
        Universidade uni = criarUniversidadeBase();
        Departamento facom = new Departamento("FACOM");
        uni.adicionarDepartamento(facom);

        DepartamentoPanel panel = new DepartamentoPanel(uni);
        JTable tblDepartamentos = lerCampo(panel, "tblDepartamentos");
        JComboBox<Professor> cbProfessor = lerCampo(panel, "cbProfessor");
        DefaultListModel<Professor> modeloProfessoresDepto = lerCampo(panel, "modeloProfessoresDepto");

        // Seleciona o departamento FACOM na tabela
        tblDepartamentos.setRowSelectionInterval(0, 0);
        cbProfessor.setSelectedIndex(0); // Dra. Ana Paula

        // Vincula professor
        invocarMetodoPrivado(panel, "adicionarProfessor");
        assertEquals(1, facom.getProfessores().size());
        assertEquals(1, modeloProfessoresDepto.getSize());
        assertEquals(1, tblDepartamentos.getValueAt(0, 1), "Coluna de contagem na tabela deve atualizar para 1");

        // Tenta vincular o mesmo professor novamente
        invocarMetodoPrivado(panel, "adicionarProfessor");
        assertEquals(1, facom.getProfessores().size(), "Não deve permitir vincular professor repetido");

        // Remove professor pelo método do modelo/painel
        javax.swing.JList<Professor> lstProfessoresDepto = lerCampo(panel, "lstProfessoresDepto");
        lstProfessoresDepto.setSelectedIndex(0);
        invocarMetodoPrivado(panel, "removerProfessor");
        assertTrue(facom.getProfessores().isEmpty(), "Professor deve ser removido do departamento");
        assertEquals(0, tblDepartamentos.getValueAt(0, 1));
    }
}