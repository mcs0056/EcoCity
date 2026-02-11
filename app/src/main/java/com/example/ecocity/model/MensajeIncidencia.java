package com.example.ecocity.model;

public class MensajeIncidencia {

    private int idIncidencia;
    private int idUsuario;
    private String texto;
    private boolean esPropio;
    private long timestamp;

    public MensajeIncidencia(){} //Construtor vacío

    public MensajeIncidencia(int idIncidencia, int idUsuario, String texto, boolean esPropio, long timestamp){
        this.idIncidencia = idIncidencia;
        this.idUsuario = idUsuario;
        this.texto = texto;
        this.esPropio = esPropio;
        this.timestamp = timestamp;
    }

    //Getters
    public int getIdIncidencia(){return idIncidencia;}

    public int getIdUsuario(){return idUsuario;}

    public String getTexto(){return texto;}

    public boolean isEsPropio(){return esPropio;}

    public long getTimestamp(){return timestamp;}
}
