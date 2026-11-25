package br.edu.universidade;

import br.edu.universidade.exception.NotaInvalidaException;
import br.edu.universidade.model.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes PR #62 (Issue #54 / #10.1) - Consolidação da Situação da Matrícula")
public class TestPR62 {

    private Aluno criarAlunoPadrao() {
        return new Aluno("Joao Silva", "12345678909", "joao@ufu.br", "2026CC001", new Date());
    }

    private Turma criarTurmaPadrao() {
        Disciplina disc = new Disciplina("POO", "Programacao Orientada a Objetos", 60);
        Professor prof = new Professor("Profa. Ana", "98765432100", "ana@ufu.br", "123456", "Doutora");
        return new Turma("T01", disc, "2026/2", prof, 30);
    }

    @Test
    @DisplayName("Deve iniciar como ATIVA e média 0.0 sem avaliações")
    void deveIniciarAtivaSemNotas() {
        Matricula m = new Matricula(criarAlunoPadrao(), criarTurmaPadrao());

        assertEquals(6.0, Matricula.MEDIA_APROVACAO);
        assertEquals(StatusMatricula.ATIVA, m.getStatus());
        assertEquals("ATIVA", m.getSituacao());
        assertEquals(0.0, m.calcularMedia());
    }

    @Test
    @DisplayName("Deve aprovar com média exata 6.0 e acima de 6.0")
    void deveAprovarComMediaMaiorOuIgualA6() throws Exception {
        Matricula m = new Matricula(criarAlunoPadrao(), criarTurmaPadrao());
        m.adicionarAvaliacao("P1", 6.0);

        assertEquals(6.0, m.calcularMedia());
        assertEquals(StatusMatricula.APROVADA, m.getStatus());
        assertEquals("APROVADA", m.getSituacao());
    }

    @Test
    @DisplayName("Deve reprovar com média abaixo de 6.0")
    void deveReprovarComMediaAbaixoDe6() throws Exception {
        Matricula m = new Matricula(criarAlunoPadrao(), criarTurmaPadrao());
        m.adicionarAvaliacao("P1", 5.9);

        assertEquals(5.9, m.calcularMedia());
        assertEquals(StatusMatricula.REPROVADA, m.getStatus());
        assertEquals("REPROVADA", m.getSituacao());
    }

    @Test
    @DisplayName("Deve recalcular situação ao alterar nota existente")
    void deveRecalcularSituacaoAoAlterarNota() throws Exception {
        Matricula m = new Matricula(criarAlunoPadrao(), criarTurmaPadrao());
        m.adicionarAvaliacao("P1", 8.0);
        m.adicionarAvaliacao("P2", 6.0);
        assertEquals(StatusMatricula.APROVADA, m.getStatus());

        m.getAvaliacoes().get(0).alterarNota(2.0);
        assertEquals(4.0, m.calcularMedia());
        assertEquals(StatusMatricula.REPROVADA, m.getStatus());
    }

    @Test
    @DisplayName("Deve sincronizar cancelamento em Aluno.cancelarMatricula")
    void deveSincronizarCancelamentoComAluno() throws Exception {
        Aluno aluno = criarAlunoPadrao();
        Turma turma = criarTurmaPadrao();
        Matricula m = new Matricula(aluno, turma);

        turma.adicionarMatricula(m);
        aluno.getMatriculas().add(m);
        m.adicionarAvaliacao("P1", 9.0);

        aluno.cancelarMatricula(m);

        assertEquals(StatusMatricula.CANCELADA, m.getStatus());
        assertFalse(aluno.getMatriculas().contains(m));
        assertDoesNotThrow(() -> aluno.cancelarMatricula(null));
    }

    @Test
    @DisplayName("Não deve sobrescrever status CANCELADA e deve bloquear novas avaliações")
    void deveProtegerStatusCancelada() throws Exception {
        Matricula m = new Matricula(criarAlunoPadrao(), criarTurmaPadrao());
        m.adicionarAvaliacao("P1", 10.0);
        m.cancelar();

        m.consolidarSituacao();
        assertEquals(StatusMatricula.CANCELADA, m.getStatus());
        assertEquals("CANCELADA", m.getSituacao());
        assertThrows(IllegalStateException.class, () -> m.adicionarAvaliacao("P2", 8.0));
    }

    @Test
    @DisplayName("Deve validar notas fora do intervalo [0, 10] e contrato Exibivel")
    void deveValidarNotasEExibivel() {
        Aluno aluno = criarAlunoPadrao();
        Curso curso = new Curso("CC", "Ciencia da Computacao", 3200);
        Disciplina disc = new Disciplina("POO", "Programacao Orientada a Objetos", 60);
        Turma turma = criarTurmaPadrao();
        Matricula m = new Matricula(aluno, turma);

        assertThrows(NotaInvalidaException.class, () -> m.adicionarAvaliacao("P1", 10.5));
        assertThrows(NotaInvalidaException.class, () -> m.adicionarAvaliacao("P1", -0.5));
        assertEquals(StatusMatricula.ATIVA, m.getStatus());

        for (Exibivel item : List.of(aluno, curso, disc, turma)) {
            assertNotNull(item.exibirDados());
            assertFalse(item.exibirDados().isBlank());
        }
    }
}