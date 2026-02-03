package com.example.ecocity.model;

public class Incidencia {

    private int id;
    private String titulo;
    private String descripcion;
    private int importancia; //"0=Baja", "1=Media", "2=Alta"

    private String fotoRuta;
    private double latitud;
    private double longitud;

    //Constructores
    public Incidencia() {

    }
    public Incidencia(String titulo, String descripcion, int importancia, String fotoRuta, double latitud, double longitud) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.importancia = importancia;
        this.fotoRuta = fotoRuta;
        this.latitud = latitud;
        this.longitud = longitud;
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
    public double getLatitud() { return latitud; }
    public double getLongitud() { return longitud; }



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
    public void setLatitud(double latitud) { this.latitud = latitud; }
    public void setLongitud(double longitud) { this.longitud = longitud; }
}

