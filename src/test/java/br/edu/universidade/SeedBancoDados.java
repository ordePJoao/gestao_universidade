package br.edu.universidade;

import br.edu.universidade.exception.MatriculaDuplicadaException;
import br.edu.universidade.exception.TurmaCheiaException;
import br.edu.universidade.model.*;
import br.edu.universidade.persistence.RepositorioArquivo;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SeedBancoDados {

        public static void main(String[] args) throws Exception {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                RepositorioArquivo repo = new RepositorioArquivo();

                // Inicia uma base limpa para garantir dados padronizados
                Universidade ufu = new Universidade("Universidade Federal de Uberlândia - UFU");

                // =====================================================================
                // 1. UNIDADES ACADÊMICAS (DEPARTAMENTOS UFU)
                // =====================================================================
                Departamento facom = obterOuCriarDepto(ufu, "FACOM - Faculdade de Computação");
                Departamento famat = obterOuCriarDepto(ufu, "FAMAT - Faculdade de Matemática");
                Departamento feelt = obterOuCriarDepto(ufu, "FEELT - Faculdade de Engenharia Elétrica");
                Departamento femec = obterOuCriarDepto(ufu, "FEMEC - Faculdade de Engenharia Mecânica");
                Departamento fequi = obterOuCriarDepto(ufu, "FEQUI - Faculdade de Engenharia Química");
                Departamento feciv = obterOuCriarDepto(ufu, "FECIV - Faculdade de Engenharia Civil");
                Departamento fagen = obterOuCriarDepto(ufu, "FAGEN - Faculdade de Gestão e Negócios");
                Departamento fadir = obterOuCriarDepto(ufu, "FADIR - Faculdade de Direito");
                Departamento famed = obterOuCriarDepto(ufu, "FAMED - Faculdade de Medicina");
                Departamento infis = obterOuCriarDepto(ufu, "INFIS - Instituto de Física");
                Departamento ieri  = obterOuCriarDepto(ufu, "IERI - Instituto de Economia e Relações Internacionais");

                // =====================================================================
                // 2. PROFESSORES
                // =====================================================================
                Professor pGean     = cadastrarProf(ufu, facom, "Gean Carlos Silveira", "20015364512", "carlosgean@ufu.br", "12457689", "Pós-Doutor");
                Professor pMarcos   = cadastrarProf(ufu, facom, "Marcos Vinícius Mendes", "44455566677", "marcos.mendes@ufu.br", "1984321", "Doutor");
                Professor pRenata   = cadastrarProf(ufu, facom, "Renata Cristina Souza", "44455566688", "renata.souza@ufu.br", "2145698", "Pós-Doutor");
                Professor pThiago   = cadastrarProf(ufu, facom, "Thiago Henrique Almeida", "44455566699", "thiago.almeida@ufu.br", "2458712", "Doutor");
                Professor pCesar    = cadastrarProf(ufu, famat, "Cesár Silva Dornelas", "68475634891", "cesardorsilva@ufu.br", "34285967", "Doutor");
                Professor pJuliaP   = cadastrarProf(ufu, famat, "Júlia Namas Peixoto", "85734456784", "namasjulia@ufu.br", "85767898", "Pós-Doutor");
                Professor pBeatriz  = cadastrarProf(ufu, famat, "Beatriz Martins Tavares", "77788899900", "beatriz.tavares@ufu.br", "2341987", "Doutor");
                Professor pEduardo  = cadastrarProf(ufu, famat, "Eduardo Vasconcelos Lima", "77788899911", "eduardo.lima@ufu.br", "1874523", "Mestre");
                Professor pFernanda = cadastrarProf(ufu, feelt, "Fernanda Almeida Rocha", "55566677788", "fernanda.rocha@ufu.br", "2098745", "Pós-Doutor");
                Professor pGustavo  = cadastrarProf(ufu, femec, "Gustavo Henrique Prado", "55566677799", "gustavo.prado@ufu.br", "1654987", "Doutor");
                Professor pCamila   = cadastrarProf(ufu, fequi, "Camila Nogueira Barros", "33344455566", "camila.barros@ufu.br", "2234876", "Doutor");
                Professor pRicardo  = cadastrarProf(ufu, fagen, "Ricardo Nogueira Borges", "66677788899", "ricardo.borges@ufu.br", "1765432", "Mestre");
                Professor pHelena   = cadastrarProf(ufu, fadir, "Helena Coutinho Ribeiro", "88899900011", "helena.ribeiro@ufu.br", "1543298", "Pós-Doutor");
                Professor pAndre    = cadastrarProf(ufu, famed, "André Luiz Monteiro", "99900011122", "andre.monteiro@ufu.br", "1432876", "Doutor");
                Professor pSabrina  = cadastrarProf(ufu, infis, "Sabrina Oliveira Dias", "11122233345", "sabrina.dias@ufu.br", "2567431", "Doutor");

                // =====================================================================
                // 3. CURSOS E DISCIPLINAS
                // =====================================================================
                Curso bsi = obterOuCriarCurso(ufu, "BSI", "Bacharelado em Sistemas de Informação", 3000);
                Curso bcc = obterOuCriarCurso(ufu, "BCC", "Bacharelado em Ciência da Computação", 3200);
                Curso mat = obterOuCriarCurso(ufu, "MAT", "Bacharelado em Matemática", 2800);
                Curso est = obterOuCriarCurso(ufu, "EST", "Bacharelado em Estatística", 3000);
                Curso ebm = obterOuCriarCurso(ufu, "EBM", "Engenharia Biomédica", 3600);
                Curso eca = obterOuCriarCurso(ufu, "ECA", "Engenharia de Controle e Automação", 3720);
                Curso emc = obterOuCriarCurso(ufu, "EMC", "Engenharia Mecânica", 3840);
                Curso eqm = obterOuCriarCurso(ufu, "EQM", "Engenharia Química", 3660);
                Curso ecv = obterOuCriarCurso(ufu, "ECV", "Engenharia Civil", 3780);
                Curso adm = obterOuCriarCurso(ufu, "ADM", "Administração", 3000);
                Curso dir = obterOuCriarCurso(ufu, "DIR", "Direito", 3700);
                Curso med = obterOuCriarCurso(ufu, "MED", "Medicina", 7200);
                Curso eco = obterOuCriarCurso(ufu, "ECO", "Ciências Econômicas", 3000);

                Disciplina poo        = criarDisciplina(ufu, "FACOM26POO", "Programação Orientada a Objetos", 60);
                Disciplina aed1       = criarDisciplina(ufu, "FACOM31201", "Algoritmos e Estruturas de Dados I", 60);
                Disciplina aed2       = criarDisciplina(ufu, "FACOM31301", "Algoritmos e Estruturas de Dados II", 60);
                Disciplina bd1        = criarDisciplina(ufu, "FACOM31401", "Banco de Dados I", 60);
                Disciplina so         = criarDisciplina(ufu, "FACOM31502", "Sistemas Operacionais", 60);
                Disciplina ia         = criarDisciplina(ufu, "FACOM31701", "Inteligência Artificial", 60);
                Disciplina mdc        = criarDisciplina(ufu, "FAMAT26MDC", "Matemática Discreta", 60);
                Disciplina est1       = criarDisciplina(ufu, "FAMAT26EST1", "Estatística I", 60);
                Disciplina calc1      = criarDisciplina(ufu, "FAMAT31101", "Cálculo Diferencial e Integral I", 90);
                Disciplina calc2      = criarDisciplina(ufu, "FAMAT31201", "Cálculo Diferencial e Integral II", 90);
                Disciplina gaal       = criarDisciplina(ufu, "FAMAT31103", "Geometria Analítica e Álgebra Linear", 60);
                Disciplina circuitos  = criarDisciplina(ufu, "FEELT31204", "Circuitos Elétricos I", 60);
                Disciplina controle   = criarDisciplina(ufu, "FEELT31501", "Sistemas de Controle Linear", 60);
                Disciplina termo      = criarDisciplina(ufu, "FEMEC31302", "Termodinâmica Aplicada", 60);
                Disciplina mecSolidos = criarDisciplina(ufu, "FEMEC31205", "Mecânica dos Sólidos", 60);
                Disciplina fenTransp  = criarDisciplina(ufu, "FEQUI31401", "Fenômenos de Transporte", 60);
                Disciplina fisica1    = criarDisciplina(ufu, "INFIS31101", "Física Geral I", 60);
                Disciplina tga        = criarDisciplina(ufu, "FAGEN31102", "Teoria Geral da Administração", 60);
                Disciplina dirConst   = criarDisciplina(ufu, "FADIR31101", "Direito Constitucional I", 60);
                Disciplina anatomia   = criarDisciplina(ufu, "FAMED31101", "Anatomia Humana Sistêmica", 120);
                Disciplina macro      = criarDisciplina(ufu, "IERI31201", "Macroeconomia I", 60);

                // Vínculos com grades curriculares dos cursos
                bsi.adicionarDisciplina(poo);
                bsi.adicionarDisciplina(aed1);
                bsi.adicionarDisciplina(bd1);

                bcc.adicionarDisciplina(poo);
                bcc.adicionarDisciplina(aed1);
                bcc.adicionarDisciplina(aed2);
                bcc.adicionarDisciplina(bd1);
                bcc.adicionarDisciplina(so);
                bcc.adicionarDisciplina(ia);
                bcc.adicionarDisciplina(calc1);
                bcc.adicionarDisciplina(gaal);

                mat.adicionarDisciplina(mdc);
                mat.adicionarDisciplina(est1);
                mat.adicionarDisciplina(calc1);

                est.adicionarDisciplina(mdc);
                est.adicionarDisciplina(est1);

                ebm.adicionarDisciplina(calc1);
                ebm.adicionarDisciplina(calc2);
                ebm.adicionarDisciplina(circuitos);
                ebm.adicionarDisciplina(fisica1);

                eca.adicionarDisciplina(calc1);
                eca.adicionarDisciplina(circuitos);
                eca.adicionarDisciplina(controle);

                emc.adicionarDisciplina(calc1);
                emc.adicionarDisciplina(fisica1);
                emc.adicionarDisciplina(termo);
                emc.adicionarDisciplina(mecSolidos);

                eqm.adicionarDisciplina(calc1);
                eqm.adicionarDisciplina(fenTransp);

                ecv.adicionarDisciplina(calc1);
                ecv.adicionarDisciplina(gaal);
                ecv.adicionarDisciplina(mecSolidos);

                adm.adicionarDisciplina(tga);
                adm.adicionarDisciplina(macro);
                dir.adicionarDisciplina(dirConst);
                med.adicionarDisciplina(anatomia);
                eco.adicionarDisciplina(macro);
                eco.adicionarDisciplina(calc1);

                // =====================================================================
                // 4. ALUNOS
                // =====================================================================
                Aluno aJoao     = cadastrarAluno(ufu, "João Pedro Cardoso de Oliveira", "11111111111", "joao@ufu.br", sdf.parse("10/08/2026"), bsi);
                Aluno aPaulo    = cadastrarAluno(ufu, "Paulo José Cardoso de Oliveira", "22222222222", "pjoliveira@ufu.br", sdf.parse("10/08/2026"), bsi);
                Aluno aMaryane  = cadastrarAluno(ufu, "Maryane Lima Silva", "33333333333", "marylima@ufu.br", sdf.parse("10/08/2026"), mat);

                Aluno aLucas    = cadastrarAluno(ufu, "Lucas Gabriel Ferreira", "12345678909", "lucas.ferreira@ufu.br", sdf.parse("10/08/2026"), bcc);
                Aluno aCamila   = cadastrarAluno(ufu, "Camila Andrade Rezende", "98765432100", "camila.rezende@ufu.br", sdf.parse("10/08/2026"), bcc);
                Aluno aMateus   = cadastrarAluno(ufu, "Mateus Henrique Vieira", "14725836901", "mateus.vieira@ufu.br", sdf.parse("15/03/2025"), bcc);
                Aluno aSofia    = cadastrarAluno(ufu, "Sofia Castro Nogueira", "25836914702", "sofia.nogueira@ufu.br", sdf.parse("15/03/2025"), bcc);
                Aluno aGabriel  = cadastrarAluno(ufu, "Gabriel Santos Moreira", "36914725803", "gabriel.moreira@ufu.br", sdf.parse("10/08/2026"), bcc);
                Aluno aRafael   = cadastrarAluno(ufu, "Rafael Augusto Costa", "45612378902", "rafael.costa@ufu.br", sdf.parse("12/08/2026"), ebm);
                Aluno aIsabela  = cadastrarAluno(ufu, "Isabela Freitas Martins", "74185296304", "isabela.martins@ufu.br", sdf.parse("18/03/2025"), ebm);
                Aluno aPedro    = cadastrarAluno(ufu, "Pedro Henrique Borges", "85296374105", "pedro.borges@ufu.br", sdf.parse("12/08/2026"), eca);
                Aluno aVinicius = cadastrarAluno(ufu, "Vinícius Alves Teixeira", "96374185206", "vinicius.teixeira@ufu.br", sdf.parse("12/08/2026"), emc);
                Aluno aMariana  = cadastrarAluno(ufu, "Mariana Barbosa Lima", "15935748607", "mariana.lima@ufu.br", sdf.parse("14/08/2026"), eqm);
                Aluno aDaniel   = cadastrarAluno(ufu, "Daniel Ribeiro Cardoso", "35715948608", "daniel.cardoso@ufu.br", sdf.parse("14/08/2026"), ecv);
                Aluno aLarissa  = cadastrarAluno(ufu, "Larissa Moura Vasconcelos", "78945612304", "larissa.vasconcelos@ufu.br", sdf.parse("15/08/2026"), adm);
                Aluno aBruno    = cadastrarAluno(ufu, "Bruno César Carvalho", "95175348609", "bruno.carvalho@ufu.br", sdf.parse("15/08/2026"), adm);
                Aluno aBeatriz  = cadastrarAluno(ufu, "Beatriz Lopes Cunha", "75395148610", "beatriz.cunha@ufu.br", sdf.parse("16/08/2026"), dir);
                Aluno aFelipe   = cadastrarAluno(ufu, "Felipe Augusto Azevedo", "45678912311", "felipe.azevedo@ufu.br", sdf.parse("16/08/2026"), dir);
                Aluno aJulia    = cadastrarAluno(ufu, "Júlia Mendes Correia", "65432198712", "julia.correia@ufu.br", sdf.parse("10/03/2024"), med);
                Aluno aArthur   = cadastrarAluno(ufu, "Arthur Rodrigues Pinto", "32165498713", "arthur.pinto@ufu.br", sdf.parse("10/03/2024"), med);
                Aluno aLeticia  = cadastrarAluno(ufu, "Letícia Gomes Duarte", "78912345614", "leticia.duarte@ufu.br", sdf.parse("17/08/2026"), eco);

                // =====================================================================
                // 5. TURMAS
                // =====================================================================
                Turma tPoo         = new Turma("FACOM26POO01", poo, "2026/2", pGean, 40);
                Turma tMdc         = new Turma("FAMAT26MDC01", mdc, "2026/2", pCesar, 45);
                Turma tEst1        = new Turma("FAMAT26EST101", est1, "2026/2", pJuliaP, 40);

                Turma tAed1        = new Turma("FACOM31201-T01", aed1, "2026/2", pMarcos, 35);
                Turma tAed2        = new Turma("FACOM31301-T01", aed2, "2026/2", pRenata, 30);
                Turma tBd1         = new Turma("FACOM31401-T01", bd1, "2026/2", pMarcos, 30);
                Turma tSo          = new Turma("FACOM31502-T01", so, "2026/1", pThiago, 25);
                Turma tIa          = new Turma("FACOM31701-T01", ia, "2026/2", pRenata, 2);
                Turma tCalc1A      = new Turma("FAMAT31101-T01", calc1, "2026/2", pBeatriz, 45);
                Turma tCalc1Lotada = new Turma("FAMAT31101-T02", calc1, "2026/2", pEduardo, 1);
                Turma tGaal        = new Turma("FAMAT31103-T01", gaal, "2026/2", pEduardo, 40);
                Turma tCircuitos   = new Turma("FEELT31204-T01", circuitos, "2026/2", pFernanda, 25);
                Turma tTermo       = new Turma("FEMEC31302-T01", termo, "2026/2", pGustavo, 30);
                Turma tFenTransp   = new Turma("FEQUI31401-T01", fenTransp, "2026/2", pCamila, 25);
                Turma tFisica1     = new Turma("INFIS31101-T01", fisica1, "2026/2", pSabrina, 50);
                Turma tTga         = new Turma("FAGEN31102-T01", tga, "2026/2", pRicardo, 40);
                Turma tDirConst    = new Turma("FADIR31101-T01", dirConst, "2026/2", pHelena, 45);
                Turma tAnatomia    = new Turma("FAMED31101-T01", anatomia, "2026/2", pAndre, 30);
                Turma tMacro       = new Turma("IERI31201-T01", macro, "2026/2", pRicardo, 35);

                ufu.adicionarTurma(tPoo);
                ufu.adicionarTurma(tMdc);
                ufu.adicionarTurma(tEst1);
                ufu.adicionarTurma(tAed1);
                ufu.adicionarTurma(tAed2);
                ufu.adicionarTurma(tBd1);
                ufu.adicionarTurma(tSo);
                ufu.adicionarTurma(tIa);
                ufu.adicionarTurma(tCalc1A);
                ufu.adicionarTurma(tCalc1Lotada);
                ufu.adicionarTurma(tGaal);
                ufu.adicionarTurma(tCircuitos);
                ufu.adicionarTurma(tTermo);
                ufu.adicionarTurma(tFenTransp);
                ufu.adicionarTurma(tFisica1);
                ufu.adicionarTurma(tTga);
                ufu.adicionarTurma(tDirConst);
                ufu.adicionarTurma(tAnatomia);
                ufu.adicionarTurma(tMacro);

                // =====================================================================
                // 6. MATRÍCULAS E AVALIAÇÕES
                //    (APENAS OS 4 TIPOS DA UI: "Prova", "Trabalho", "Seminário", "Lista")
                // =====================================================================
                Matricula mJoao = matricular(tPoo, aJoao);
                mJoao.adicionarAvaliacao("Prova", 8.0);
                mJoao.adicionarAvaliacao("Trabalho", 9.0);

                matricular(tPoo, aPaulo);

                Matricula mMaryMdc = matricular(tMdc, aMaryane);
                mMaryMdc.adicionarAvaliacao("Prova", 6.0);
                mMaryMdc.adicionarAvaliacao("Lista", 8.0);

                Matricula mMaryEst = matricular(tEst1, aMaryane);
                mMaryEst.adicionarAvaliacao("Prova", 4.0);
                mMaryEst.adicionarAvaliacao("Trabalho", 5.0);

                Matricula m1 = matricular(tAed1, aLucas);
                m1.adicionarAvaliacao("Prova", 8.0);
                m1.adicionarAvaliacao("Trabalho", 9.0);
                m1.adicionarAvaliacao("Lista", 9.5);

                Matricula m2 = matricular(tCalc1Lotada, aLucas);
                m2.adicionarAvaliacao("Prova", 7.5);
                matricular(tGaal, aLucas);

                Matricula m3 = matricular(tAed1, aCamila);
                m3.adicionarAvaliacao("Prova", 5.0);
                m3.adicionarAvaliacao("Lista", 7.0);

                Matricula m4 = matricular(tBd1, aCamila);
                m4.adicionarAvaliacao("Prova", 4.5);
                m4.adicionarAvaliacao("Trabalho", 5.0);

                Matricula m5 = matricular(tSo, aMateus);
                m5.adicionarAvaliacao("Prova", 9.2);
                m5.adicionarAvaliacao("Seminário", 8.8);

                Matricula m6 = matricular(tIa, aMateus);
                m6.adicionarAvaliacao("Seminário", 9.5);

                Matricula m7 = matricular(tIa, aSofia);
                m7.adicionarAvaliacao("Seminário", 10.0);

                Matricula m8 = matricular(tAed2, aSofia);
                m8.adicionarAvaliacao("Prova", 8.5);
                m8.adicionarAvaliacao("Trabalho", 8.0);

                Matricula m9 = matricular(tCalc1A, aGabriel);
                m9.adicionarAvaliacao("Prova", 2.0);
                m9.adicionarAvaliacao("Lista", 3.5);
                m9.adicionarAvaliacao("Prova", 3.0);

                Matricula m10 = matricular(tCircuitos, aRafael);
                m10.adicionarAvaliacao("Prova", 3.0);
                aRafael.cancelarMatricula(m10);

                Matricula m11 = matricular(tFisica1, aRafael);
                m11.adicionarAvaliacao("Prova", 7.0);
                m11.adicionarAvaliacao("Trabalho", 8.5);

                Matricula m12 = matricular(tCircuitos, aIsabela);
                m12.adicionarAvaliacao("Prova", 8.8);
                m12.adicionarAvaliacao("Lista", 9.2);

                Matricula m13 = matricular(tCircuitos, aPedro);
                m13.adicionarAvaliacao("Prova", 6.5);

                Matricula m14 = matricular(tTermo, aVinicius);
                m14.adicionarAvaliacao("Prova", 7.8);
                m14.adicionarAvaliacao("Lista", 6.8);

                Matricula m15 = matricular(tFenTransp, aMariana);
                m15.adicionarAvaliacao("Prova", 5.5);
                m15.adicionarAvaliacao("Trabalho", 5.8);

                matricular(tGaal, aDaniel);

                matricular(tTga, aLarissa);
                Matricula m16 = matricular(tTga, aBruno);
                m16.adicionarAvaliacao("Seminário", 8.5);
                m16.adicionarAvaliacao("Prova", 7.5);

                Matricula m17 = matricular(tDirConst, aBeatriz);
                m17.adicionarAvaliacao("Seminário", 9.5);
                m17.adicionarAvaliacao("Prova", 9.0);

                Matricula m18 = matricular(tDirConst, aFelipe);
                m18.adicionarAvaliacao("Seminário", 6.0);
                aFelipe.cancelarMatricula(m18);

                Matricula m19 = matricular(tAnatomia, aJulia);
                m19.adicionarAvaliacao("Trabalho", 9.4);
                m19.adicionarAvaliacao("Prova", 8.9);

                Matricula m20 = matricular(tAnatomia, aArthur);
                m20.adicionarAvaliacao("Trabalho", 7.2);

                Matricula m21 = matricular(tMacro, aLeticia);
                m21.adicionarAvaliacao("Prova", 8.0);
                m21.adicionarAvaliacao("Trabalho", 8.5);

                repo.salvar(ufu);
                System.out.println("BANCO DE DADOS UFU GERADO COM SUCESSO! Disciplinas cadastradas: " + ufu.getDisciplinas().size());
        }

        private static Disciplina criarDisciplina(Universidade u, String codigo, String nome, int ch) {
                Disciplina d = new Disciplina(codigo, nome, ch);
                u.adicionarDisciplina(d);
                return d;
        }

        private static Matricula matricular(Turma turma, Aluno aluno) throws Exception {
                Matricula m = new Matricula(aluno, turma);
                if (turma.getMatriculas().contains(m)) {
                        throw new MatriculaDuplicadaException("Aluno já matriculado na turma " + turma.getCodigo());
                }
                if (!turma.temVaga()) {
                        throw new TurmaCheiaException("Turma " + turma.getCodigo() + " está lotada.");
                }
                turma.adicionarMatricula(m);
                if (!aluno.getMatriculas().contains(m)) {
                        aluno.getMatriculas().add(m);
                }
                return m;
        }

        private static Departamento obterOuCriarDepto(Universidade u, String nome) {
                for (Departamento d : u.getDepartamentos()) {
                        if (d.getNome().equalsIgnoreCase(nome)) return d;
                }
                Departamento novo = new Departamento(nome);
                u.adicionarDepartamento(novo);
                return novo;
        }

        private static Curso obterOuCriarCurso(Universidade u, String codigo, String nome, int ch) {
                for (Curso c : u.getCursos()) {
                        if (c.getCodigo().equalsIgnoreCase(codigo)) return c;
                }
                Curso novo = new Curso(codigo, nome, ch);
                u.adicionarCurso(novo);
                return novo;
        }

        private static Professor cadastrarProf(Universidade u, Departamento d, String nome, String cpf, String email, String siape, String tit) throws Exception {
                for (Professor p : u.getProfessores()) {
                        if (p.getSiape().equals(siape)) return p;
                }
                Professor novo = new Professor(nome, cpf, email, siape, tit);
                u.cadastrarProfessor(novo);
                d.adicionarProfessor(novo);
                return novo;
        }

        private static Aluno cadastrarAluno(Universidade u, String nome, String cpf, String email, Date ingresso, Curso c) throws Exception {
                for (Aluno a : u.getAlunos()) {
                        if (a.getCpf().replaceAll("\\D", "").equals(cpf.replaceAll("\\D", ""))) return a;
                }
                Aluno novo = new Aluno(nome, cpf, email, ingresso, c);
                u.cadastrarAluno(novo);
                return novo;
        }
}