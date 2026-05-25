package com.gimnasio.dto;

public class ClienteResumenResponse {
    private Long id;
    private String nombreCompleto;
    private String correo;
    private String documento;
    private String objetivo;
    private boolean activo;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }
    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }
    public String getDocumento() { return documento; }
    public void setDocumento(String documento) { this.documento = documento; }
    public String getObjetivo() { return objetivo; }
    public void setObjetivo(String objetivo) { this.objetivo = objetivo; }
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
}
