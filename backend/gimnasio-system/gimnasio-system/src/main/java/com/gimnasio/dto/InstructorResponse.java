package com.gimnasio.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class InstructorResponse {
    private Long id;
    private String nombre;
    private String apellido;
    private String nombreCompleto;
    private String correo;
    private String documento;
    private String telefono;
    private boolean activo;
    private String status;
    private String especialidad;
    private String certificaciones;
    private Integer anosExperiencia;
    private boolean disponible;
    private String contractType;
    private BigDecimal salario;
    private LocalDate fechaContratacion;
    private String horarioTrabajo;
    private int cantidadClientes;

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
    public String getDocumento() { return documento; }
    public void setDocumento(String documento) { this.documento = documento; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getEspecialidad() { return especialidad; }
    public void setEspecialidad(String especialidad) { this.especialidad = especialidad; }
    public String getCertificaciones() { return certificaciones; }
    public void setCertificaciones(String certificaciones) { this.certificaciones = certificaciones; }
    public Integer getAnosExperiencia() { return anosExperiencia; }
    public void setAnosExperiencia(Integer anosExperiencia) { this.anosExperiencia = anosExperiencia; }
    public boolean isDisponible() { return disponible; }
    public void setDisponible(boolean disponible) { this.disponible = disponible; }
    public String getContractType() { return contractType; }
    public void setContractType(String contractType) { this.contractType = contractType; }
    public BigDecimal getSalario() { return salario; }
    public void setSalario(BigDecimal salario) { this.salario = salario; }
    public LocalDate getFechaContratacion() { return fechaContratacion; }
    public void setFechaContratacion(LocalDate fechaContratacion) { this.fechaContratacion = fechaContratacion; }
    public String getHorarioTrabajo() { return horarioTrabajo; }
    public void setHorarioTrabajo(String horarioTrabajo) { this.horarioTrabajo = horarioTrabajo; }
    public int getCantidadClientes() { return cantidadClientes; }
    public void setCantidadClientes(int cantidadClientes) { this.cantidadClientes = cantidadClientes; }
}
