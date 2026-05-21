package com.gimnasio.controller;

import com.gimnasio.model.*;
import com.gimnasio.service.InstructorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/instructor")
public class InstructorController {

    private final InstructorService instructorService;

    public InstructorController(InstructorService instructorService) {
        this.instructorService = instructorService;
    }

    @GetMapping("/{instructorId}/clientes")
    public ResponseEntity<List<Cliente>> getClientes(@PathVariable Long instructorId) {
        return ResponseEntity.ok(instructorService.consultarClientes(instructorId));
    }

    @GetMapping("/{instructorId}/clientes/{clienteId}")
    public ResponseEntity<Cliente> getCliente(@PathVariable Long instructorId, @PathVariable Long clienteId) {
        Cliente cliente = instructorService.findClienteById(clienteId).orElse(null);
        if (cliente == null || cliente.getInstructor() == null || !cliente.getInstructor().getId().equals(instructorId)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(cliente);
    }

    @GetMapping("/{instructorId}/clientes/{clienteId}/rutina")
    public ResponseEntity<Rutina> getRutinaCliente(@PathVariable Long instructorId, @PathVariable Long clienteId) {
        Cliente cliente = instructorService.findClienteById(clienteId).orElse(null);
        if (cliente == null || cliente.getInstructor() == null || !cliente.getInstructor().getId().equals(instructorId)) {
            return ResponseEntity.notFound().build();
        }
        return instructorService.consultarRutinaCliente(clienteId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{instructorId}/clientes/{clienteId}/progreso")
    public ResponseEntity<List<ProgresoFisico>> getProgresoCliente(@PathVariable Long instructorId, @PathVariable Long clienteId) {
        Cliente cliente = instructorService.findClienteById(clienteId).orElse(null);
        if (cliente == null || cliente.getInstructor() == null || !cliente.getInstructor().getId().equals(instructorId)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(instructorService.consultarProgresoCliente(clienteId));
    }

    @PostMapping("/{instructorId}/clientes/{clienteId}/asesoria")
    public ResponseEntity<?> brindarAsesoria(@PathVariable Long instructorId, 
                                              @PathVariable Long clienteId, 
                                              @RequestBody Map<String, String> body) {
        try {
            NotaInstructor nota = instructorService.brindarAsesoria(instructorId, clienteId, body.get("nota"));
            return ResponseEntity.ok(nota);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{instructorId}/clientes/{clienteId}/asesorias")
    public ResponseEntity<List<NotaInstructor>> getAsesorias(@PathVariable Long instructorId, @PathVariable Long clienteId) {
        return ResponseEntity.ok(instructorService.getAsesoriasPropias(instructorId, clienteId));
    }
}