// Teste PR#57: #8 Atualiza classe Universidade com requisitos e departamentos
package br.edu.universidade;

import br.edu.universidade.model.Departamento;
import br.edu.universidade.model.Professor;
import br.edu.universidade.model.Universidade;
import br.edu.universidade.persistence.RepositorioArquivo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes PR #57 (Issue #8) - Integração de Departamentos em Universidade")
class UniversidadeDepartamentoTest {

    @Test
    @DisplayName("PR57-CT01: Deve adicionar, listar (não modificável) e remover Departamentos em Universidade")
    void deveGerenciarDepartamentosNaUniversidade() {
        Universidade ufu = new Universidade("UFU");
        Departamento facom = new Departamento("FACOM");
        Departamento femec = new Departamento("FEMEC");

        assertTrue(ufu.getDepartamentos().isEmpty());
        assertTrue(ufu.adicionarDepartamento(facom));
        assertTrue(ufu.adicionarDepartamento(femec));

        assertFalse(ufu.adicionarDepartamento(null));
        assertFalse(ufu.adicionarDepartamento(facom));
        assertEquals(2, ufu.getDepartamentos().size());

        assertThrows(UnsupportedOperationException.class, () -> ufu.getDepartamentos().clear());

        assertFalse(ufu.removerDepartamento(null));
        assertTrue(ufu.removerDepartamento(femec));
        assertEquals(1, ufu.getDepartamentos().size());
        assertEquals("FACOM", ufu.getDepartamentos().get(0).getNome());
    }

    @Test
    @DisplayName("PR57-CT02: Deve persistir Departamentos e seus Professores em cascata via RepositorioArquivo")
    void devePersistirDepartamentosEmCascata() throws Exception {
        String caminhoTeste = "data/teste_uni_departamentos.dat";
        File arquivo = new File(caminhoTeste);
        try {
            Universidade uOriginal = new Universidade("UFU");
            Departamento facom = new Departamento("FACOM");
            Professor prof = new Professor("Dr. Carlos", "111.222.333-44", "carlos@ufu.br", "1234567", "Doutor");
            facom.adicionarProfessor(prof);
            uOriginal.adicionarDepartamento(facom);

            RepositorioArquivo repo = new RepositorioArquivo();
            repo.salvar(uOriginal, caminhoTeste);

            Universidade uCarregada = repo.carregar(caminhoTeste);
            assertEquals(1, uCarregada.getDepartamentos().size());
            assertEquals("FACOM", uCarregada.getDepartamentos().get(0).getNome());
            assertEquals(1, uCarregada.getDepartamentos().get(0).getProfessores().size());
            assertEquals("Dr. Carlos", uCarregada.getDepartamentos().get(0).getProfessores().get(0).getNome());
        } finally {
            if (arquivo.exists()) {
                arquivo.delete();
            }
        }
    }
}