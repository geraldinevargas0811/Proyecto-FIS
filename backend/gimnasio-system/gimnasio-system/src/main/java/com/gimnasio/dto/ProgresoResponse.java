package com.gimnasio.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class ProgresoResponse {
    private Long id;
    private Long clienteId;
    private LocalDate fecha;
    private BigDecimal peso;
    private BigDecimal medidaCintura;
    private BigDecimal medidaCadera;
    private BigDecimal medidaPecho;
    private Integer rendimiento;
    private String observaciones;
    private LocalDateTime recordedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getClienteId() { return clienteId; }
    public void setClienteId(Long clienteId) { this.clienteId = clienteId; }
    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    public BigDecimal getPeso() { return peso; }
    public void setPeso(BigDecimal peso) { this.peso = peso; }
    public BigDecimal getMedidaCintura() { return medidaCintura; }
    public void setMedidaCintura(BigDecimal medidaCintura) { this.medidaCintura = medidaCintura; }
    public BigDecimal getMedidaCadera() { return medidaCadera; }
    public void setMedidaCadera(BigDecimal medidaCadera) { this.medidaCadera = medidaCadera; }
    public BigDecimal getMedidaPecho() { return medidaPecho; }
    public void setMedidaPecho(BigDecimal medidaPecho) { this.medidaPecho = medidaPecho; }
    public Integer getRendimiento() { return rendimiento; }
    public void setRendimiento(Integer rendimiento) { this.rendimiento = rendimiento; }
    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
    public LocalDateTime getRecordedAt() { return recordedAt; }
    public void setRecordedAt(LocalDateTime recordedAt) { this.recordedAt = recordedAt; }
}
