package br.edu.universidade;

import br.edu.universidade.exception.MatriculaDuplicadaException;
import br.edu.universidade.exception.NotaInvalidaException;
import br.edu.universidade.exception.TurmaCheiaException;
import br.edu.universidade.model.Aluno;
import br.edu.universidade.model.Curso;
import br.edu.universidade.model.Disciplina;
import br.edu.universidade.model.Matricula;
import br.edu.universidade.model.Professor;
import br.edu.universidade.model.Turma;
import br.edu.universidade.model.Universidade;
import br.edu.universidade.persistence.RepositorioArquivo;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;

/** Teste de console do fluxo completo do núcleo, antes da interface */
public class TesteConsole {

    private static int falhas = 0;

    public static void main(String[] args) throws Exception {
        System.out.println("=== 1. Fluxo feliz ===");
        Universidade u = new Universidade("Universidade Teste");

        Curso curso = new Curso("BSI", "Sistemas de Informacao", 3000);
        Disciplina disciplina = new Disciplina("POO01", "Programacao Orientada a Objetos", 60);
        curso.adicionarDisciplina(disciplina);
        u.cadastrarCurso(curso);
        u.cadastrarDisciplina(disciplina);

        Professor professor = new Professor("Maria Lima", "52998224725", "maria@uni.br", "SIAPE1", "Doutor");
        u.cadastrarProfessor(professor);

        Aluno joao = new Aluno("Joao Souza", "11144477735", "joao@uni.br", new Date(), curso);
        Aluno ana = new Aluno("Ana Costa", "39053344705", "ana@uni.br", new Date(), curso);
        u.cadastrarAluno(joao);
        u.cadastrarAluno(ana);
        verificar("aluno recebeu matricula gerada", joao.getMatricula() != null);

        Turma turma = new Turma("T1", disciplina, "2026/2", professor, 1); // 1 vaga, para testar turma cheia
        u.cadastrarTurma(turma);
        professor.getTurmas().add(turma);

        Matricula m = matricular(joao, turma);
        verificar("aluno matriculado na turma", turma.getVagasOcupadas() == 1);
        verificar("matricula comeca ATIVA", "ATIVA".equals(m.getSituacao()));

        m.adicionarAvaliacao("P1", 8.0);
        m.adicionarAvaliacao("P2", 6.0);
        verificar("media das notas = 7.0", m.calcularMedia() == 7.0);

        System.out.println("\n=== 2. Excecoes nos casos invalidos ===");
        try {
            matricular(joao, turma);
            verificar("matricula duplicada dispara excecao", false);
        } catch (MatriculaDuplicadaException e) {
            verificar("matricula duplicada dispara excecao", true);
        }
        try {
            matricular(ana, turma);
            verificar("turma cheia dispara excecao", false);
        } catch (TurmaCheiaException e) {
            verificar("turma cheia dispara excecao", true);
        }
        try {
            m.adicionarAvaliacao("P3", 11);
            verificar("nota invalida dispara excecao", false);
        } catch (NotaInvalidaException e) {
            verificar("nota invalida dispara excecao", true);
        }
        verificar("nota invalida nao entrou na lista", m.getAvaliacoes().size() == 2);

        System.out.println("\n=== 3. Salvar e recarregar ===");
        Path arquivo = Files.createTempFile("universidade-teste", ".dat");
        RepositorioArquivo repositorio = new RepositorioArquivo();
        repositorio.salvar(u, arquivo.toString());
        Universidade carregada = repositorio.carregar(arquivo.toString());
        Files.deleteIfExists(arquivo);

        verificar("alunos preservados", carregada.getAlunos().size() == 2);
        verificar("professores preservados", carregada.getProfessores().size() == 1);
        verificar("cursos preservados", carregada.getCursos().size() == 1);
        verificar("turmas preservadas", carregada.getTurmas().size() == 1);
        Turma turmaCarregada = carregada.getTurmas().get(0);
        verificar("matricula preservada na turma", turmaCarregada.getMatriculas().size() == 1);
        verificar("notas preservadas (media 7.0)",
                turmaCarregada.getMatriculas().get(0).calcularMedia() == 7.0);

        System.out.println("\n" + (falhas == 0 ? "TUDO OK" : falhas + " VERIFICACAO(OES) FALHARAM"));
        if (falhas > 0) {
            System.exit(1);
        }
    }

    /**
     * Matricula o aluno aplicando as regras de negocio. O model ainda nao lanca estas excecoes
     * (Turma.adicionarMatricula so devolve true/false), entao o teste as aplica aqui.
     */
    private static Matricula matricular(Aluno aluno, Turma turma)
            throws MatriculaDuplicadaException, TurmaCheiaException {
        Matricula nova = new Matricula(aluno, turma);
        if (turma.getMatriculas().contains(nova)) {
            throw new MatriculaDuplicadaException(aluno.getMatricula(), turma.getCodigo());
        }
        if (!turma.temVaga()) {
            throw new TurmaCheiaException(turma.getCodigo(), turma.getVagas());
        }
        turma.adicionarMatricula(nova);
        aluno.getMatriculas().add(nova);
        return nova;
    }

    private static void verificar(String descricao, boolean ok) {
        System.out.println((ok ? "[OK]    " : "[FALHA] ") + descricao);
        if (!ok) {
            falhas++;
        }
    }
}