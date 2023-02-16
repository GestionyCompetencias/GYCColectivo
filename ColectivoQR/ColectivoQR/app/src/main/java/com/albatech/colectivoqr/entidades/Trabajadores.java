package com.albatech.colectivoqr.entidades;

public class Trabajadores {

    private Integer user_id;
    private String rut;

    public Trabajadores(Integer user_id, String rut) {
        this.user_id = user_id;
        this.rut = rut;
    }

    public Integer getUser_id() {
        return user_id;
    }

    public void setUser_id(Integer user_id) {
        this.user_id = user_id;
    }

    public String getRut() {
        return rut;
    }

    public void setRut(String rut) {
        this.rut = rut;
    }

}
