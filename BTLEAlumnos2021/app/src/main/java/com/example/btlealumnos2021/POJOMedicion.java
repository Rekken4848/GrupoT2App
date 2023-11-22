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

    public POJOMedicion(int id, float valor, int tipo_valor_id, String fecha, String lugar) {
        this.id = id;
        this.valor = valor;
        this.tipo_valor_id = tipo_valor_id;
        this.fecha = fecha;
        this.lugar = lugar;
    }

    public Date getFechaAsDate(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            return dateFormat.parse(this.fecha);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public float getValor() {
        return valor;
    }

    public void setValor(float valor) {
        this.valor = valor;
    }

    public int getTipo_valor_id() {
        return tipo_valor_id;
    }

    public void setTipo_valor_id(int tipo_valor_id) {
        this.tipo_valor_id = tipo_valor_id;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getLugar() {
        return lugar;
    }

    public void setLugar(String lugar) {
        this.lugar = lugar;
    }
}
