package com.gimnasio.dto;

public class EjercicioResponse {
    private Long id;
    private String nombre;
    private String descripcion;
    private String grupoMuscular;
    private Integer series;
    private Integer repeticiones;
    private Integer descansoSegundos;
    private String equipamiento;
    private String videoUrl;
    private boolean activo;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public String getGrupoMuscular() { return grupoMuscular; }
    public void setGrupoMuscular(String grupoMuscular) { this.grupoMuscular = grupoMuscular; }
    public Integer getSeries() { return series; }
    public void setSeries(Integer series) { this.series = series; }
    public Integer getRepeticiones() { return repeticiones; }
    public void setRepeticiones(Integer repeticiones) { this.repeticiones = repeticiones; }
    public Integer getDescansoSegundos() { return descansoSegundos; }
    public void setDescansoSegundos(Integer descansoSegundos) { this.descansoSegundos = descansoSegundos; }
    public String getEquipamiento() { return equipamiento; }
    public void setEquipamiento(String equipamiento) { this.equipamiento = equipamiento; }
    public String getVideoUrl() { return videoUrl; }
    public void setVideoUrl(String videoUrl) { this.videoUrl = videoUrl; }
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
}
