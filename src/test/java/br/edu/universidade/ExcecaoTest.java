package br.edu.universidade;

import br.edu.universidade.exception.AlunoNaoEncontradoException;
import br.edu.universidade.exception.CpfInvalidoException;
import br.edu.universidade.exception.MatriculaDuplicadaException;
import br.edu.universidade.exception.NotaInvalidaException;
import br.edu.universidade.exception.TurmaCheiaException;
import br.edu.universidade.exception.UniversidadeException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Suíte de Testes de Exceções Customizadas")
class ExcecaoTest {

    @Test
    @DisplayName("CT-EXC-01: Todas as exceções do domínio devem estender UniversidadeException")
    void todasExcecoesDevemEstenderUniversidadeException() {
        assertTrue(UniversidadeException.class.isAssignableFrom(NotaInvalidaException.class));
        assertTrue(UniversidadeException.class.isAssignableFrom(MatriculaDuplicadaException.class));
        assertTrue(UniversidadeException.class.isAssignableFrom(TurmaCheiaException.class));
        assertTrue(UniversidadeException.class.isAssignableFrom(AlunoNaoEncontradoException.class));
        assertTrue(UniversidadeException.class.isAssignableFrom(CpfInvalidoException.class));
    }

    @Test
    @DisplayName("CT-EXC-02: MatriculaDuplicadaException deve conter mensagem clara e dados do erro")
    void deveValidarMatriculaDuplicadaException() {
        MatriculaDuplicadaException ex = new MatriculaDuplicadaException("20261BSI01", "POO-2026-1");
        assertEquals("20261BSI01", ex.getIdentificadorAluno());
        assertEquals("POO-2026-1", ex.getCodigoTurma());
        assertTrue(ex.getMessage().contains("20261BSI01"));
        assertTrue(ex.getMessage().contains("POO-2026-1"));
    }

    @Test
    @DisplayName("CT-EXC-03: TurmaCheiaException deve conter mensagem clara e dados do erro")
    void deveValidarTurmaCheiaException() {
        TurmaCheiaException ex = new TurmaCheiaException("POO-2026-1", 40);
        assertEquals("POO-2026-1", ex.getCodigoTurma());
        assertEquals(40, ex.getVagasTotais());
        assertTrue(ex.getMessage().contains("POO-2026-1"));
        assertTrue(ex.getMessage().contains("40"));
    }

    @Test
    @DisplayName("CT-EXC-04: AlunoNaoEncontradoException deve conter mensagem clara e identificador")
    void deveValidarAlunoNaoEncontradoException() {
        AlunoNaoEncontradoException ex = new AlunoNaoEncontradoException("20261BSI99");
        assertEquals("20261BSI99", ex.getIdentificador());
        assertTrue(ex.getMessage().contains("20261BSI99"));
    }

    @Test
    @DisplayName("CT-EXC-05: CpfInvalidoException deve conter mensagem clara e CPF informado")
    void deveValidarCpfInvalidoException() {
        CpfInvalidoException ex = new CpfInvalidoException("123.456");
        assertEquals("123.456", ex.getCpfInformado());
        assertTrue(ex.getMessage().contains("123.456"));
    }
}
