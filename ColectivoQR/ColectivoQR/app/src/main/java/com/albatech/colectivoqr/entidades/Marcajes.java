package com.albatech.colectivoqr.entidades;

public class Marcajes {

    private String token;
    private String id_dispositivo;
    private String marcacion;
    private String tipo_marca;
    private String latitud;
    private String longitud;
    private String tipo_verificacion;
    private String trabajador;
    private String n_documento;

    public Marcajes(String token, String id_dispositivo, String marcacion, String tipo_marca, String latitud, String longitud, String tipo_verificacion, String trabajador, String n_documento) {
        this.token = token;
        this.id_dispositivo = id_dispositivo;
        this.marcacion = marcacion;
        this.tipo_marca = tipo_marca;
        this.latitud = latitud;
        this.longitud = longitud;
        this.tipo_verificacion = tipo_verificacion;
        this.trabajador = trabajador;
        this.n_documento = n_documento;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getId_dispositivo() {
        return id_dispositivo;
    }

    public void setId_dispositivo(String id_dispositivo) {
        this.id_dispositivo = id_dispositivo;
    }

    public String getMarcacion() {
        return marcacion;
    }

    public void setMarcacion(String marcacion) {
        this.marcacion = marcacion;
    }

    public String getTipo_marca() {
        return tipo_marca;
    }

    public void setTipo_marca(String tipo_marca) {
        this.tipo_marca = tipo_marca;
    }

    public String getLatitud() {
        return latitud;
    }

    public void setLatitud(String latitud) {
        this.latitud = latitud;
    }

    public String getLongitud() {
        return longitud;
    }

    public void setLongitud(String longitud) {
        this.longitud = longitud;
    }

    public String getTipo_verificacion() {
        return tipo_verificacion;
    }

    public void setTipo_verificacion(String tipo_verificacion) {
        this.tipo_verificacion = tipo_verificacion;
    }

    public String getTrabajador() {
        return trabajador;
    }

    public void setTrabajador(String trabajador) {
        this.trabajador = trabajador;
    }

    public String getN_documento() {
        return n_documento;
    }

    public void setN_documento(String n_documento) {
        this.n_documento = n_documento;
    }
}
