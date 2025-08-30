package com.boardtarefas.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Board {
    private int id;
    private String nome;
    private LocalDateTime dataCriacao;
    private List<Coluna> colunas;

    public Board() {
        this.colunas = new ArrayList<>();
    }

    public Board(String nome) {
        this.nome = nome;
        this.dataCriacao = LocalDateTime.now();
        this.colunas = new ArrayList<>();
    }

    // Getters e Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public List<Coluna> getColunas() {
        return colunas;
    }

    public void setColunas(List<Coluna> colunas) {
        this.colunas = colunas;
    }

    public void adicionarColuna(Coluna coluna) {
        this.colunas.add(coluna);
    }

    @Override
    public String toString() {
        return "Board{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", dataCriacao=" + dataCriacao +
                '}';
    }
}