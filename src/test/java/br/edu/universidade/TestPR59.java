// Teste PR#59: #11.2 Busca de aluno por matrícula, CPF ou nome _ (Issue #31 github)
package br.edu.universidade;

import br.edu.universidade.exception.AlunoNaoEncontradoException;
import br.edu.universidade.model.Aluno;
import br.edu.universidade.model.Curso;
import br.edu.universidade.model.Universidade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes PR #59 (Issue #31 / #11.2) - Busca de Aluno por Matrícula, CPF ou Nome")
class TestPR59 {

    private Universidade ufu;
    private Aluno jose;
    private Aluno maria;
    private Aluno joana;

    @BeforeEach
    void setUp() {
        ufu = new Universidade("UFU");
        Curso bsi = new Curso("BSI", "Sistemas de Informação", 3000);
        ufu.cadastrarCurso(bsi);

        jose = new Aluno("José Álvares", "111.222.333-44", "jose@ufu.br", "2026BSI0001", new Date());
        maria = new Aluno("Maria Souza", "555.666.777-88", "maria@ufu.br", "2026BSI0002", new Date());
        joana = new Aluno("Joana Silva", "99988877766", "joana@ufu.br", "2026BSI0003", new Date());

        ufu.cadastrarAluno(jose);
        ufu.cadastrarAluno(maria);
        ufu.cadastrarAluno(joana);
    }

    @Test
    @DisplayName("PR59-CT01: Deve buscar aluno por parte do nome ignorando acentos e maiúsculas/minúsculas")
    void deveBuscarPorNomeSemAcentoECase() {
        List<Aluno> resAcento = ufu.buscarAluno("jose");
        assertEquals(1, resAcento.size());
        assertEquals(jose, resAcento.get(0));

        List<Aluno> resParcial = ufu.buscarAluno("JO");
        assertEquals(2, resParcial.size());
        assertTrue(resParcial.contains(jose));
        assertTrue(resParcial.contains(joana));
    }

    @Test
    @DisplayName("PR59-CT02: Deve buscar aluno por CPF com ou sem pontuação e ignorar termos numéricos curtos")
    void deveBuscarPorCpfComOuSemPontuacao() {
        List<Aluno> comPontos = ufu.buscarAluno("555.666.777-88");
        assertEquals(1, comPontos.size());
        assertEquals(maria, comPontos.get(0));

        List<Aluno> semPontos = ufu.buscarAluno("55566677788");
        assertEquals(1, semPontos.size());
        assertEquals(maria, semPontos.get(0));

        List<Aluno> comPontosParaCpfLimpo = ufu.buscarAluno("999.888.777-66");
        assertEquals(1, comPontosParaCpfLimpo.size());
        assertEquals(joana, comPontosParaCpfLimpo.get(0));

        // Termo numérico com menos de 11 dígitos não deve gerar falso positivo de CPF
        assertTrue(ufu.buscarAluno("555").isEmpty());
    }

    @Test
    @DisplayName("PR59-CT03: Deve buscar aluno por matrícula exata via buscarAluno e retornar lista vazia para termo inválido")
    void deveBuscarPorMatriculaETermoInvalido() {
        List<Aluno> porMatricula = ufu.buscarAluno("2026bsi0001");
        assertEquals(1, porMatricula.size());
        assertEquals(jose, porMatricula.get(0));

        assertTrue(ufu.buscarAluno("inexistente").isEmpty());
        assertTrue(ufu.buscarAluno("").isEmpty());
        assertTrue(ufu.buscarAluno("   ").isEmpty());
        assertTrue(ufu.buscarAluno(null).isEmpty());
    }

    @Test
    @DisplayName("PR59-CT04: Deve retornar Aluno em buscarPorMatricula(String) ou lançar AlunoNaoEncontradoException")
    void deveBuscarPorMatriculaExataOuLancarExcecao() throws AlunoNaoEncontradoException {
        Aluno encontrado = ufu.buscarPorMatricula("2026BSI0002");
        assertEquals(maria, encontrado);

        AlunoNaoEncontradoException ex = assertThrows(
                AlunoNaoEncontradoException.class,
                () -> ufu.buscarPorMatricula("2026BSI9999")
        );
        assertEquals("2026BSI9999", ex.getIdentificador());
    }
}