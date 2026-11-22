package br.edu.universidade;

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

@DisplayName("Suíte de Testes de Persistência em Arquivo")
class PersistenciaTest {

    private static final String ARQUIVO_TESTE = "data/teste_universidade.dat";
    private RepositorioArquivo repositorio;

    @BeforeEach
    void setUp() {
        repositorio = new RepositorioArquivo();
        limparArquivoTeste();
    }

    @AfterEach
    void tearDown() {
        limparArquivoTeste();
    }

    private void limparArquivoTeste() {
        File arquivo = new File(ARQUIVO_TESTE);
        if (arquivo.exists()) {
            arquivo.delete();
        }
    }

    @Test
    @DisplayName("CT-PERSIST-01: Deve salvar e carregar Universidade com dados persistidos em cascata")
    void deveSalvarECarregarUniversidade() throws Exception {
        Universidade uOriginal = new Universidade("UFU - Universidade Federal de Uberlândia");

        Aluno aluno = new Aluno("Lethicia Santos", "123.456.789-00", "lethicia@ufu.br", "20261BSI01", new Date());
        Professor prof = new Professor("Dr. Alan Turing", "000.111.222-33", "alan@ufu.br", "98765", "Doutor");
        Curso curso = new Curso("BSI", "Sistemas de Informação", 3000);
        Disciplina disciplina = new Disciplina("POO", "Programação Orientada a Objetos", 60);
        curso.adicionarDisciplina(disciplina);
        Turma turma = new Turma("POO-2026-1", disciplina, "2026/1", prof, 40);

        uOriginal.cadastrarAluno(aluno);
        uOriginal.cadastrarProfessor(prof);
        uOriginal.cadastrarCurso(curso);
        uOriginal.cadastrarDisciplina(disciplina);
        uOriginal.cadastrarTurma(turma);

        // Salva no arquivo de teste
        repositorio.salvar(uOriginal, ARQUIVO_TESTE);

        File arquivoGerado = new File(ARQUIVO_TESTE);
        assertTrue(arquivoGerado.exists(), "O arquivo de persistência deve ter sido criado em disco");
        assertTrue(arquivoGerado.length() > 0, "O arquivo de persistência não pode estar vazio");

        // Carrega do arquivo
        Universidade uCarregada = repositorio.carregar(ARQUIVO_TESTE);

        assertNotNull(uCarregada, "A universidade carregada não deve ser nula");
        assertEquals("UFU - Universidade Federal de Uberlândia", uCarregada.getNome());
        assertEquals(1, uCarregada.getAlunos().size(), "Deve conter 1 aluno persistido");
        assertEquals(1, uCarregada.getProfessores().size(), "Deve conter 1 professor persistido");
        assertEquals(1, uCarregada.getCursos().size(), "Deve conter 1 curso persistido");
        assertEquals(1, uCarregada.getDisciplinas().size(), "Deve conter 1 disciplina persistida");
        assertEquals(1, uCarregada.getTurmas().size(), "Deve conter 1 turma persistida");

        // Verifica integridade dos dados recuperados
        Aluno alunoRecuperado = uCarregada.buscarAlunoPorMatricula("20261BSI01").orElse(null);
        assertNotNull(alunoRecuperado);
        assertEquals("Lethicia Santos", alunoRecuperado.getNome());
        assertEquals("20261BSI01", alunoRecuperado.getIdent());

        Professor profRecuperado = uCarregada.buscarProfessorPorSiape("98765").orElse(null);
        assertNotNull(profRecuperado);
        assertEquals("Dr. Alan Turing", profRecuperado.getNome());

        Curso cursoRecuperado = uCarregada.buscarCursoPorCodigo("BSI").orElse(null);
        assertNotNull(cursoRecuperado);
        assertEquals("Sistemas de Informação", cursoRecuperado.getNome());
        assertEquals(1, cursoRecuperado.getDisciplinas().size());
    }

    @Test
    @DisplayName("CT-PERSIST-02: Se o arquivo não existir, deve retornar uma Universidade nova e vazia")
    void deveRetornarUniversidadeVaziaSeArquivoNaoExistir() throws Exception {
        String arquivoInexistente = "data/arquivo_que_nunca_existiu.dat";
        File f = new File(arquivoInexistente);
        if (f.exists()) f.delete();

        Universidade u = repositorio.carregar(arquivoInexistente);

        assertNotNull(u, "Deve retornar uma instância não nula");
        assertTrue(u.getAlunos().isEmpty(), "Lista de alunos deve iniciar vazia");
        assertTrue(u.getProfessores().isEmpty(), "Lista de professores deve iniciar vazia");
        assertTrue(u.getCursos().isEmpty(), "Lista de cursos deve iniciar vazia");
        assertTrue(u.getDisciplinas().isEmpty(), "Lista de disciplinas deve iniciar vazia");
        assertTrue(u.getTurmas().isEmpty(), "Lista de turmas deve iniciar vazia");
    }

    @Test
    @DisplayName("CT-PERSIST-03: Validação de parâmetros nulos e inválidos")
    void deveValidarParametrosInvalidos() {
        assertThrows(IllegalArgumentException.class, () -> repositorio.salvar(null, ARQUIVO_TESTE));
        assertThrows(IllegalArgumentException.class, () -> repositorio.salvar(new Universidade(), ""));
        assertThrows(IllegalArgumentException.class, () -> repositorio.carregar(null));
        assertThrows(IllegalArgumentException.class, () -> repositorio.carregar("   "));
    }
}
