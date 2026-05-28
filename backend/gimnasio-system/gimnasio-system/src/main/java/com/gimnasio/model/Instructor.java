package com.gimnasio.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.gimnasio.enums.ContractType;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "instructors")
@PrimaryKeyJoinColumn(name = "user_id")
public class Instructor extends Usuario {

    @Column(length = 100)
    private String especialidad;

    @Column(columnDefinition = "TEXT")
    private String certificaciones;

    @Column(name = "anos_experiencia")
    private Integer anosExperiencia = 0;

    private boolean disponible = true;

    @Enumerated(EnumType.STRING)
    @Column(name = "contract_type")
    private ContractType contractType = ContractType.PART_TIME;

    private BigDecimal salario;

    @Column(name = "fecha_contratacion")
    private LocalDate fechaContratacion;

    @Column(name = "horario_trabajo", columnDefinition = "TEXT")
    private String horarioTrabajo;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "instructor")
    @JsonIgnoreProperties({"instructor", "membresia", "rutina", "progresos", "notasInstructor"})
    private List<Cliente> clientesAsignados = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Getters y Setters
    public String getEspecialidad() { return especialidad; }
    public void setEspecialidad(String especialidad) { this.especialidad = especialidad; }

    public String getCertificaciones() { return certificaciones; }
    public void setCertificaciones(String certificaciones) { this.certificaciones = certificaciones; }

    public Integer getAnosExperiencia() { return anosExperiencia; }
    public void setAnosExperiencia(Integer anosExperiencia) { this.anosExperiencia = anosExperiencia; }

    public boolean isDisponible() { return disponible; }
    public void setDisponible(boolean disponible) { this.disponible = disponible; }

    public ContractType getContractType() { return contractType; }
    public void setContractType(ContractType contractType) { this.contractType = contractType; }

    public BigDecimal getSalario() { return salario; }
    public void setSalario(BigDecimal salario) { this.salario = salario; }

    public LocalDate getFechaContratacion() { return fechaContratacion; }
    public void setFechaContratacion(LocalDate fechaContratacion) { this.fechaContratacion = fechaContratacion; }

    public String getHorarioTrabajo() { return horarioTrabajo; }
    public void setHorarioTrabajo(String horarioTrabajo) { this.horarioTrabajo = horarioTrabajo; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public List<Cliente> getClientesAsignados() { return clientesAsignados; }
    public void setClientesAsignados(List<Cliente> clientesAsignados) { this.clientesAsignados = clientesAsignados; }

    public int getCantidadClientes() {
        return clientesAsignados != null ? clientesAsignados.size() : 0;
    }
}
