package com.gimnasio.dto;

import com.gimnasio.model.Usuario;

public class UserResponse {

    private Long id;
    private String nombre;
    private String apellido;
    private String nombreCompleto;
    private String correo;
    private String rol;
    private boolean activo;
    private String status;

    public UserResponse() {}

    public UserResponse(Usuario usuario) {
        this.id = usuario.getId();
        this.nombre = usuario.getNombre();
        this.apellido = usuario.getApellido();
        this.nombreCompleto = usuario.getNombreCompleto();
        this.correo = usuario.getCorreo();
        this.rol = usuario.getRol().name();
        this.activo = usuario.isActivo();
        this.status = usuario.getStatus() != null ? usuario.getStatus().name() : null;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
