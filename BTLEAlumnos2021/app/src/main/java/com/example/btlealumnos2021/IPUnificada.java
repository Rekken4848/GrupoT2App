package com.example.btlealumnos2021;

public class IPUnificada {

    private String ipServidor;

    //"192.168.21.130"
    public IPUnificada() {
        this.ipServidor = "http://" + "192.168.1.21" + ":8080";
    }

    public String getIpServidor() {
        return ipServidor;
    }
}
