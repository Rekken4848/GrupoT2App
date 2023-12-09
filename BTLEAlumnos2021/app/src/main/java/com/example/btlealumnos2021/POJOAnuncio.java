package com.example.btlealumnos2021;

public class POJOAnuncio {
    private int anuncio_id;
    private String titulo;
    private String contenido;
    private String problemas;
    private String estado;

    public POJOAnuncio(){}
    public POJOAnuncio(String titulo, String contenido, String problemas){
        this.titulo=titulo;
        this.contenido=contenido;
        this.problemas=problemas;
    }
    public POJOAnuncio(String titulo, String contenido, String problemas, String estado){
        this.titulo=titulo;
        this.contenido=contenido;
        this.problemas=problemas;
        this.estado=estado;
    }

    public POJOAnuncio(String titulo, String contenido, String problemas, String estado, int anuncio_id){
        this.anuncio_id=anuncio_id;
        this.titulo=titulo;
        this.contenido=contenido;
        this.problemas=problemas;
        this.estado=estado;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public String getProblemas() {
        return problemas;
    }

    public void setProblemas(String problemas) {
        this.problemas = problemas;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public int getAnuncio_id() {
        return anuncio_id;
    }

    public void setAnuncio_id(int idAnuncio) {
        this.anuncio_id = idAnuncio;
    }
}
