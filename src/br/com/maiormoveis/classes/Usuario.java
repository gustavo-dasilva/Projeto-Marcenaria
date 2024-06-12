/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.maiormoveis.classes;

/**
 *
 * @author euraf
 * 
 * 
 
 */
 public class Usuario extends Pessoa {
    private String login;
    private String senha;
    private String perfil;

    // Construtor
    public Usuario(){}
    public Usuario(String nome, String id, String fone, String login, String senha, String perfil, String status) {
        super(nome, id, fone, status);
        this.login = login;
        this.senha = senha;
        this.perfil = perfil;
    }
    
    //getters and setter
     public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getPerfil() {
        return perfil;
    }

    public void setPerfil(String perfil) {
        this.perfil = perfil;
    }
    
}
    