package br.edu.universidade;

import br.edu.universidade.exception.NotaInvalidaException;
import br.edu.universidade.model.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes PR #51 - Matricula (Cobertura Completa)")
class MatriculaLeticiaTest {

    private Turma criarTurmaBase(String codigo) {
        Disciplina disc = new Disciplina("FACOM31102", "Programação Orientada a Objetos", 60);
        Professor prof = new Professor("Dr. Carlos", "999.888.777-66", "carlos@ufu.br", "1234567", "Doutor");
        return new Turma(codigo, disc, "2026/2", prof, 30);
    }

    @Test
    @DisplayName("PR51-CT01 - Deve iniciar com status ATIVA, data preenchida e média 0.0")
    void deveInicializarMatriculaCorretamente() {
        Turma turma = criarTurmaBase("T01");
        Aluno aluno = new Aluno("Ana Silva", "111.222.333-44", "ana@ufu.br", "20261BCC01", new Date());

        Matricula mat = new Matricula(aluno, turma);

        assertEquals(aluno, mat.getAluno());
        assertEquals(turma, mat.getTurma());
        assertEquals(StatusMatricula.ATIVA, mat.getStatus());
        assertEquals("ATIVA", mat.getSituacao());
        assertNotNull(mat.getData());
        assertEquals(0.0, mat.calcularMedia());
        assertTrue(mat.getAvaliacoes().isEmpty());
    }

    @Test
    @DisplayName("PR51-CT02 - Deve impedir criação de Matricula com Aluno ou Turma nulos")
    void deveRejeitarParametrosNulosNoConstrutor() {
        Turma turma = criarTurmaBase("T01");
        Aluno aluno = new Aluno("Ana Silva", "111.222.333-44", "ana@ufu.br", "20261BCC01", new Date());

        assertThrows(NullPointerException.class, () -> new Matricula(null, turma));
        assertThrows(NullPointerException.class, () -> new Matricula(aluno, null));
    }

    @Test
    @DisplayName("PR51-CT03 - Deve aceitar notas nos limites exatos (0.0 e 10.0) e calcular média")
    void deveAceitarNotasNosLimitesECalcularMedia() throws NotaInvalidaException {
        Turma turma = criarTurmaBase("T01");
        Aluno aluno = new Aluno("Ana Silva", "111.222.333-44", "ana@ufu.br", "20261BCC01", new Date());
        Matricula mat = new Matricula(aluno, turma);

        mat.adicionarAvaliacao("P1", 0.0);
        mat.adicionarAvaliacao("P2", 10.0);

        assertEquals(2, mat.getAvaliacoes().size());
        assertEquals(5.0, mat.calcularMedia(), 0.0001);
    }

    @Test
    @DisplayName("PR51-CT04 - Deve rejeitar nota inválida (< 0 ou > 10) e tipo nulo")
    void deveRejeitarAvaliacaoInvalida() {
        Turma turma = criarTurmaBase("T01");
        Aluno aluno = new Aluno("Ana Silva", "111.222.333-44", "ana@ufu.br", "20261BCC01", new Date());
        Matricula mat = new Matricula(aluno, turma);

        assertThrows(NotaInvalidaException.class, () -> mat.adicionarAvaliacao("P1", -0.1));
        assertThrows(NotaInvalidaException.class, () -> mat.adicionarAvaliacao("P1", 10.1));
        assertThrows(NullPointerException.class, () -> mat.adicionarAvaliacao(null, 8.0));
        assertTrue(mat.getAvaliacoes().isEmpty());
    }

    @Test
    @DisplayName("PR51-CT05 - Deve blindar a lista de avaliações contra modificação externa")
    void deveRetornarListaNaoModificavel() throws NotaInvalidaException {
        Turma turma = criarTurmaBase("T01");
        Aluno aluno = new Aluno("Ana Silva", "111.222.333-44", "ana@ufu.br", "20261BCC01", new Date());
        Matricula mat = new Matricula(aluno, turma);
        mat.adicionarAvaliacao("P1", 7.5);

        assertThrows(UnsupportedOperationException.class, () -> mat.getAvaliacoes().clear());
        assertThrows(UnsupportedOperationException.class, () -> mat.getAvaliacoes().remove(0));
    }

    @Test
    @DisplayName("PR51-CT06 - Deve alterar status para CANCELADA ao chamar cancelar()")
    void deveCancelarMatricula() {
        Turma turma = criarTurmaBase("T01");
        Aluno aluno = new Aluno("Ana Silva", "111.222.333-44", "ana@ufu.br", "20261BCC01", new Date());
        Matricula mat = new Matricula(aluno, turma);

        mat.cancelar();

        assertEquals(StatusMatricula.CANCELADA, mat.getStatus());
        assertEquals("CANCELADA", mat.getSituacao());
    }

    @Test
    @DisplayName("PR51-CT07 - Deve comparar igualdade corretamente por Aluno e Turma")
    void deveTestarEqualsEHashCodeCompleto() {
        Turma turma1 = criarTurmaBase("T01");
        Turma turma2 = criarTurmaBase("T02");
        Aluno a1 = new Aluno("Ana Silva", "111.222.333-44", "ana@ufu.br", "20261BCC01", new Date());
        Aluno a2 = new Aluno("Bruno Costa", "555.666.777-88", "bruno@ufu.br", "20261BCC02", new Date());

        Matricula m1 = new Matricula(a1, turma1);
        Matricula m1Copia = new Matricula(a1, turma1);
        Matricula mOutroAluno = new Matricula(a2, turma1);
        Matricula mOutraTurma = new Matricula(a1, turma2);

        assertEquals(m1, m1);
        assertEquals(m1, m1Copia);
        assertEquals(m1.hashCode(), m1Copia.hashCode());
        assertNotEquals(m1, mOutroAluno);
        assertNotEquals(m1, mOutraTurma);
        assertNotEquals(m1, null);
    }

    @Test
    @DisplayName("PR51-CT08 - Deve serializar e desserializar Matricula mantendo dados e avaliações")
    void deveSerializarEDesserializarMatricula() throws Exception {
        Turma turma = criarTurmaBase("T01");
        Aluno aluno = new Aluno("Ana Silva", "111.222.333-44", "ana@ufu.br", "20261BCC01", new Date());
        Matricula original = new Matricula(aluno, turma);
        original.adicionarAvaliacao("P1", 8.5);
        original.adicionarAvaliacao("Trabalho", 9.5);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(original);
        }

        Matricula restaurada;
        try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()))) {
            restaurada = (Matricula) ois.readObject();
        }

        assertNotNull(restaurada);
        assertEquals(original.getAluno().getIdent(), restaurada.getAluno().getIdent());
        assertEquals(original.getData(), restaurada.getData());
        assertEquals(9.0, restaurada.calcularMedia(), 0.0001);
        assertEquals(2, restaurada.getAvaliacoes().size());
        assertEquals(StatusMatricula.APROVADA, restaurada.getStatus());
    }
}