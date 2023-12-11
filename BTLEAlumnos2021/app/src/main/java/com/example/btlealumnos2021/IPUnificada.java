package com.example.btlealumnos2021;

public class IPUnificada {

    private String ipServidor;

    public IPUnificada() {
        this.ipServidor = "http://" + "192.168.21.58" + ":8080";
    }

    public String getIpServidor() {
        return ipServidor;
    }
}
