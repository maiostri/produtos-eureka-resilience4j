package com.letscode.produtoseureka.domain;

import org.springframework.data.annotation.Id;

import java.math.BigDecimal;

public class Produto {

    @Id
    private String id;
    private String nome;
    private BigDecimal preco;
    private Integer usuario;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public BigDecimal getPreco() {
        return preco;
    }

    public void setPreco(BigDecimal preco) {
        this.preco = preco;
    }

    public Integer getUsuario() {
        return usuario;
    }

    public void setUsuario(Integer usuario) {
        this.usuario = usuario;
    }
}