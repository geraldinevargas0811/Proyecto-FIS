package com.gimnasio.dto;

public class InstructorResumenResponse {
    private Long id;
    private String nombreCompleto;
    private String correo;
    private String especialidad;
    private boolean disponible;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }
    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }
    public String getEspecialidad() { return especialidad; }
    public void setEspecialidad(String especialidad) { this.especialidad = especialidad; }
    public boolean isDisponible() { return disponible; }
    public void setDisponible(boolean disponible) { this.disponible = disponible; }
}
