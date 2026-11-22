
package br.edu.universidade.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

//Classe Aluno, herda de Pessoa
public class Aluno extends Pessoa implements Serializable {
    private static final long serialVersionUID = 1L;
    
    //Atributos específicos do Aluno
    private String matricula;
    private Date dataIngresso;
    private Curso curso;
    
    //Lista de matrículas em disciplinas (relação de associação/agregação)
    private List<Matricula> matriculas; 

    //Construtor original: para não quebrar quem já usa
    public Aluno(String nome, String cpf, String email, String matricula, Date dataIngresso) {
        super(nome, cpf, email); //Chama o construtor da classe mãe - Pessoa)
        this.matricula = matricula;
        this.dataIngresso = dataIngresso;
        this.matriculas = new ArrayList<>(); //Inicializa a lista para evitar NullPointerException
    }
    
    //Construtor sem código: a Universidade gera o código ao cadastrar
    public Aluno(String nome, String cpf, String email, Date dataIngresso, Curso curso) {
        super(nome, cpf, email); //Chama o construtor da classe mãe - Pessoa)
        this.dataIngresso = dataIngresso;
        this.curso = curso;
        this.matriculas = new ArrayList<>(); //Inicializa a lista para evitar NullPointerException
    }

    //Sobrescrita do método abstrato de Pessoa, pra Aluno o identificador principal é  matrícula
    @Override
    public String getIdent() { 
        return this.matricula; 
    }

    //Retorna a lista de todas as matrículas do aluno
    public List<Matricula> getMatriculas() { 
        return matriculas; 
    }
    
    //Remove uma matrícula específica da lista do aluno
    public void cancelarMatricula(Matricula m) { 
        this.matriculas.remove(m); 
    }

    //Getters e Setters
    public String getMatricula() { return matricula; }
    public void setMatricula(String matricula) { this.matricula = matricula; }
    
    public Date getDataIngresso() { return dataIngresso; }
    public void setDataIngresso(Date dataIngresso) { this.dataIngresso = dataIngresso; }

    public Curso getCurso() { return curso; }
    public void setCurso(Curso curso){ this.curso = curso; }

    public String getCodigoMatricula() { return matricula; }
    public void setCodigoMatricula(String codigoMatricula) { this.matricula = codigoMatricula; }
}
