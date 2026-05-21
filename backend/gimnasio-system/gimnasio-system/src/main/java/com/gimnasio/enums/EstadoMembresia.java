package com.gimnasio.enums;

public enum EstadoMembresia {
    ACTIVA("Activa"),
    VENCIDA("Vencida"),
    SUSPENDIDA("Suspendida"),
    PENDIENTE_PAGO("Pendiente de Pago");

    private final String descripcion;

    EstadoMembresia(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
