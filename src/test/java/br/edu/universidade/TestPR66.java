package br.edu.universidade;

import br.edu.universidade.exception.MatriculaDuplicadaException;
import br.edu.universidade.exception.NotaInvalidaException;
import br.edu.universidade.exception.TurmaCheiaException;
import br.edu.universidade.model.Aluno;
import br.edu.universidade.model.Curso;
import br.edu.universidade.model.Disciplina;
import br.edu.universidade.model.Matricula;
import br.edu.universidade.model.Professor;
import br.edu.universidade.model.Turma;
import br.edu.universidade.model.Universidade;
import br.edu.universidade.persistence.RepositorioArquivo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes PR #66 (Issue #14) - TesteConsole: Fluxo Completo do Núcleo")
public class TestPR66 {

    @Test
    @DisplayName("PR66-CT01 - Deve executar main() do TesteConsole sem falhas e imprimir TUDO OK")
    void deveExecutarMainDoTesteConsoleSemFalhas() throws Exception {
        PrintStream saidaOriginal = System.out;
        ByteArrayOutputStream bufferSaida = new ByteArrayOutputStream();

        try {
            System.setOut(new PrintStream(bufferSaida, true, StandardCharsets.UTF_8));
            assertDoesNotThrow(() -> TesteConsole.main(new String[]{}),
                    "TesteConsole.main deve concluir sem lançar exceções");
        } finally {
            System.setOut(saidaOriginal);
        }

        String logConsole = bufferSaida.toString(StandardCharsets.UTF_8);
        assertTrue(logConsole.contains("=== 1. Fluxo feliz ==="), "Deve executar a etapa 1 (Fluxo feliz)");
        assertTrue(logConsole.contains("=== 2. Excecoes nos casos invalidos ==="), "Deve executar a etapa 2 (Exceções)");
        assertTrue(logConsole.contains("=== 3. Salvar e recarregar ==="), "Deve executar a etapa 3 (Persistência)");
        assertFalse(logConsole.contains("[FALHA]"), "Nenhuma verificação do console pode falhar");
        assertTrue(logConsole.contains("TUDO OK"), "Saída final deve confirmar TUDO OK");
    }

    @Test
    @DisplayName("PR66-CT02 - Deve validar o método auxiliar matricular() para duplicidade e turma cheia")
    void deveValidarRegrasDoMetodoMatricular() throws Exception {
        Method matricularMethod = TesteConsole.class.getDeclaredMethod("matricular", Aluno.class, Turma.class);
        matricularMethod.setAccessible(true);

        Universidade u = new Universidade("UFU");
        Curso curso = new Curso("BSI", "Sistemas de Informação", 3000);
        Disciplina disciplina = new Disciplina("POO01", "POO", 60);
        Professor professor = new Professor("Maria Lima", "52998224725", "maria@uni.br", "SIAPE1", "Doutor");

        Aluno joao = new Aluno("Joao Souza", "11144477735", "joao@uni.br", new Date(), curso);
        Aluno ana = new Aluno("Ana Costa", "39053344705", "ana@uni.br", new Date(), curso);
        u.cadastrarAluno(joao);
        u.cadastrarAluno(ana);

        Turma turma = new Turma("T1", disciplina, "2026/2", professor, 1);

        // Primeira matrícula: sucesso
        Matricula m1 = (Matricula) matricularMethod.invoke(null, joao, turma);
        assertNotNull(m1);
        assertEquals(1, turma.getVagasOcupadas());
        assertTrue(joao.getMatriculas().contains(m1));

        // Segunda matrícula do mesmo aluno: MatriculaDuplicadaException
        Exception exDuplicada = assertThrows(Exception.class, () -> matricularMethod.invoke(null, joao, turma));
        assertInstanceOf(MatriculaDuplicadaException.class, exDuplicada.getCause());

        // Matrícula de outro aluno com turma lotada: TurmaCheiaException
        Exception exCheia = assertThrows(Exception.class, () -> matricularMethod.invoke(null, ana, turma));
        assertInstanceOf(TurmaCheiaException.class, exCheia.getCause());
    }

    @Test
    @DisplayName("PR66-CT03 - Deve persistir e restaurar o fluxo completo em arquivo temporário sem sujar data/")
    void devePersistirERestaurarFluxoEmArquivoTemporario() throws Exception {
        Universidade u = new Universidade("Universidade Teste");
        Curso curso = new Curso("BSI", "Sistemas de Informacao", 3000);
        Disciplina disciplina = new Disciplina("POO01", "Programacao Orientada a Objetos", 60);
        curso.adicionarDisciplina(disciplina);
        u.cadastrarCurso(curso);
        u.cadastrarDisciplina(disciplina);

        Professor professor = new Professor("Maria Lima", "52998224725", "maria@uni.br", "SIAPE1", "Doutor");
        u.cadastrarProfessor(professor);

        Aluno joao = new Aluno("Joao Souza", "11144477735", "joao@uni.br", new Date(), curso);
        u.cadastrarAluno(joao);

        Turma turma = new Turma("T1", disciplina, "2026/2", professor, 2);
        u.cadastrarTurma(turma);

        Matricula m = new Matricula(joao, turma);
        turma.adicionarMatricula(m);
        joao.getMatriculas().add(m);
        m.adicionarAvaliacao("P1", 8.0);
        m.adicionarAvaliacao("P2", 6.0);

        assertThrows(NotaInvalidaException.class, () -> m.adicionarAvaliacao("P3", 11.0));

        Path arquivoTemp = Files.createTempFile("universidade-pr66", ".dat");
        try {
            RepositorioArquivo repo = new RepositorioArquivo();
            repo.salvar(u, arquivoTemp.toString());
            Universidade carregada = repo.carregar(arquivoTemp.toString());

            assertEquals(1, carregada.getAlunos().size());
            assertEquals(1, carregada.getProfessores().size());
            assertEquals(1, carregada.getCursos().size());
            assertEquals(1, carregada.getTurmas().size());
            assertEquals(7.0, carregada.getTurmas().get(0).getMatriculas().get(0).calcularMedia(), 0.0001);
        } finally {
            Files.deleteIfExists(arquivoTemp);
        }
    }
}