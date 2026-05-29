package com.gimnasio.enums;

public enum MetodoPago {
    CASH("Efectivo"),
    CARD("Tarjeta"),
    TRANSFER("Transferencia"),
    NEQUI("Nequi"),
    DAVIPLATA("Daviplata");

    private final String descripcion;

    MetodoPago(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
