package com.gimnasio.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.gimnasio.enums.Genero;
import com.gimnasio.enums.Objetivo;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "clients")
@PrimaryKeyJoinColumn(name = "user_id")
public class Cliente extends Usuario {

    private BigDecimal peso;
    private BigDecimal altura;
    private BigDecimal imc;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Objetivo objetivo;

    @Column(name = "frecuencia_entrenamiento", length = 50)
    private String frecuenciaEntrenamiento;

    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;

    @Enumerated(EnumType.STRING)
    private Genero genero;

    @ManyToOne
    @JoinColumn(name = "instructor_id")
    @JsonIgnoreProperties({"clientesAsignados"})
    private Instructor instructor;

    @Column(name = "quiere_instructor")
    private boolean quiereInstructor = false;

    private boolean active = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToOne(mappedBy = "cliente", cascade = CascadeType.ALL)
    @JsonIgnoreProperties({"cliente", "pagos"})
    private Membresia membresia;

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL)
    @OrderBy("fecha DESC")
    @JsonIgnoreProperties({"cliente"})
    private List<ProgresoFisico> progresos = new ArrayList<>();

    @OneToOne(mappedBy = "cliente", cascade = CascadeType.ALL)
    @JsonIgnoreProperties({"cliente"})
    private Rutina rutina;

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL)
    @OrderBy("fecha DESC")
    @JsonIgnoreProperties({"cliente", "instructor"})
    private List<NotaInstructor> notasInstructor = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Métodos de negocio
    public BigDecimal calcularIMC() {
        if (altura != null && altura.compareTo(BigDecimal.ZERO) > 0 && peso != null) {
            return peso.divide(altura.multiply(altura), 2, RoundingMode.HALF_UP);
        }
        return BigDecimal.ZERO;
    }

    public void actualizarDatosFisicos(BigDecimal peso, BigDecimal altura) {
        this.peso = peso;
        this.altura = altura;
        this.imc = calcularIMC();
    }

    public boolean tieneMembresiaActiva() {
        return membresia != null && membresia.isVigente();
    }

    public int getEdad() {
        if (fechaNacimiento == null) return 0;
        return Period.between(fechaNacimiento, LocalDate.now()).getYears();
    }

    public String getCategoriaPeso() {
        if (imc == null) return "Sin datos";
        double val = imc.doubleValue();
        if (val < 18.5) return "Bajo peso";
        if (val < 25.0) return "Peso normal";
        if (val < 30.0) return "Sobrepeso";
        return "Obesidad";
    }

    // Getters y Setters
    public BigDecimal getPeso() { return peso; }
    public void setPeso(BigDecimal peso) { this.peso = peso; }

    public BigDecimal getAltura() { return altura; }
    public void setAltura(BigDecimal altura) { this.altura = altura; }

    public BigDecimal getImc() { return imc; }
    public void setImc(BigDecimal imc) { this.imc = imc; }

    public Objetivo getObjetivo() { return objetivo; }
    public void setObjetivo(Objetivo objetivo) { this.objetivo = objetivo; }

    public String getFrecuenciaEntrenamiento() { return frecuenciaEntrenamiento; }
    public void setFrecuenciaEntrenamiento(String frecuenciaEntrenamiento) { this.frecuenciaEntrenamiento = frecuenciaEntrenamiento; }

    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(LocalDate fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }

    public Genero getGenero() { return genero; }
    public void setGenero(Genero genero) { this.genero = genero; }

    public Instructor getInstructor() { return instructor; }
    public void setInstructor(Instructor instructor) { this.instructor = instructor; }

    public boolean isQuiereInstructor() { return quiereInstructor; }
    public void setQuiereInstructor(boolean quiereInstructor) { this.quiereInstructor = quiereInstructor; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public Membresia getMembresia() { return membresia; }
    public void setMembresia(Membresia membresia) { this.membresia = membresia; }

    public List<ProgresoFisico> getProgresos() { return progresos; }
    public void setProgresos(List<ProgresoFisico> progresos) { this.progresos = progresos; }

    public Rutina getRutina() { return rutina; }
    public void setRutina(Rutina rutina) { this.rutina = rutina; }

    public List<NotaInstructor> getNotasInstructor() { return notasInstructor; }
    public void setNotasInstructor(List<NotaInstructor> notasInstructor) { this.notasInstructor = notasInstructor; }
}
