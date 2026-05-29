package com.gimnasio.dto;

import com.gimnasio.enums.Objetivo;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public class RegistroClienteDTO {

    @NotBlank(message = "El nombre es requerido")
    @Pattern(regexp = "^[A-Za-zÁÉÍÓÚáéíóúÑñ ]+$", message = "El nombre solo puede contener letras y espacios")
    private String nombre;

    @NotBlank(message = "El apellido es requerido")
    @Pattern(regexp = "^[A-Za-zÁÉÍÓÚáéíóúÑñ ]+$", message = "El apellido solo puede contener letras y espacios")
    private String apellido;

    @NotBlank(message = "El correo es requerido")
    @Email(message = "Ingrese un correo válido")
    private String correo;

    @NotBlank(message = "La contraseña es requerida")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String contrasena;

    @NotBlank(message = "El documento es requerido")
    @Pattern(regexp = "^[0-9]{5,20}$", message = "El documento debe contener entre 5 y 20 numeros")
    private String documento;

    @Pattern(regexp = "^$|^[0-9]{7,15}$", message = "El telefono debe contener entre 7 y 15 numeros")
    private String telefono;

    @DecimalMin(value = "20.0", message = "El peso mínimo es 20 kg")
    @DecimalMax(value = "300.0", message = "El peso máximo es 300 kg")
    private BigDecimal peso;

    @DecimalMin(value = "1.0", message = "La altura mínima es 1.0 m")
    @DecimalMax(value = "2.5", message = "La altura máxima es 2.5 m")
    private BigDecimal altura;

    @NotNull(message = "El objetivo es requerido")
    private Objetivo objetivo;

    private String frecuenciaEntrenamiento;

    private LocalDate fechaNacimiento;

    private String genero;

    private boolean quiereInstructor = false;

    private Long instructorId;

    private Long planId;

    // Constructores
    public RegistroClienteDTO() {}

    // Getters y Setters
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getContrasena() { return contrasena; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }

    public String getDocumento() { return documento; }
    public void setDocumento(String documento) { this.documento = documento; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public BigDecimal getPeso() { return peso; }
    public void setPeso(BigDecimal peso) { this.peso = peso; }

    public BigDecimal getAltura() { return altura; }
    public void setAltura(BigDecimal altura) { this.altura = altura; }

    public Objetivo getObjetivo() { return objetivo; }
    public void setObjetivo(Objetivo objetivo) { this.objetivo = objetivo; }

    public String getFrecuenciaEntrenamiento() { return frecuenciaEntrenamiento; }
    public void setFrecuenciaEntrenamiento(String frecuenciaEntrenamiento) { this.frecuenciaEntrenamiento = frecuenciaEntrenamiento; }

    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(LocalDate fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }

    public String getGenero() { return genero; }
    public void setGenero(String genero) { this.genero = genero; }

    public boolean isQuiereInstructor() { return quiereInstructor; }
    public void setQuiereInstructor(boolean quiereInstructor) { this.quiereInstructor = quiereInstructor; }

    public Long getInstructorId() { return instructorId; }
    public void setInstructorId(Long instructorId) { this.instructorId = instructorId; }

    public Long getPlanId() { return planId; }
    public void setPlanId(Long planId) { this.planId = planId; }
}
