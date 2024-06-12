/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.maiormoveis.dal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.DriverManager;


/**
 *
 * @author euraf
 */
public class ModuloConexao {

    //Metodo para conectar com o banco de dados
    public static Connection conector() {
        Connection conexao = null;
        //a linha abaixo inicia o driver
        String driver = "com.mysql.cj.jdbc.Driver";
        //variáveis para armazenar info referentes ao db
        String url = "jdbc:mysql://localhost:3306/dbmarcenaria?characterEncoding=utf-8";
        String user = "dba";
        String password = "Sql110501";
        //Estabelecendo conexao com o banco
        try {
            Class.forName(driver);
            conexao = DriverManager.getConnection(url, user, password);
            return conexao;
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }
}
