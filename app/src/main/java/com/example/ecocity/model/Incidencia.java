package com.example.ecocity.model;

import com.google.firebase.firestore.Exclude;

public class Incidencia {
    private int id; // Usado para SQLite
    private String firebaseId;
    private String titulo, descripcion, fotoRuta, fotoBase64;
    private int importancia;
    private double latitud, longitud;
    private long timestamp;

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    // CONSTRUCTOR VACÍO: Obligatorio para Firebase Firestore
    public Incidencia() {
    }

    public Incidencia(String titulo, String descripcion, int importancia, String fotoRuta, double latitud,
            double longitud) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.importancia = importancia;
        this.fotoRuta = fotoRuta;
        this.latitud = latitud;
        this.longitud = longitud;
    }

    // Getters y Setters...
    @Exclude // Evita que el ID de SQLite se suba a Firebase si prefieres usar el ID de la
             // nube
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirebaseId() {
        return firebaseId;
    }

    public void setFirebaseId(String firebaseId) {
        this.firebaseId = firebaseId;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getImportancia() {
        return importancia;
    }

    public void setImportancia(int importancia) {
        this.importancia = importancia;
    }

    public String getFotoRuta() {
        return fotoRuta;
    }

    public void setFotoRuta(String fotoRuta) {
        this.fotoRuta = fotoRuta;
    }

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

    public String getFotoBase64() {
        return fotoBase64;
    }

    public void setFotoBase64(String fotoBase64) {
        this.fotoBase64 = fotoBase64;
    }
}