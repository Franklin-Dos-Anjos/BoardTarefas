package com.boardtarefas.model;

import java.util.ArrayList;
import java.util.List;

public class Coluna {
    private int id;
    private int boardId;
    private String nome;
    private int ordemColuna;
    private TipoColuna tipo;
    private List<Card> cards;

    public Coluna() {
        this.cards = new ArrayList<>();
    }

    public Coluna(String nome, int ordemColuna, TipoColuna tipo) {
        this.nome = nome;
        this.ordemColuna = ordemColuna;
        this.tipo = tipo;
        this.cards = new ArrayList<>();
    }

    // Getters e Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBoardId() {
        return boardId;
    }

    public void setBoardId(int boardId) {
        this.boardId = boardId;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getOrdemColuna() {
        return ordemColuna;
    }

    public void setOrdemColuna(int ordemColuna) {
        this.ordemColuna = ordemColuna;
    }

    public TipoColuna getTipo() {
        return tipo;
    }

    public void setTipo(TipoColuna tipo) {
        this.tipo = tipo;
    }

    public List<Card> getCards() {
        return cards;
    }

    public void setCards(List<Card> cards) {
        this.cards = cards;
    }

    public void adicionarCard(Card card) {
        this.cards.add(card);
    }

    public void removerCard(Card card) {
        this.cards.remove(card);
    }

    @Override
    public String toString() {
        return "Coluna{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", ordemColuna=" + ordemColuna +
                ", tipo=" + tipo +
                ", quantidadeCards=" + cards.size() +
                '}';
    }
}