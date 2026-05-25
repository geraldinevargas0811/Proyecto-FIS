package com.gimnasio.dto;

import java.time.LocalDateTime;
import java.util.List;

public class RutinaResponse {
    private Long id;
    private Long clienteId;
    private String objetivo;
    private String nivelDificultad;
    private Integer frecuenciaSemanal;
    private LocalDateTime fechaGeneracion;
    private boolean activa;
    private boolean generatedBySystem;
    private List<RutinaEjercicioResponse> ejercicios;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getClienteId() { return clienteId; }
    public void setClienteId(Long clienteId) { this.clienteId = clienteId; }
    public String getObjetivo() { return objetivo; }
    public void setObjetivo(String objetivo) { this.objetivo = objetivo; }
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
    public List<RutinaEjercicioResponse> getEjercicios() { return ejercicios; }
    public void setEjercicios(List<RutinaEjercicioResponse> ejercicios) { this.ejercicios = ejercicios; }
}
