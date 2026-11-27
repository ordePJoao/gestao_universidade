package br.edu.universidade;

import br.edu.universidade.model.Universidade;
import br.edu.universidade.view.AlunoPanel;
import br.edu.universidade.view.CursoPanel;
import br.edu.universidade.view.DepartamentoPanel;
import br.edu.universidade.view.DisciplinaPanel;
import br.edu.universidade.view.MatriculaPanel;
import br.edu.universidade.view.NotasPanel;
import br.edu.universidade.view.ProfessorPanel;
import br.edu.universidade.view.TelaPrincipal;
import br.edu.universidade.view.TurmaPanel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JTabbedPane;
import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes PR #69 (Issue #22 / #15 / #18) - TelaPrincipal e Navegação entre Abas")
public class TestPR69 {

    @SuppressWarnings("unchecked")
    private <T> T lerCampo(Object alvo, String nomeCampo) throws Exception {
        Field field = alvo.getClass().getDeclaredField(nomeCampo);
        field.setAccessible(true);
        return (T) field.get(alvo);
    }

    @Test
    @DisplayName("PR69-CT01 - Deve inicializar janela principal com título, tamanho mínimo e fechamento controlado")
    void deveConfigurarJanelaPrincipalCorretamente() throws Exception {
        TelaPrincipal tela = new TelaPrincipal();
        try {
            assertEquals("Gestão Universidade - Secretaria", tela.getTitle());
            assertEquals(JFrame.DO_NOTHING_ON_CLOSE, tela.getDefaultCloseOperation(),
                    "Fechamento deve passar pelo confirmarSaida()");
            assertNotNull(tela.getUniversidade(), "Deve manter uma instância única de Universidade");
            assertEquals(1000, tela.getMinimumSize().width);
            assertEquals(650, tela.getMinimumSize().height);
        } finally {
            tela.dispose();
        }
    }

    @Test
    @DisplayName("PR69-CT02 - Deve registrar todas as 8 abas oficiais incluindo DepartamentoPanel")
    void deveRegistrarTodasAsAbasOficiais() throws Exception {
        TelaPrincipal tela = new TelaPrincipal();
        try {
            JTabbedPane abas = lerCampo(tela, "abas");
            List<String> titulosEsperados = List.of(
                    "Alunos", "Professores", "Departamentos",
                    "Cursos", "Disciplinas", "Turmas",
                    "Matrículas", "Notas"
            );

            assertEquals(titulosEsperados.size(), abas.getTabCount(), "Deve conter exatamente 8 abas");
            for (int i = 0; i < titulosEsperados.size(); i++) {
                assertEquals(titulosEsperados.get(i), abas.getTitleAt(i));
            }

            assertInstanceOf(AlunoPanel.class, abas.getComponentAt(0));
            assertInstanceOf(ProfessorPanel.class, abas.getComponentAt(1));
            assertInstanceOf(DepartamentoPanel.class, abas.getComponentAt(2),
                    "Aba Departamentos deve usar DepartamentoPanel oficial após resolução de conflito");
            assertInstanceOf(CursoPanel.class, abas.getComponentAt(3));
            assertInstanceOf(DisciplinaPanel.class, abas.getComponentAt(4));
            assertInstanceOf(TurmaPanel.class, abas.getComponentAt(5));
            assertInstanceOf(MatriculaPanel.class, abas.getComponentAt(6));
            assertInstanceOf(NotasPanel.class, abas.getComponentAt(7));
        } finally {
            tela.dispose();
        }
    }

    @Test
    @DisplayName("PR69-CT03 - Deve navegar entre abas via abrirAba() e configurar barra de menus")
    void deveNavegarEntreAbasEConfigurarMenus() throws Exception {
        TelaPrincipal tela = new TelaPrincipal();
        try {
            JTabbedPane abas = lerCampo(tela, "abas");
            JMenuBar menuBar = tela.getJMenuBar();

            assertNotNull(menuBar, "Deve possuir barra de menu superior");
            assertEquals(4, menuBar.getMenuCount(), "Deve conter menus Arquivo, Cadastros, Acadêmico e Ajuda");

            tela.abrirAba("Departamentos");
            assertEquals(2, abas.getSelectedIndex());

            tela.abrirAba("Notas");
            assertEquals(7, abas.getSelectedIndex());

            // Aba inexistente não deve alterar seleção atual
            tela.abrirAba("AbaInexistente");
            assertEquals(7, abas.getSelectedIndex());
        } finally {
            tela.dispose();
        }
    }

    @Test
    @DisplayName("PR69-CT04 - Deve compartilhar a mesma instância de Universidade e permitir salvar()")
    void deveCompartilharUniversidadeESalvar() {
        TelaPrincipal tela = new TelaPrincipal();
        try {
            Universidade u = tela.getUniversidade();
            assertNotNull(u);
            assertTrue(tela.salvar(), "Método salvar() deve gravar dados sem erro");
        } finally {
            tela.dispose();
        }
    }
}