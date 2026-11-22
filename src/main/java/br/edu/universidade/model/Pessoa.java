
package br.edu.universidade.model;

import java.io.Serializable;

//Classe abstrata que serve como base para Aluno e Professor
//Biblioteca Serializable para permitir a gravação do objeto num arquivo

public abstract class Pessoa implements Serializable {
    //Identificador de versao para a serialização
    private static final long serialVersionUID = 1L;
    
    //Atributos definidos como protected para que as subclasses Aluno e Professor tenham acesso direto
    protected String nome;
    protected String cpf;
    protected String email;

    //Construtor para inicializar os dados de uma Pessoa
    public Pessoa(String nome, String cpf, String email) {
        this.nome = nome;
        this.cpf = cpf;
        this.email = email;
    }

    //Metodo abstrato que força as subclasses a implementarem a sua própria forma de identificação (Polimorfismo)
    public abstract String getIdent();


    //Metodo comum herdado pelas subclasses
    public void exibirDados() {
        System.out.println("Nome: " + nome + " | CPF: " + cpf + " | Email: " + email);
    }
    
    //Getters e Setters para encapsulamento
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    
    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}