package com.gimnasio.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "physical_progress")
public class ProgresoFisico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    @JsonIgnoreProperties({"progresos", "membresia", "rutina", "notasInstructor", "instructor"})
    private Cliente cliente;

    @Column(nullable = false)
    private LocalDate fecha;

    private BigDecimal peso;

    @Column(name = "medida_cintura")
    private BigDecimal medidaCintura;

    @Column(name = "medida_cadera")
    private BigDecimal medidaCadera;

    @Column(name = "medida_pecho")
    private BigDecimal medidaPecho;

    private Integer rendimiento;

    @Column(columnDefinition = "TEXT")
    private String observaciones;

    @Column(name = "recorded_at")
    private LocalDateTime recordedAt;

    @PrePersist
    public void prePersist() {
        this.recordedAt = LocalDateTime.now();
        if (this.fecha == null) {
            this.fecha = LocalDate.now();
        }
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }

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
