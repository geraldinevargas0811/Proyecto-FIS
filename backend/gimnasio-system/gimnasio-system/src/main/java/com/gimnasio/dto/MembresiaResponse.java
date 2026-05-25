package com.gimnasio.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class MembresiaResponse {
    private Long id;
    private ClienteResumenResponse cliente;
    private PlanResponse plan;
    private LocalDate fechaInicio;
    private LocalDate fechaVencimiento;
    private String estado;
    private boolean renovacionAutomatica;
    private boolean activo;
    private long diasRestantes;
    private boolean vigente;
    private LocalDateTime createdAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public ClienteResumenResponse getCliente() { return cliente; }
    public void setCliente(ClienteResumenResponse cliente) { this.cliente = cliente; }
    public PlanResponse getPlan() { return plan; }
    public void setPlan(PlanResponse plan) { this.plan = plan; }
    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }
    public LocalDate getFechaVencimiento() { return fechaVencimiento; }
    public void setFechaVencimiento(LocalDate fechaVencimiento) { this.fechaVencimiento = fechaVencimiento; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public boolean isRenovacionAutomatica() { return renovacionAutomatica; }
    public void setRenovacionAutomatica(boolean renovacionAutomatica) { this.renovacionAutomatica = renovacionAutomatica; }
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
    public long getDiasRestantes() { return diasRestantes; }
    public void setDiasRestantes(long diasRestantes) { this.diasRestantes = diasRestantes; }
    public boolean isVigente() { return vigente; }
    public void setVigente(boolean vigente) { this.vigente = vigente; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
