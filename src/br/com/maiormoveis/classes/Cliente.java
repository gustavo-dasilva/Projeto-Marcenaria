/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.maiormoveis.classes;

/**
 *
 * @author euraf
 */
public class Cliente extends Pessoa {
    private String cpf;
    private String endereco;
    private String email;

    // Construtor
    public Cliente(){}
    public Cliente(String nome, String id, String fone, String cpf, String endereco, String email, String status) {
        super(nome, id, fone, status);
        this.cpf = cpf;
        this.endereco = endereco;
        this.email = email;
    }
    
    // Getters e Setters
    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }
    
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
