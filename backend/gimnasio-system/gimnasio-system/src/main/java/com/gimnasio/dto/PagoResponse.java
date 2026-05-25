package com.gimnasio.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PagoResponse {
    private Long id;
    private ClienteResumenResponse cliente;
    private Long membresiaId;
    private String planNombre;
    private BigDecimal monto;
    private LocalDateTime fechaPago;
    private String metodoPago;
    private String estado;
    private String referencia;
    private String observaciones;
    private LocalDateTime createdAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public ClienteResumenResponse getCliente() { return cliente; }
    public void setCliente(ClienteResumenResponse cliente) { this.cliente = cliente; }
    public Long getMembresiaId() { return membresiaId; }
    public void setMembresiaId(Long membresiaId) { this.membresiaId = membresiaId; }
    public String getPlanNombre() { return planNombre; }
    public void setPlanNombre(String planNombre) { this.planNombre = planNombre; }
    public BigDecimal getMonto() { return monto; }
    public void setMonto(BigDecimal monto) { this.monto = monto; }
    public LocalDateTime getFechaPago() { return fechaPago; }
    public void setFechaPago(LocalDateTime fechaPago) { this.fechaPago = fechaPago; }
    public String getMetodoPago() { return metodoPago; }
    public void setMetodoPago(String metodoPago) { this.metodoPago = metodoPago; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public String getReferencia() { return referencia; }
    public void setReferencia(String referencia) { this.referencia = referencia; }
    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
