package com.boardtarefas.model;

import java.time.LocalDateTime;

public class Card {
    private int id;
    private int colunaId;
    private String titulo;
    private String descricao;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataEntradaColuna;
    private boolean bloqueado;
    private String motivoBloqueio;
    private LocalDateTime dataBloqueio;
    private String motivoDesbloqueio;
    private LocalDateTime dataDesbloqueio;

    public Card() {
        this.dataCriacao = LocalDateTime.now();
        this.dataEntradaColuna = LocalDateTime.now();
        this.bloqueado = false;
    }

    public Card(String titulo, String descricao) {
        this.titulo = titulo;
        this.descricao = descricao;
        this.dataCriacao = LocalDateTime.now();
        this.dataEntradaColuna = LocalDateTime.now();
        this.bloqueado = false;
    }

    // Getters e Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getColunaId() {
        return colunaId;
    }

    public void setColunaId(int colunaId) {
        this.colunaId = colunaId;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public LocalDateTime getDataEntradaColuna() {
        return dataEntradaColuna;
    }

    public void setDataEntradaColuna(LocalDateTime dataEntradaColuna) {
        this.dataEntradaColuna = dataEntradaColuna;
    }

    public boolean isBloqueado() {
        return bloqueado;
    }

    public void setBloqueado(boolean bloqueado) {
        this.bloqueado = bloqueado;
    }

    public String getMotivoBloqueio() {
        return motivoBloqueio;
    }

    public void setMotivoBloqueio(String motivoBloqueio) {
        this.motivoBloqueio = motivoBloqueio;
    }

    public LocalDateTime getDataBloqueio() {
        return dataBloqueio;
    }

    public void setDataBloqueio(LocalDateTime dataBloqueio) {
        this.dataBloqueio = dataBloqueio;
    }

    public String getMotivoDesbloqueio() {
        return motivoDesbloqueio;
    }

    public void setMotivoDesbloqueio(String motivoDesbloqueio) {
        this.motivoDesbloqueio = motivoDesbloqueio;
    }

    public LocalDateTime getDataDesbloqueio() {
        return dataDesbloqueio;
    }

    public void setDataDesbloqueio(LocalDateTime dataDesbloqueio) {
        this.dataDesbloqueio = dataDesbloqueio;
    }

    public void bloquear(String motivo) {
        this.bloqueado = true;
        this.motivoBloqueio = motivo;
        this.dataBloqueio = LocalDateTime.now();
    }

    public void desbloquear(String motivo) {
        this.bloqueado = false;
        this.motivoDesbloqueio = motivo;
        this.dataDesbloqueio = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "Card{" +
                "id=" + id +
                ", titulo='" + titulo + '\'' +
                ", descricao='" + descricao + '\'' +
                ", bloqueado=" + bloqueado +
                '}';
    }
}