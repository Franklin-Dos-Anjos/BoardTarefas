package com.boardtarefas.model;

import java.time.LocalDateTime;

public class Bloqueio {
    private int id;
    private int cardId;
    private String motivoBloqueio;
    private LocalDateTime dataBloqueio;
    private String motivoDesbloqueio;
    private LocalDateTime dataDesbloqueio;

    public Bloqueio() {}

    public Bloqueio(int cardId, String motivoBloqueio) {
        this.cardId = cardId;
        this.motivoBloqueio = motivoBloqueio;
        this.dataBloqueio = LocalDateTime.now();
    }

    // Getters e Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCardId() {
        return cardId;
    }

    public void setCardId(int cardId) {
        this.cardId = cardId;
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
}