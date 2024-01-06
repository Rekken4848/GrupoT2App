package com.example.btlealumnos2021;

public class POJOAdmin {
    private String dni_admin;

    // --------------------------------------------------------------
    // POJOAdmin()
    // --------------------------------------------------------------
    public POJOAdmin() {
    }

    // --------------------------------------------------------------
    // Texto --> POJOAdmin()
    // --------------------------------------------------------------
    public POJOAdmin(String dni_admin) {
        this.dni_admin = dni_admin;
    }

    // --------------------------------------------------------------
    // getDni_admin() --> Texto
    // --------------------------------------------------------------
    public String getDni_admin() {
        return dni_admin;
    }

    // --------------------------------------------------------------
    // Texto --> setDni_admin()
    // --------------------------------------------------------------
    public void setDni_admin(String dni_admin) {
        this.dni_admin = dni_admin;
    }
}
