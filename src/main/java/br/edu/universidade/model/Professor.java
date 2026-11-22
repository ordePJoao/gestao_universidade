
package br.edu.universidade.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

//Classe Professor, herda de Pessoa
public class Professor extends Pessoa implements Serializable {
    private static final long serialVersionUID = 1L;
    
    //Atributos específicos do Professor
    private String siape;
    private String titulacao;
    
    //Lista de turmas que o professor da aula
    private List<Turma> turmas; 

    //Construtor
    public Professor(String nome, String cpf, String email, String siape, String titulacao) {
        super(nome, cpf, email); //Chama o construtor da classe Pessoa
        this.siape = siape;
        this.titulacao = titulacao;
        this.turmas = new ArrayList<>(); //Inicializa a lista de turmas
    }

    //Sobrescrita do método abstrato de Pessoa para o Professor o identificador principal é o siape
    @Override
    public String getIdent() { 
        return this.siape; 
    }

    //Retorna a lista de turmas do professor
    public List<Turma> getTurmas() { 
        return turmas; 
    }

    //Getters e Setters
    public String getSiape() { return siape; }
    public void setSiape(String siape) { this.siape = siape; }
    
    public String getTitulacao() { return titulacao; }
    public void setTitulacao(String titulacao) { this.titulacao = titulacao; }
    
    public void setTurmas(List<Turma> turmas) { this.turmas = turmas; }
}