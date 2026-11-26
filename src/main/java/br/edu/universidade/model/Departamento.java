package br.edu.universidade.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
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

    //Adiciona um professor ao departamento e verifica se a lista já contém o professor para evitar duplicação
    public void adicionarProfessor(Professor p) {
        if (!this.professores.contains(p)) {
            this.professores.add(p);
        } else {
            System.out.println("Professor ja cadastrado neste departamento.");
        }
    }
    //remover professor
    public boolean removerProfessor(Professor p) {
        if (p == null) {
            return false;
        }
        return this.professores.remove(p);
    }

    @Override
    public String toString() {
        return nome;
    }

    //Getters e Setters com acesso encapsulado
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    //Retorna visão somente leitura para obrigar o uso de adicionarProfessor(p) e evitar duplicatas
    public List<Professor> getProfessores() {
        return Collections.unmodifiableList(professores);
    }
}