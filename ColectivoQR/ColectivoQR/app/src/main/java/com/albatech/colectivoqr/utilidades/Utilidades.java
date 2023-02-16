package com.albatech.colectivoqr.utilidades;

public class Utilidades {

    //Contantes de Campos de la tabla trabajadores
    public static final String TABLA_TABAJADORES = "trabajadores";
    public static final String CAMPO_USER_ID = "user_id";
    public static final String CAMPO_RUT = "rut";
    //////////////////////////////////////////////
    public static final String  CREAR_TABLE_TRAB="CREATE TABLE "+ TABLA_TABAJADORES +" ("+CAMPO_USER_ID+" INTEGER, "+CAMPO_RUT+" TEXT)";


    //Contantes de Campos de la tabla marcajes
    public static final String TABLA_MARCAJES = "marcajes";
    public static final String CAMPO_MARCACION = "marcacion";
    public static final String CAMPO_TIPO_MARCA = "tipo_marca";
    public static final String CAMPO_LATITUD = "latitud";
    public static final String CAMPO_LONGITUD = "longitud";
    public static final String CAMPO_TIPO_VERIFICACION = "tipo_verificacion";
    public static final String CAMPO_TRABAJADOR = "trabajador";
    public static final String CAMPO_N_DOCUMENTO = "n_documento";
    ///////////////////////////////////////////
    public static final String  CREAR_TABLE_MARCAJES="CREATE TABLE "+TABLA_MARCAJES+" (" +
            " "+CAMPO_MARCACION+" TEXT, "+CAMPO_TIPO_MARCA+" TEXT," +
            " "+CAMPO_LATITUD+","+CAMPO_LONGITUD+","+CAMPO_TIPO_VERIFICACION+"," +
            " "+CAMPO_TRABAJADOR+","+CAMPO_N_DOCUMENTO+")";

}
