package br.edu.universidade;

import br.edu.universidade.exception.AlunoNaoEncontradoException;
import br.edu.universidade.model.Aluno;
import br.edu.universidade.model.Curso;
import br.edu.universidade.model.Universidade;

import java.util.Date;

/** Teste de console da busca de alunos (task #31). */
public class MainConsole {
    public static void main(String[] args) {
        Universidade u = new Universidade("Teste");
        Curso bsi = new Curso("BSI", "Sistemas de Informacao", 3000);
        u.cadastrarCurso(bsi);

        u.cadastrarAluno(new Aluno("José Álvares", "11122233344", "jose@uni.br", new Date(), bsi));
        u.cadastrarAluno(new Aluno("Maria Souza", "55566677788", "maria@uni.br", new Date(), bsi));
        u.cadastrarAluno(new Aluno("Joana Silva", "99988877766", "joana@uni.br", new Date(), bsi));

        System.out.println("Nome 'jose' (esperado 1): " + u.buscarAluno("jose").size());
        System.out.println("Nome 'JO' (esperado 2): " + u.buscarAluno("JO").size());
        System.out.println("CPF com pontos (esperado 1): " + u.buscarAluno("555.666.777-88").size());
        System.out.println("CPF sem pontos (esperado 1): " + u.buscarAluno("55566677788").size());
        System.out.println("Sem resultado (esperado 0): " + u.buscarAluno("xyz").size());

        String codigo = u.getAlunos().get(0).getMatricula();
        System.out.println("Busca por codigo (esperado 1): " + u.buscarAluno(codigo).size());

        try {
            System.out.println("Encontrou: " + u.buscarPorMatricula(codigo).getNome());
            u.buscarPorMatricula("INEXISTENTE");
            System.out.println("ERRO: nao lancou excecao");
        } catch (AlunoNaoEncontradoException e) {
            System.out.println("OK, lancou: " + e.getMessage());
        }
    }
}