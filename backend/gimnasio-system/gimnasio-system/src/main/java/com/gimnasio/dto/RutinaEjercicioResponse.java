package com.gimnasio.dto;

public class RutinaEjercicioResponse {
    private Long id;
    private EjercicioResponse ejercicio;
    private String diaSemana;
    private Integer series;
    private Integer repeticiones;
    private Integer descansoSegundos;
    private Integer ordenEjercicio;
    private String notas;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public EjercicioResponse getEjercicio() { return ejercicio; }
    public void setEjercicio(EjercicioResponse ejercicio) { this.ejercicio = ejercicio; }
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
