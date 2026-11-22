package br.edu.universidade;

import br.edu.universidade.model.*;
import br.edu.universidade.persistence.PersistenciaArquivo;
import br.edu.universidade.persistence.RepositorioArquivo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes PR #13 (#15) - Persistência em Arquivo (Cobertura Completa)")
class PersistenciaTest {

    private static final String ARQUIVO_TESTE = "data/teste_universidade.dat";
    private RepositorioArquivo repositorio;

    @BeforeEach
    void setUp() {
        repositorio = new RepositorioArquivo();
        limparArquivo(ARQUIVO_TESTE);
    }

    @AfterEach
    void tearDown() {
        limparArquivo(ARQUIVO_TESTE);
        limparArquivo("data/subpasta_nova/teste_dir.dat");
        File subpasta = new File("data/subpasta_nova");
        if (subpasta.exists()) {
            subpasta.delete();
        }
    }

    private void limparArquivo(String caminho) {
        File arquivo = new File(caminho);
        if (arquivo.exists()) {
            arquivo.delete();
        }
    }

    @Test
    @DisplayName("PR13-CT01: Deve salvar e carregar Universidade completa em cascata (incluindo Matrículas e Avaliações)")
    void deveSalvarECarregarUniversidadeCompletaEmCascata() throws Exception {
        Universidade uOriginal = new Universidade("UFU - Universidade Federal de Uberlândia");

        Aluno aluno = new Aluno("Lethicia Santos", "123.456.789-00", "lethicia@ufu.br", "20261BSI01", new Date());
        Professor prof = new Professor("Dr. Alan Turing", "000.111.222-33", "alan@ufu.br", "98765", "Doutor");
        Curso curso = new Curso("BSI", "Sistemas de Informação", 3000);
        Disciplina disciplina = new Disciplina("POO", "Programação Orientada a Objetos", 60);
        curso.adicionarDisciplina(disciplina);

        Turma turma = new Turma("POO-2026-1", disciplina, "2026/1", prof, 40);
        Matricula matricula = new Matricula(aluno, turma);
        matricula.adicionarAvaliacao("P1", 8.5);
        matricula.adicionarAvaliacao("P2", 9.5);
        aluno.getMatriculas().add(matricula);

        uOriginal.cadastrarAluno(aluno);
        uOriginal.cadastrarProfessor(prof);
        uOriginal.cadastrarCurso(curso);
        uOriginal.cadastrarDisciplina(disciplina);
        uOriginal.cadastrarTurma(turma);

        repositorio.salvar(uOriginal, ARQUIVO_TESTE);

        File arquivoGerado = new File(ARQUIVO_TESTE);
        assertTrue(arquivoGerado.exists(), "O arquivo de persistência deve ser criado em disco");
        assertTrue(arquivoGerado.length() > 0, "O arquivo não pode estar vazio");

        Universidade uCarregada = repositorio.carregar(ARQUIVO_TESTE);

        assertNotNull(uCarregada);
        assertEquals("UFU - Universidade Federal de Uberlândia", uCarregada.getNome());
        assertEquals(1, uCarregada.getAlunos().size());
        assertEquals(1, uCarregada.getProfessores().size());
        assertEquals(1, uCarregada.getCursos().size());
        assertEquals(1, uCarregada.getDisciplinas().size());
        assertEquals(1, uCarregada.getTurmas().size());

        Aluno alunoRecuperado = uCarregada.buscarAlunoPorMatricula("20261BSI01").orElseThrow();
        assertEquals("Lethicia Santos", alunoRecuperado.getNome());
        assertEquals(1, alunoRecuperado.getMatriculas().size());
        assertEquals(9.0, alunoRecuperado.getMatriculas().get(0).calcularMedia(), 0.0001);

        Professor profRecuperado = uCarregada.buscarProfessorPorSiape("98765").orElseThrow();
        assertEquals("Dr. Alan Turing", profRecuperado.getNome());

        Curso cursoRecuperado = uCarregada.buscarCursoPorCodigo("BSI").orElseThrow();
        assertEquals(1, cursoRecuperado.getDisciplinas().size());
    }

    @Test
    @DisplayName("PR13-CT02: Deve retornar Universidade vazia na primeira execução (arquivo inexistente)")
    void deveRetornarUniversidadeVaziaSeArquivoNaoExistir() throws Exception {
        String arquivoInexistente = "data/arquivo_que_nunca_existiu.dat";
        limparArquivo(arquivoInexistente);

        Universidade u = repositorio.carregar(arquivoInexistente);

        assertNotNull(u);
        assertTrue(u.getAlunos().isEmpty());
        assertTrue(u.getProfessores().isEmpty());
        assertTrue(u.getCursos().isEmpty());
        assertTrue(u.getDisciplinas().isEmpty());
        assertTrue(u.getTurmas().isEmpty());
    }

    @Test
    @DisplayName("PR13-CT03: Deve criar diretórios pais automaticamente se não existirem")
    void deveCriarDiretoriosPaisAutomaticamente() throws Exception {
        String caminhoComSubpasta = "data/subpasta_nova/teste_dir.dat";
        Universidade u = new Universidade("UFU");

        repositorio.salvar(u, caminhoComSubpasta);

        assertTrue(new File(caminhoComSubpasta).exists());
    }

    @Test
    @DisplayName("PR13-CT04: Deve lançar IOException se o arquivo estiver corrompido")
    void deveLancarExcecaoParaArquivoCorrompido() throws Exception {
        try (FileOutputStream fos = new FileOutputStream(ARQUIVO_TESTE)) {
            fos.write("conteudo_invalido_nao_serializado".getBytes(StandardCharsets.UTF_8));
        }

        assertThrows(IOException.class, () -> repositorio.carregar(ARQUIVO_TESTE));
    }

    @Test
    @DisplayName("PR13-CT05: Deve validar parâmetros nulos ou em branco")
    void deveValidarParametrosInvalidos() {
        assertThrows(IllegalArgumentException.class, () -> repositorio.salvar(null, ARQUIVO_TESTE));
        assertThrows(IllegalArgumentException.class, () -> repositorio.salvar(new Universidade(), ""));
        assertThrows(IllegalArgumentException.class, () -> repositorio.salvar(new Universidade(), null));
        assertThrows(IllegalArgumentException.class, () -> repositorio.carregar(null));
        assertThrows(IllegalArgumentException.class, () -> repositorio.carregar("   "));
    }

    @Test
    @DisplayName("PR13-CT06: Alias PersistenciaArquivo deve funcionar de forma idêntica a RepositorioArquivo")
    void deveFuncionarComAliasPersistenciaArquivo() throws Exception {
        PersistenciaArquivo alias = new PersistenciaArquivo();
        assertEquals("data/universidade.dat", RepositorioArquivo.ARQUIVO_PADRAO);

        Universidade u = new Universidade("UFU Alias");
        alias.salvar(u, ARQUIVO_TESTE);
        Universidade carregada = alias.carregar(ARQUIVO_TESTE);

        assertEquals("UFU Alias", carregada.getNome());
    }
}