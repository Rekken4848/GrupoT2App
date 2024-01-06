package com.example.btlealumnos2021;

public class POJOAnuncio {
    private int anuncio_id;
    private String titulo;
    private String contenido;
    private String problemas;
    private String estado;

    // --------------------------------------------------------------
    // POJOAnuncio()
    // --------------------------------------------------------------
    public POJOAnuncio(){}
    // --------------------------------------------------------------
    // Texto, Texto, Texto --> POJOAnuncio()
    // --------------------------------------------------------------
    public POJOAnuncio(String titulo, String contenido, String problemas){
        this.titulo=titulo;
        this.contenido=contenido;
        this.problemas=problemas;
    }
    // --------------------------------------------------------------
    // Texto, Texto, Texto, Texto --> POJOAnuncio()
    // --------------------------------------------------------------
    public POJOAnuncio(String titulo, String contenido, String problemas, String estado){
        this.titulo=titulo;
        this.contenido=contenido;
        this.problemas=problemas;
        this.estado=estado;
    }

    // --------------------------------------------------------------
    // Texto, Texto, Texto, Texto, N --> POJOAnuncio()
    // --------------------------------------------------------------
    public POJOAnuncio(String titulo, String contenido, String problemas, String estado, int anuncio_id){
        this.anuncio_id=anuncio_id;
        this.titulo=titulo;
        this.contenido=contenido;
        this.problemas=problemas;
        this.estado=estado;
    }

    // --------------------------------------------------------------
    // getTitulo() --> Texto
    // --------------------------------------------------------------
    public String getTitulo() {
        return titulo;
    }

    // --------------------------------------------------------------
    // Texto --> setTitulo()
    // --------------------------------------------------------------
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    // --------------------------------------------------------------
    // getContenido() --> Texto
    // --------------------------------------------------------------
    public String getContenido() {
        return contenido;
    }

    // --------------------------------------------------------------
    // Texto --> setContenido()
    // --------------------------------------------------------------
    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    // --------------------------------------------------------------
    // getProblemas() --> Texto
    // --------------------------------------------------------------
    public String getProblemas() {
        return problemas;
    }

    // --------------------------------------------------------------
    // Texto --> setProblemas()
    // --------------------------------------------------------------
    public void setProblemas(String problemas) {
        this.problemas = problemas;
    }

    // --------------------------------------------------------------
    // getEstado() --> Texto
    // --------------------------------------------------------------
    public String getEstado() {
        return estado;
    }

    // --------------------------------------------------------------
    // Texto --> setEstado()
    // --------------------------------------------------------------
    public void setEstado(String estado) {
        this.estado = estado;
    }

    // --------------------------------------------------------------
    // getAnuncio_id() --> N
    // --------------------------------------------------------------
    public int getAnuncio_id() {
        return anuncio_id;
    }

    // --------------------------------------------------------------
    // N --> setAnuncio_id()
    // --------------------------------------------------------------
    public void setAnuncio_id(int idAnuncio) {
        this.anuncio_id = idAnuncio;
    }
}
