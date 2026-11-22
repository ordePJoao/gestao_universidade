package br.edu.universidade.view;

/**
 * Dados fictícios (mock) usados apenas no esqueleto das telas (Sprint 1).
 * Na Sprint 2 estas listas serão substituídas pelos dados vindos do núcleo
 * (pacotes model/service), e esta classe pode ser apagada.
 */
public final class DadosFalsos {

    private DadosFalsos() {
    }

    public static final String[] DEPARTAMENTOS = {
            "FACOM - Faculdade de Computação",
            "FAMAT - Faculdade de Matemática",
            "FAGEN - Faculdade de Gestão e Negócios"
    };

    public static final String[] PROFESSORES = {
            "Ana Souza", "Carlos Lima", "Marina Rocha", "Paulo Mendes"
    };

    public static final String[] ALUNOS = {
            "João Pereira", "Maria Oliveira", "Lucas Martins", "Beatriz Costa", "Pedro Alves"
    };

    public static final String[] SEMESTRES = {"2026/1", "2026/2", "2027/1"};

    public static final String[] TIPOS_AVALIACAO = {"Prova", "Trabalho", "Seminário", "Lista"};

    // Colunas: Código, Nome, Carga horária (h)
    public static final Object[][] CURSOS = {
            {"BSI", "Sistemas de Informação", 3000},
            {"BCC", "Ciência da Computação", 3200},
            {"MAT", "Matemática", 2800}
    };

    // Colunas: Código, Nome, Carga horária (h), Departamento
    public static final Object[][] DISCIPLINAS = {
            {"POO", "Programação Orientada a Objetos", 60, DEPARTAMENTOS[0]},
            {"ED2", "Estrutura de Dados II", 60, DEPARTAMENTOS[0]},
            {"BD1", "Banco de Dados 1", 60, DEPARTAMENTOS[0]},
            {"CAL1", "Cálculo 1", 90, DEPARTAMENTOS[1]}
    };

    // Colunas: Código, Disciplina, Semestre, Professor, Vagas, Ocupadas
    public static final Object[][] TURMAS = {
            {"POO-A", "POO", "2026/2", "Ana Souza", 40, 3},
            {"ED2-A", "ED2", "2026/2", "Carlos Lima", 35, 2},
            {"BD1-B", "BD1", "2026/2", "Marina Rocha", 2, 2}
    };

    // Colunas: Aluno, Turma, Data, Status
    public static final Object[][] MATRICULAS = {
            {"João Pereira", "POO-A", "10/08/2026", "ATIVA"},
            {"Maria Oliveira", "POO-A", "10/08/2026", "ATIVA"},
            {"Lucas Martins", "ED2-A", "11/08/2026", "ATIVA"},
            {"Beatriz Costa", "BD1-B", "12/08/2026", "CANCELADA"}
    };

    // Colunas: Tipo, Nota  (avaliações da primeira matrícula, só de exemplo)
    public static final Object[][] AVALIACOES = {
            {"Prova", 8.0},
            {"Trabalho", 6.5}
    };

    public static String[] codigosDisciplinas() {
        String[] codigos = new String[DISCIPLINAS.length];
        for (int i = 0; i < DISCIPLINAS.length; i++) {
            codigos[i] = (String) DISCIPLINAS[i][0];
        }
        return codigos;
    }

    public static String[] codigosTurmas() {
        String[] codigos = new String[TURMAS.length];
        for (int i = 0; i < TURMAS.length; i++) {
            codigos[i] = (String) TURMAS[i][0];
        }
        return codigos;
    }

    /** Texto "Aluno - Turma" de cada matrícula, para os combos. */
    public static String[] descricoesMatriculas() {
        String[] descricoes = new String[MATRICULAS.length];
        for (int i = 0; i < MATRICULAS.length; i++) {
            descricoes[i] = MATRICULAS[i][0] + " - " + MATRICULAS[i][1];
        }
        return descricoes;
    }
}
