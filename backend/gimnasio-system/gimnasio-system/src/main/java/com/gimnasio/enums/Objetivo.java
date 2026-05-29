package com.gimnasio.enums;


public enum Objetivo {
    AUMENTAR_MASA("Aumentar Masa Muscular"),
    DEFINICION("Definición Muscular"),
    PERDER_PESO("Perder Peso"),
    RECOMPOSICION("Recomposicion corporal"),
    MANTENIMIENTO("Mantenimiento");

    private final String descripcion;

    Objetivo(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
