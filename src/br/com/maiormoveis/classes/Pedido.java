/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.maiormoveis.classes;

import java.util.Date;

/**
 *
 * @author euraf
 */
public class Pedido {

    // Atributos
    private int nPedido;
    private String dataPedido;
    private String produto;
    private int quantidade;
    private String descricao;
    private double custo;
    private String idvendedor;
    private int markup;
    private double orcamento;
    private String idcliente;
    private String status;
    private String tipo;

    // Construtor
    public Pedido() {
    }

    public Pedido(int nPedido, String dataPedido, String produto, int quantidade, String descricao, double custo, String idvendedor, int markup, double orcamento, String idcliente, String status, String tipo) {
        this.nPedido = nPedido;
        this.dataPedido = dataPedido;
        this.produto = produto;
        this.quantidade = quantidade;
        this.descricao = descricao;
        this.custo = custo;
        this.idvendedor = idvendedor;
        this.markup = markup;
        this.orcamento = orcamento;
        this.idcliente = idcliente;
        this.status = status;
        this.tipo = tipo;
    }

    // Getters e Setters
    public int getNPedido() {
        return nPedido;
    }

    public void setNPedido(int nPedido) {
        this.nPedido = nPedido;
    }

    public String getDataPedido() {
        return dataPedido;
    }

    public void setDataPedido(String dataPedido) {
        this.dataPedido = dataPedido;
    }

    public String getProduto() {
        return produto;
    }

    public void setProduto(String produto) {
        this.produto = produto;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public double getCusto() {
        return custo;
    }

    public void setCusto(double custo) {
        this.custo = custo;
    }

    public String getIdvendedor() {
        return idvendedor;
    }

    public void setIdvendedor(String idvendedor) {
        this.idvendedor = idvendedor;
    }

    public int getMarkup() {
        return markup;
    }

    public void setMarkup(int markup) {
        this.markup = markup;
    }

    public double getOrcamento() {
        return orcamento;
    }

    public void setOrcamento(double orcamento) {
        this.orcamento = orcamento;
    }

    public String getIdcliente() {
        return idcliente;
    }

    public void setIdcliente(String idcliente) {
        this.idcliente = idcliente;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
}
