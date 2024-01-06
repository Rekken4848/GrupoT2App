package com.example.btlealumnos2021;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class POJOMedicion {
    private int id;
    private float valor;
    private int tipo_valor_id;
    private String fecha;
    private String lugar;

    // --------------------------------------------------------------
    // N, R, N, Texto, Texto --> POJOMedicion()
    // --------------------------------------------------------------
    public POJOMedicion(int id, float valor, int tipo_valor_id, String fecha, String lugar) {
        this.id = id;
        this.valor = valor;
        this.tipo_valor_id = tipo_valor_id;
        this.fecha = fecha;
        this.lugar = lugar;
    }

    // --------------------------------------------------------------
    // getFechaAsDate() --> fecha
    // --------------------------------------------------------------
    public Date getFechaAsDate(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            return dateFormat.parse(this.fecha);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    // --------------------------------------------------------------
    // getId() --> N
    // --------------------------------------------------------------
    public int getId() {
        return id;
    }

    // --------------------------------------------------------------
    // N --> setId()
    // --------------------------------------------------------------
    public void setId(int id) {
        this.id = id;
    }

    // --------------------------------------------------------------
    // getValor() --> R
    // --------------------------------------------------------------
    public float getValor() {
        return valor;
    }

    // --------------------------------------------------------------
    // R --> setValor()
    // --------------------------------------------------------------
    public void setValor(float valor) {
        this.valor = valor;
    }

    // --------------------------------------------------------------
    // getTipo_valor_id() --> N
    // --------------------------------------------------------------
    public int getTipo_valor_id() {
        return tipo_valor_id;
    }

    // --------------------------------------------------------------
    // N --> setTipo_valor_id()
    // --------------------------------------------------------------
    public void setTipo_valor_id(int tipo_valor_id) {
        this.tipo_valor_id = tipo_valor_id;
    }

    // --------------------------------------------------------------
    // getFecha() --> Texto
    // --------------------------------------------------------------
    public String getFecha() {
        return fecha;
    }

    // --------------------------------------------------------------
    // Texto --> setFecha()
    // --------------------------------------------------------------
    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    // --------------------------------------------------------------
    // getLugar() --> Texto
    // --------------------------------------------------------------
    public String getLugar() {
        return lugar;
    }

    // --------------------------------------------------------------
    // Texto --> setLugar()
    // --------------------------------------------------------------
    public void setLugar(String lugar) {
        this.lugar = lugar;
    }
}
