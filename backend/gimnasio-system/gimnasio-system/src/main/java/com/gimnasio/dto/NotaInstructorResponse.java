package com.gimnasio.dto;

import java.time.LocalDateTime;

public class NotaInstructorResponse {
    private Long id;
    private InstructorResumenResponse instructor;
    private ClienteResumenResponse cliente;
    private String nota;
    private LocalDateTime fecha;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public InstructorResumenResponse getInstructor() { return instructor; }
    public void setInstructor(InstructorResumenResponse instructor) { this.instructor = instructor; }
    public ClienteResumenResponse getCliente() { return cliente; }
    public void setCliente(ClienteResumenResponse cliente) { this.cliente = cliente; }
    public String getNota() { return nota; }
    public void setNota(String nota) { this.nota = nota; }
    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
}
