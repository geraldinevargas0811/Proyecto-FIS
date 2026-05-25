package com.gimnasio.dto;

import java.time.LocalDate;

public class MembresiaResumenResponse {
    private Long id;
    private String planNombre;
    private String estado;
    private LocalDate fechaInicio;
    private LocalDate fechaVencimiento;
    private long diasRestantes;
    private boolean vigente;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getPlanNombre() { return planNombre; }
    public void setPlanNombre(String planNombre) { this.planNombre = planNombre; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }
    public LocalDate getFechaVencimiento() { return fechaVencimiento; }
    public void setFechaVencimiento(LocalDate fechaVencimiento) { this.fechaVencimiento = fechaVencimiento; }
    public long getDiasRestantes() { return diasRestantes; }
    public void setDiasRestantes(long diasRestantes) { this.diasRestantes = diasRestantes; }
    public boolean isVigente() { return vigente; }
    public void setVigente(boolean vigente) { this.vigente = vigente; }
}
