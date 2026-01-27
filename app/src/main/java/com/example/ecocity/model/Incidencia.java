package com.example.ecocity.model;

public class Incidencia {

    private int id;
    private String titulo;
    private String descripcion;
    private int urgencia;

    //Constructores
    public Incidencia() {

    }
    public Incidencia(String titulo, String descripcion, int urgencia) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.urgencia = urgencia;
    }

    //Getters
    public int getId() {
        return id;
    }
    public String getTitulo() {
        return titulo;
    }
    public String getDescripcion() {
        return descripcion;
    }
    public int getUrgencia() {
        return urgencia;
    }


    //Setters
    public void setId(int id) {
        this.id = id;
    }
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    public void setUrgencia(int urgencia) {
        this.urgencia = urgencia;
    }
}

