// Teste PR#56: #5 Implementa classe Departamento

package br.edu.universidade;

import br.edu.universidade.model.Departamento;
import br.edu.universidade.model.Professor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes PR #56 (Issue #7) - Departamento")
class DepartamentoTest {

    @Test
    @DisplayName("PR56-CT01: Deve inicializar Departamento com nome e lista vazia de professores")
    void deveInicializarDepartamento() {
        Departamento dep = new Departamento("FACOM - Faculdade de Computação");

        assertEquals("FACOM - Faculdade de Computação", dep.getNome());
        assertNotNull(dep.getProfessores());
        assertTrue(dep.getProfessores().isEmpty());

        dep.setNome("Faculdade de Computação - UFU");
        assertEquals("Faculdade de Computação - UFU", dep.getNome());
    }

    @Test
    @DisplayName("PR56-CT02: Deve adicionar professores distintos e ignorar professor duplicado")
    void deveAdicionarProfessorSemDuplicar() {
        Departamento dep = new Departamento("FACOM");
        Professor p1 = new Professor("Dr. Carlos", "111.222.333-44", "carlos@ufu.br", "1234567", "Doutor");
        Professor p2 = new Professor("Dra. Mariana", "555.666.777-88", "mariana@ufu.br", "7654321", "Doutora");

        dep.adicionarProfessor(p1);
        dep.adicionarProfessor(p2);
        dep.adicionarProfessor(p1); // Tentativa duplicada da mesma instância

        assertEquals(2, dep.getProfessores().size());
        assertTrue(dep.getProfessores().contains(p1));
        assertTrue(dep.getProfessores().contains(p2));
    }

    @Test
    @DisplayName("PR56-CT03: Deve permitir existência de Professor independente do Departamento (agregação)")
    void devePermitirProfessorSemDepartamento() {
        Professor profSemDep = new Professor("Dr. Roberto", "999.888.777-66", "roberto@ufu.br", "1122334", "Mestre");

        assertNotNull(profSemDep);
        assertEquals("1122334", profSemDep.getIdent());
    }

    @Test
    @DisplayName("PR56-CT04: Deve serializar e desserializar Departamento com professores")
    void deveSerializarEDesserializarDepartamento() throws Exception {
        Departamento original = new Departamento("FACOM");
        Professor p1 = new Professor("Dr. Carlos", "111.222.333-44", "carlos@ufu.br", "1234567", "Doutor");
        original.adicionarProfessor(p1);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(original);
        }

        Departamento restaurado;
        try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()))) {
            restaurado = (Departamento) ois.readObject();
        }

        assertEquals("FACOM", restaurado.getNome());
        assertEquals(1, restaurado.getProfessores().size());
        assertEquals("Dr. Carlos", restaurado.getProfessores().get(0).getNome());
    }
}