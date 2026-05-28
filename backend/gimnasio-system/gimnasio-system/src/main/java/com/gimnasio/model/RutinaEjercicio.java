package com.gimnasio.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

@Entity
@Table(name = "routine_exercises")
public class RutinaEjercicio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "routine_id", nullable = false)
    @JsonIgnoreProperties({"rutinaEjercicios", "cliente"})
    private Rutina rutina;

    @ManyToOne
    @JoinColumn(name = "exercise_id", nullable = false)
    private Ejercicio ejercicio;

    @Column(name = "dia_semana", length = 20)
    private String diaSemana;

    @Column(nullable = false)
    private Integer series;

    @Column(nullable = false)
    private Integer repeticiones;

    @Column(name = "descanso_segundos")
    private Integer descansoSegundos;

    @Column(name = "orden_ejercicio")
    private Integer ordenEjercicio;

    @Column(columnDefinition = "TEXT")
    private String notas;

    // Constructores
    public RutinaEjercicio() {}

    public RutinaEjercicio(Rutina rutina, Ejercicio ejercicio, Integer series, Integer repeticiones, Integer descansoSegundos) {
        this.rutina = rutina;
        this.ejercicio = ejercicio;
        this.series = series;
        this.repeticiones = repeticiones;
        this.descansoSegundos = descansoSegundos;
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Rutina getRutina() { return rutina; }
    public void setRutina(Rutina rutina) { this.rutina = rutina; }

    public Ejercicio getEjercicio() { return ejercicio; }
    public void setEjercicio(Ejercicio ejercicio) { this.ejercicio = ejercicio; }

    public String getDiaSemana() { return diaSemana; }
    public void setDiaSemana(String diaSemana) { this.diaSemana = diaSemana; }

    public Integer getSeries() { return series; }
    public void setSeries(Integer series) { this.series = series; }

    public Integer getRepeticiones() { return repeticiones; }
    public void setRepeticiones(Integer repeticiones) { this.repeticiones = repeticiones; }

    public Integer getDescansoSegundos() { return descansoSegundos; }
    public void setDescansoSegundos(Integer descansoSegundos) { this.descansoSegundos = descansoSegundos; }

    public Integer getOrdenEjercicio() { return ordenEjercicio; }
    public void setOrdenEjercicio(Integer ordenEjercicio) { this.ordenEjercicio = ordenEjercicio; }

    public String getNotas() { return notas; }
    public void setNotas(String notas) { this.notas = notas; }
}
