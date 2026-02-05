package com.example.ecocity.model;

public class Mensaje {

    private String texto;
    private boolean esBot;

    public Mensaje(String texto, boolean esBot) {
        this.texto = texto;
        this.esBot = esBot;
    }

    public String getTexto() {
        return texto;
    }

    public boolean isEsBot() {
        return esBot;
    }
}
