package com.gimnasio.dto;

import jakarta.validation.constraints.NotNull;

public class AsignarInstructorDTO {

    @NotNull(message = "El cliente es requerido")
    private Long clienteId;

    private Long instructorId; // null para remover instructor

    // Constructores
    public AsignarInstructorDTO() {}

    public AsignarInstructorDTO(Long clienteId, Long instructorId) {
        this.clienteId = clienteId;
        this.instructorId = instructorId;
    }

    // Getters y Setters
    public Long getClienteId() { return clienteId; }
    public void setClienteId(Long clienteId) { this.clienteId = clienteId; }

    public Long getInstructorId() { return instructorId; }
    public void setInstructorId(Long instructorId) { this.instructorId = instructorId; }
}