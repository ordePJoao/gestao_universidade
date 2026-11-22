package br.edu.universidade.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

//Classe que representa um Departamento da Universidade e implementa Serializable conforme requisito do projeto
public class Departamento implements Serializable {
    private static final long serialVersionUID = 1L;

    private String nome;
    
    //Lista de Professores
    private List<Professor> professores;

    public Departamento(String nome) {
        this.nome = nome;
        this.professores = new ArrayList<>();
    }

    //Adiciona um professor ao departamento everifica se a lista já contém o professor para evitar duplicação
    public void adicionarProfessor(Professor p) {
        if (!this.professores.contains(p)) {
            this.professores.add(p);
        } else {
            System.out.println("Professor ja cadastrado neste departamento.");
        }
    }

    //Getters e Setters com acesso encapsulado
    public String getNome() { 
        return nome; 
    }
    
    public void setNome(String nome) { 
        this.nome = nome; 
    }

    //Retorna a lista de professores Não criei o setProfessores para obrigar o uso do adicionarProfessor(p)
    public List<Professor> getProfessores() { 
        return professores; 
    }
}