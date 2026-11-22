package br.edu.universidade;

import br.edu.universidade.exception.NotaInvalidaException;
import br.edu.universidade.model.Aluno;
import br.edu.universidade.model.Avaliacao;
import br.edu.universidade.model.Pessoa;
import br.edu.universidade.model.Professor;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Suíte de Testes do Domínio - Gestão Universitária")
class DominioTest {

    @Test
    @DisplayName("CT01 - Deve aplicar polimorfismo em getIdent() para Aluno e Professor")
    void deveTestarPolimorfismoPessoaAlunoProfessor() {
        Pessoa aluno = new Aluno("Ana Silva", "111.222.333-44", "ana@ufu.br", "20261BCC01", new Date());
        Pessoa prof = new Professor("Dr. Carlos", "999.888.777-66", "carlos@ufu.br", "1234567", "Doutor");

        assertEquals("20261BCC01", aluno.getIdent(), "O identificador do aluno deve ser a matrícula");
        assertEquals("1234567", prof.getIdent(), "O identificador do professor deve ser o SIAPE");
        assertDoesNotThrow(() -> {
            aluno.exibirDados();
            prof.exibirDados();
        });
    }

    @Test
    @DisplayName("CT02 - Deve aceitar notas entre 0 e 10 e lançar NotaInvalidaException fora do intervalo")
    void deveValidarAvaliacaoENotaInvalida() throws NotaInvalidaException {
        Avaliacao p1 = new Avaliacao("P1", 8.5);
        assertEquals("P1", p1.getTipo());
        assertEquals(8.5, p1.getNota());

        p1.alterarNota(9.0);
        assertEquals(9.0, p1.getNota());

        assertThrows(NotaInvalidaException.class, () -> new Avaliacao("P2", -1.0));
        assertThrows(NotaInvalidaException.class, () -> new Avaliacao("P2", 10.5));
        assertThrows(NotaInvalidaException.class, () -> p1.alterarNota(11.0));
    }
}