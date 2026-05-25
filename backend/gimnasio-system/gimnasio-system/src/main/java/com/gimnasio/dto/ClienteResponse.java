package com.gimnasio.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class ClienteResponse {
    private Long id;
    private String nombre;
    private String apellido;
    private String nombreCompleto;
    private String correo;
    private String documento;
    private String telefono;
    private boolean activo;
    private String status;
    private BigDecimal peso;
    private BigDecimal altura;
    private BigDecimal imc;
    private String categoriaPeso;
    private String objetivo;
    private String frecuenciaEntrenamiento;
    private LocalDate fechaNacimiento;
    private Integer edad;
    private String genero;
    private boolean quiereInstructor;
    private InstructorResumenResponse instructor;
    private MembresiaResumenResponse membresia;
    private LocalDateTime fechaRegistro;

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
    public BigDecimal getPeso() { return peso; }
    public void setPeso(BigDecimal peso) { this.peso = peso; }
    public BigDecimal getAltura() { return altura; }
    public void setAltura(BigDecimal altura) { this.altura = altura; }
    public BigDecimal getImc() { return imc; }
    public void setImc(BigDecimal imc) { this.imc = imc; }
    public String getCategoriaPeso() { return categoriaPeso; }
    public void setCategoriaPeso(String categoriaPeso) { this.categoriaPeso = categoriaPeso; }
    public String getObjetivo() { return objetivo; }
    public void setObjetivo(String objetivo) { this.objetivo = objetivo; }
    public String getFrecuenciaEntrenamiento() { return frecuenciaEntrenamiento; }
    public void setFrecuenciaEntrenamiento(String frecuenciaEntrenamiento) { this.frecuenciaEntrenamiento = frecuenciaEntrenamiento; }
    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(LocalDate fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }
    public Integer getEdad() { return edad; }
    public void setEdad(Integer edad) { this.edad = edad; }
    public String getGenero() { return genero; }
    public void setGenero(String genero) { this.genero = genero; }
    public boolean isQuiereInstructor() { return quiereInstructor; }
    public void setQuiereInstructor(boolean quiereInstructor) { this.quiereInstructor = quiereInstructor; }
    public InstructorResumenResponse getInstructor() { return instructor; }
    public void setInstructor(InstructorResumenResponse instructor) { this.instructor = instructor; }
    public MembresiaResumenResponse getMembresia() { return membresia; }
    public void setMembresia(MembresiaResumenResponse membresia) { this.membresia = membresia; }
    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDateTime fechaRegistro) { this.fechaRegistro = fechaRegistro; }
}
