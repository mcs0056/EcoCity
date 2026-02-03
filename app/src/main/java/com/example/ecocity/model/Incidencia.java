package com.example.ecocity.model;

public class Incidencia {

    private int id;
    private String titulo;
    private String descripcion;
    private int importancia; //"0=Baja", "1=Media", "2=Alta"

    private String fotoRuta;

    //Constructores
    public Incidencia() {

    }
    public Incidencia(String titulo, String descripcion, int importancia, String fotoRuta) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.importancia = importancia;
        this.fotoRuta = fotoRuta;
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
    public int getImportancia() {
        return importancia;
    }
    public String getFotoRuta() {return fotoRuta;}



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
    public void setImportancia(int importancia) {
        this.importancia = importancia;
    }
    public void setFotoRuta(String fotoRuta) {this.fotoRuta = fotoRuta;}
}

