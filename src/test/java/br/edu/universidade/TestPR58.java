// Teste PR#58: #11.1 Adicionar Matrícula acadêmica do aluno (código gerado) _ (Issue #30)
package br.edu.universidade;

import br.edu.universidade.model.Aluno;
import br.edu.universidade.model.Curso;
import br.edu.universidade.model.Universidade;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Year;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes PR #58 (Issue #30 / #11.1) - Geração de Código de Matrícula do Aluno")
class TestPR58 {

    @Test
    @DisplayName("PR58-CT01: Deve gerar código no formato ANO + CODIGO_CURSO + 4 dígitos e vincular ao Curso")
    void deveGerarCodigoMatriculaAoCadastrarAlunoComCurso() {
        Universidade ufu = new Universidade("UFU");
        Curso bsi = new Curso("BSI", "Sistemas de Informação", 3000);
        Aluno aluno = new Aluno("Maria Clara", "111.222.333-44", "maria@ufu.br", new Date(), bsi);

        assertNull(aluno.getCodigoMatricula());
        assertEquals(bsi, aluno.getCurso());

        boolean cadastrado = ufu.cadastrarAluno(aluno);

        assertTrue(cadastrado);
        assertEquals(1, ufu.getAlunos().size());

        String codigo = aluno.getCodigoMatricula();
        assertNotNull(codigo);
        assertEquals(codigo, aluno.getMatricula());
        assertEquals(codigo, aluno.getIdent());

        String prefixoEsperado = Year.now().getValue() + "BSI";
        assertTrue(codigo.startsWith(prefixoEsperado), "Código deve iniciar com " + prefixoEsperado);
        assertEquals(prefixoEsperado.length() + 4, codigo.length(), "Código deve ter 4 dígitos numéricos ao final");
        assertTrue(codigo.substring(prefixoEsperado.length()).matches("\\d{4}"));
    }

    @Test
    @DisplayName("PR58-CT02: Deve gerar códigos distintos e nunca repetir para múltiplos alunos no mesmo curso")
    void deveGerarCodigosUnicosParaMultiplosAlunos() {
        Universidade ufu = new Universidade("UFU");
        Curso bcc = new Curso("BCC", "Ciência da Computação", 3200);
        Set<String> codigosGerados = new HashSet<>();

        for (int i = 0; i < 30; i++) {
            Aluno aluno = new Aluno("Aluno " + i, "000.000.000-" + String.format("%02d", i),
                    "aluno" + i + "@ufu.br", new Date(), bcc);
            assertTrue(ufu.cadastrarAluno(aluno));
            assertTrue(codigosGerados.add(aluno.getCodigoMatricula()), "Código de matrícula não pode repetir");
        }

        assertEquals(30, ufu.getAlunos().size());
        assertEquals(30, codigosGerados.size());
    }

    @Test
    @DisplayName("PR58-CT03: Deve preservar código pré-definido quando informado no construtor antigo")
    void devePreservarCodigoPreDefinido() {
        Universidade ufu = new Universidade("UFU");
        Aluno aluno = new Aluno("João Pedro", "555.666.777-88", "jp@ufu.br", "20261BCC99", new Date());

        assertTrue(ufu.cadastrarAluno(aluno));
        assertEquals("20261BCC99", aluno.getCodigoMatricula());
        assertEquals("20261BCC99", aluno.getMatricula());
        assertTrue(ufu.buscarAlunoPorMatricula("20261BCC99").isPresent());
    }

    @Test
    @DisplayName("PR58-CT04: Deve lançar IllegalArgumentException ao chamar gerarMatricula(null) e ignorar aluno nulo")
    void deveValidarParametrosNulos() {
        Universidade ufu = new Universidade("UFU");

        assertThrows(IllegalArgumentException.class, () -> ufu.gerarMatricula(null));
        assertFalse(ufu.cadastrarAluno(null));
    }
}