package com.gimnasio.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reports")
public class Reporte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String tipo;

    @Column(name = "fecha_generacion")
    private LocalDateTime fechaGeneracion;

    @Column(columnDefinition = "TEXT")
    private String contenido;

    @Column(length = 20)
    private String formato;

    @ManyToOne
    @JoinColumn(name = "generado_por")
    private Usuario generadoPor;

    @PrePersist
    public void prePersist() {
        this.fechaGeneracion = LocalDateTime.now();
    }

    // Constructores
    public Reporte() {}

    public Reporte(String tipo, String formato, Usuario generadoPor) {
        this.tipo = tipo;
        this.formato = formato;
        this.generadoPor = generadoPor;
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public LocalDateTime getFechaGeneracion() { return fechaGeneracion; }
    public void setFechaGeneracion(LocalDateTime fechaGeneracion) { this.fechaGeneracion = fechaGeneracion; }

    public String getContenido() { return contenido; }
    public void setContenido(String contenido) { this.contenido = contenido; }

    public String getFormato() { return formato; }
    public void setFormato(String formato) { this.formato = formato; }

    public Usuario getGeneradoPor() { return generadoPor; }
    public void setGeneradoPor(Usuario generadoPor) { this.generadoPor = generadoPor; }
}