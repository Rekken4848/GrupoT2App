package com.example.btlealumnos2021;

public class POJOAnuncio {
    private String titulo;
    private String contenido;
    private String problemas;
    private String estado;

    public POJOAnuncio(){}

    public POJOAnuncio(String titulo, String contenido, String problemas, String estado){
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
}
