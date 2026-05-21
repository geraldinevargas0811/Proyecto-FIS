package com.gimnasio.controller;

import com.gimnasio.dto.ProgresoDTO;
import com.gimnasio.enums.Objetivo;
import com.gimnasio.model.*;
import com.gimnasio.service.ClienteService;
import com.gimnasio.service.InstructorService;
import com.gimnasio.service.MembresiaService;
import com.gimnasio.service.SistemaGimnasioService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cliente")
public class ClienteController {

    private final ClienteService clienteService;
    private final MembresiaService membresiaService;
    private final InstructorService instructorService;
    private final SistemaGimnasioService sistemaService;

    public ClienteController(ClienteService clienteService,
                             MembresiaService membresiaService,
                             InstructorService instructorService,
                             SistemaGimnasioService sistemaService) {
        this.clienteService = clienteService;
        this.membresiaService = membresiaService;
        this.instructorService = instructorService;
        this.sistemaService = sistemaService;
    }

    @GetMapping("/{clienteId}/perfil")
    public ResponseEntity<Cliente> getPerfil(@PathVariable Long clienteId) {
        return clienteService.findById(clienteId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{clienteId}/perfil/evaluacion")
    public ResponseEntity<?> getEvaluacionObjetivo(@PathVariable Long clienteId) {
        Cliente cliente = clienteService.findById(clienteId).orElse(null);
        if (cliente == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(Map.of(
            "evaluacion", sistemaService.evaluarObjetivo(cliente.getObjetivo(), cliente.getImc())
        ));
    }

    @PutMapping("/{clienteId}/objetivo")
    public ResponseEntity<?> actualizarObjetivo(@PathVariable Long clienteId, @RequestBody Map<String, String> body) {
        try {
            Objetivo nuevoObjetivo = Objetivo.valueOf(body.get("objetivo"));
            Cliente cliente = clienteService.actualizarObjetivo(clienteId, nuevoObjetivo);
            return ResponseEntity.ok(Map.of(
                "cliente", cliente,
                "mensaje", "Objetivo actualizado y rutina regenerada"
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Objetivo inválido"));
        }
    }

    @GetMapping("/{clienteId}/rutina")
    public ResponseEntity<Rutina> getRutina(@PathVariable Long clienteId) {
        return clienteService.getRutinaActiva(clienteId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/rutina/ejercicio/{rutinaEjercicioId}")
    public ResponseEntity<?> modificarEjercicioRutina(@PathVariable Long rutinaEjercicioId,
                                                       @RequestBody Map<String, Object> body) {
        try {
            Integer series = body.get("series") != null ? (Integer) body.get("series") : null;
            Integer repeticiones = body.get("repeticiones") != null ? (Integer) body.get("repeticiones") : null;
            Integer descansoSegundos = body.get("descansoSegundos") != null ? (Integer) body.get("descansoSegundos") : null;
            Long nuevoEjercicioId = body.get("nuevoEjercicioId") != null ? Long.valueOf(body.get("nuevoEjercicioId").toString()) : null;
            String notas = (String) body.get("notas");

            RutinaEjercicio re = clienteService.modificarEjercicioRutina(rutinaEjercicioId, series, repeticiones, descansoSegundos, nuevoEjercicioId, notas);
            return ResponseEntity.ok(re);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{clienteId}/progreso")
    public ResponseEntity<?> registrarProgreso(@PathVariable Long clienteId, @Valid @RequestBody ProgresoDTO dto) {
        ProgresoFisico progreso = clienteService.registrarProgreso(clienteId, dto);
        return ResponseEntity.ok(progreso);
    }

    @GetMapping("/{clienteId}/progreso/historial")
    public ResponseEntity<List<ProgresoFisico>> getHistorialProgreso(@PathVariable Long clienteId) {
        return ResponseEntity.ok(clienteService.getHistorialProgreso(clienteId));
    }

    @GetMapping("/{clienteId}/membresia")
    public ResponseEntity<?> getMembresia(@PathVariable Long clienteId) {
        Cliente cliente = clienteService.findById(clienteId).orElse(null);
        if (cliente == null) {
            return ResponseEntity.notFound().build();
        }
        Membresia membresia = cliente.getMembresia();
        if (membresia == null) {
            return ResponseEntity.ok(Map.of("mensaje", "Sin membresía activa"));
        }
        return ResponseEntity.ok(Map.of(
            "membresia", membresia,
            "diasRestantes", membresia.getDiasRestantes(),
            "vigente", membresia.isVigente()
        ));
    }

    @GetMapping("/{clienteId}/instructor")
    public ResponseEntity<?> getInstructor(@PathVariable Long clienteId) {
        Cliente cliente = clienteService.findById(clienteId).orElse(null);
        if (cliente == null) {
            return ResponseEntity.notFound().build();
        }
        Instructor instructor = cliente.getInstructor();
        if (instructor == null) {
            return ResponseEntity.ok(Map.of("mensaje", "Sin instructor asignado"));
        }
        List<NotaInstructor> asesorias = instructorService.getAsesoriasPorCliente(clienteId);
        return ResponseEntity.ok(Map.of(
            "instructor", instructor,
            "asesorias", asesorias
        ));
    }

    @GetMapping("/ejercicios")
    public ResponseEntity<List<Ejercicio>> getEjerciciosDisponibles() {
        return ResponseEntity.ok(clienteService.getEjerciciosDisponibles());
    }

    @PutMapping("/{clienteId}/datos-fisicos")
    public ResponseEntity<?> actualizarDatosFisicos(@PathVariable Long clienteId, 
                                                     @RequestBody Map<String, Object> body) {
        java.math.BigDecimal peso = body.get("peso") != null ? 
            new java.math.BigDecimal(body.get("peso").toString()) : null;
        java.math.BigDecimal altura = body.get("altura") != null ? 
            new java.math.BigDecimal(body.get("altura").toString()) : null;
        
        Cliente cliente = clienteService.actualizarDatosFisicos(clienteId, peso, altura);
        return ResponseEntity.ok(Map.of(
            "cliente", cliente,
            "imc", cliente.getImc(),
            "categoria", cliente.getCategoriaPeso()
        ));
    }
}