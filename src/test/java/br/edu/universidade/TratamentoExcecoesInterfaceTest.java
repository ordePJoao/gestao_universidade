package br.edu.universidade;

import br.edu.universidade.exception.CpfInvalidoException;
import br.edu.universidade.exception.MatriculaDuplicadaException;
import br.edu.universidade.exception.NotaInvalidaException;
import br.edu.universidade.exception.TurmaCheiaException;
import br.edu.universidade.model.Aluno;
import br.edu.universidade.model.Curso;
import br.edu.universidade.model.Disciplina;
import br.edu.universidade.model.Professor;
import br.edu.universidade.model.Turma;
import br.edu.universidade.model.Universidade;
import br.edu.universidade.persistence.RepositorioArquivo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Suíte de Testes da Task #23 - Tratamento de Exceções e Persistência Automática")
class TratamentoExcecoesInterfaceTest {

    private static final String ARQUIVO_TESTE = "data/teste_task23.dat";
    private RepositorioArquivo repositorio;

    @BeforeEach
    void setUp() {
        repositorio = new RepositorioArquivo();
        limparArquivo();
    }

    @AfterEach
    void tearDown() {
        limparArquivo();
    }

    private void limparArquivo() {
        File f = new File(ARQUIVO_TESTE);
        if (f.exists()) {
            f.delete();
        }
    }

    @Test
    @DisplayName("CT01: Turma deve lançar TurmaCheiaException com mensagem amigável quando não houver vagas")
    void deveLancarTurmaCheiaException() {
        Disciplina d = new Disciplina("POO", "Programação Orientada a Objetos", 60);
        Professor p = new Professor("Dr. Turing", "111.222.333-44", "turing@ufu.br", "12345", "Doutor");
        Turma turma = new Turma("POO-1", d, "2026/1", p, 1);

        Aluno a1 = new Aluno("Ana Silva", "222.333.444-55", "ana@ufu.br", "2026101", new Date());
        Aluno a2 = new Aluno("Bruno Costa", "333.444.555-66", "bruno@ufu.br", "2026102", new Date());

        assertDoesNotThrow(() -> turma.matricular(a1));

        TurmaCheiaException ex = assertThrows(TurmaCheiaException.class, () -> turma.matricular(a2));
        assertEquals("POO-1", ex.getCodigoTurma());
        assertEquals(1, ex.getVagasTotais());
        assertTrue(ex.getMessage().contains("POO-1"));
        assertTrue(ex.getMessage().contains("1"));
    }

    @Test
    @DisplayName("CT02: Turma deve lançar MatriculaDuplicadaException quando aluno já estiver matriculado")
    void deveLancarMatriculaDuplicadaException() {
        Disciplina d = new Disciplina("BD1", "Banco de Dados 1", 60);
        Professor p = new Professor("Dra. Margaret", "444.555.666-77", "margaret@ufu.br", "54321", "Doutora");
        Turma turma = new Turma("BD1-1", d, "2026/1", p, 30);

        Aluno a = new Aluno("Lethicia Santos", "555.666.777-88", "lethicia@ufu.br", "2026103", new Date());

        assertDoesNotThrow(() -> turma.matricular(a));

        MatriculaDuplicadaException ex = assertThrows(MatriculaDuplicadaException.class, () -> turma.matricular(a));
        assertTrue(ex.getMessage().contains("Lethicia"));
        assertTrue(ex.getMessage().contains("BD1-1"));
    }

    @Test
    @DisplayName("CT03: Validação de CPF deve lançar CpfInvalidoException quando formato for menor que 11 dígitos")
    void deveValidarCpfInvalidoException() {
        CpfInvalidoException ex = new CpfInvalidoException("123.456");
        assertEquals("123.456", ex.getCpfInformado());
        assertTrue(ex.getMessage().contains("123.456"));
    }

    @Test
    @DisplayName("CT04: Validação de Nota deve lançar NotaInvalidaException fora de 0 a 10")
    void deveValidarNotaInvalidaException() {
        NotaInvalidaException exNegativo = new NotaInvalidaException(-1.0);
        assertEquals(-1.0, exNegativo.getNotaInformada());
        assertTrue(exNegativo.getMessage().contains("-1.0"));

        NotaInvalidaException exAcima = new NotaInvalidaException(10.5);
        assertEquals(10.5, exAcima.getNotaInformada());
        assertTrue(exAcima.getMessage().contains("10.5"));
    }

    @Test
    @DisplayName("CT05: Persistência automática deve carregar ao iniciar e salvar com sucesso")
    void deveSalvarECarregarAutomaticamente() throws Exception {
        Universidade u = new Universidade("UFU");
        Curso c = new Curso("BSI", "Sistemas de Informação", 3000);
        u.cadastrarCurso(c);

        repositorio.salvar(u, ARQUIVO_TESTE);

        File f = new File(ARQUIVO_TESTE);
        assertTrue(f.exists());
        assertTrue(f.length() > 0);

        Universidade carregada = repositorio.carregar(ARQUIVO_TESTE);
        assertNotNull(carregada);
        assertEquals(1, carregada.getCursos().size());
        assertEquals("BSI", carregada.getCursos().get(0).getCodigo());
    }
}
