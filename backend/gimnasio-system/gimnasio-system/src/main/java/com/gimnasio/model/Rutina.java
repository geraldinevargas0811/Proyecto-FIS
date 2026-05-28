package com.gimnasio.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.gimnasio.enums.Objetivo;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "routines")
public class Rutina {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "client_id", nullable = false)
    @JsonIgnoreProperties({"rutina", "membresia", "progresos", "notasInstructor", "instructor"})
    private Cliente cliente;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Objetivo objetivo;

    @Column(name = "nivel_dificultad", length = 50)
    private String nivelDificultad = "INTERMEDIO";

    @Column(name = "frecuencia_semanal")
    private Integer frecuenciaSemanal = 3;

    @Column(name = "fecha_generacion")
    private LocalDateTime fechaGeneracion;

    private boolean activa = true;

    @Column(name = "generated_by_system")
    private boolean generatedBySystem = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "rutina", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("ordenEjercicio ASC")
    @JsonIgnoreProperties({"rutina"})
    private List<RutinaEjercicio> rutinaEjercicios = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        this.fechaGeneracion = LocalDateTime.now();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void agregarEjercicio(RutinaEjercicio re) {
        rutinaEjercicios.add(re);
        re.setRutina(this);
    }

    public void limpiarEjercicios() {
        rutinaEjercicios.clear();
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }

    public Objetivo getObjetivo() { return objetivo; }
    public void setObjetivo(Objetivo objetivo) { this.objetivo = objetivo; }

    public String getNivelDificultad() { return nivelDificultad; }
    public void setNivelDificultad(String nivelDificultad) { this.nivelDificultad = nivelDificultad; }

    public Integer getFrecuenciaSemanal() { return frecuenciaSemanal; }
    public void setFrecuenciaSemanal(Integer frecuenciaSemanal) { this.frecuenciaSemanal = frecuenciaSemanal; }

    public LocalDateTime getFechaGeneracion() { return fechaGeneracion; }
    public void setFechaGeneracion(LocalDateTime fechaGeneracion) { this.fechaGeneracion = fechaGeneracion; }

    public boolean isActiva() { return activa; }
    public void setActiva(boolean activa) { this.activa = activa; }

    public boolean isGeneratedBySystem() { return generatedBySystem; }
    public void setGeneratedBySystem(boolean generatedBySystem) { this.generatedBySystem = generatedBySystem; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public List<RutinaEjercicio> getRutinaEjercicios() { return rutinaEjercicios; }
    public void setRutinaEjercicios(List<RutinaEjercicio> rutinaEjercicios) { this.rutinaEjercicios = rutinaEjercicios; }
}
