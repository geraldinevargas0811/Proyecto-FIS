package com.gimnasio.enums;

public enum EstadoPago {
    PAGADO("Pagado"),
    PENDIENTE("Pendiente"),
    ANULADO("Anulado");

    private final String descripcion;

    EstadoPago(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}