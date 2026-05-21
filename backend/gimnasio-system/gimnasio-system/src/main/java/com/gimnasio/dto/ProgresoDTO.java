package com.gimnasio.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ProgresoDTO {

    @NotNull(message = "La fecha es requerida")
    private LocalDate fecha;

    @DecimalMin(value = "20.0", message = "El peso mínimo es 20 kg")
    @DecimalMax(value = "300.0", message = "El peso máximo es 300 kg")
    private BigDecimal peso;

    private BigDecimal medidaCintura;

    private BigDecimal medidaCadera;

    private BigDecimal medidaPecho;

    @Min(value = 1, message = "El rendimiento mínimo es 1")
    @Max(value = 100, message = "El rendimiento máximo es 100")
    private Integer rendimiento;

    private String observaciones;

    // Constructores
    public ProgresoDTO() {}

    // Getters y Setters
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
}