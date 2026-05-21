package com.gimnasio.controller;

import com.gimnasio.dto.RegistroClienteDTO;
import com.gimnasio.dto.RegistroInstructorDTO;
import com.gimnasio.model.*;
import com.gimnasio.service.*;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdministradorService adminService;
    private final MembresiaService membresiaService;
    private final PagoService pagoService;
    private final ReporteService reporteService;
    private final ClienteService clienteService;

    public AdminController(AdministradorService adminService,
                           MembresiaService membresiaService,
                           PagoService pagoService,
                           ReporteService reporteService,
                           ClienteService clienteService) {
        this.adminService = adminService;
        this.membresiaService = membresiaService;
        this.pagoService = pagoService;
        this.reporteService = reporteService;
        this.clienteService = clienteService;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<?> getDashboardStats() {
        return ResponseEntity.ok(Map.of(
            "totalClientes", adminService.listarClientes().size(),
            "totalInstructores", adminService.listarInstructores().size(),
            "totalPagosPendientes", pagoService.getPagosPendientes().size(),
            "totalMembresiasActivas", membresiaService.getMembresiasPorEstado(com.gimnasio.enums.EstadoMembresia.ACTIVA).size()
        ));
    }

    @GetMapping("/clientes")
    public ResponseEntity<List<Cliente>> listarClientes() {
        return ResponseEntity.ok(adminService.listarClientes());
    }

    @GetMapping("/clientes/{id}")
    public ResponseEntity<Cliente> getCliente(@PathVariable Long id) {
        return adminService.buscarClientePorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/clientes")
    public ResponseEntity<?> registrarCliente(@Valid @RequestBody RegistroClienteDTO dto) {
        try {
            Cliente cliente = adminService.registrarCliente(dto);
            return ResponseEntity.ok(cliente);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/clientes/{id}")
    public ResponseEntity<?> desactivarCliente(@PathVariable Long id) {
        adminService.desactivarUsuario(id);
        return ResponseEntity.ok(Map.of("mensaje", "Cliente desactivado"));
    }

    @GetMapping("/instructores")
    public ResponseEntity<List<Instructor>> listarInstructores() {
        return ResponseEntity.ok(adminService.listarInstructores());
    }

    @GetMapping("/instructores/disponibles")
    public ResponseEntity<List<Instructor>> listarInstructoresDisponibles() {
        return ResponseEntity.ok(adminService.listarInstructoresDisponibles());
    }

    @PostMapping("/instructores")
    public ResponseEntity<?> registrarInstructor(@Valid @RequestBody RegistroInstructorDTO dto) {
        try {
            Instructor instructor = adminService.registrarInstructor(dto);
            return ResponseEntity.ok(instructor);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/instructores/{id}")
    public ResponseEntity<?> desactivarInstructor(@PathVariable Long id) {
        adminService.desactivarUsuario(id);
        return ResponseEntity.ok(Map.of("mensaje", "Instructor desactivado"));
    }

    @PostMapping("/clientes/{id}/asignar-instructor")
    public ResponseEntity<?> asignarInstructor(@PathVariable Long id, @RequestBody Map<String, Long> body) {
        Cliente cliente = adminService.asignarInstructor(id, body.get("instructorId"));
        return ResponseEntity.ok(cliente);
    }

    @GetMapping("/pagos")
    public ResponseEntity<List<Pago>> listarPagos() {
        return ResponseEntity.ok(pagoService.getTodosPagos());
    }

    @GetMapping("/pagos/pendientes")
    public ResponseEntity<List<Pago>> listarPagosPendientes() {
        return ResponseEntity.ok(pagoService.getPagosPendientes());
    }

    @PostMapping("/pagos/{id}/validar")
    public ResponseEntity<?> validarPago(@PathVariable Long id) {
        pagoService.validarPagoMembresia(id);
        return ResponseEntity.ok(Map.of("mensaje", "Pago validado y membresía activada"));
    }

    @PostMapping("/pagos/{id}/anular")
    public ResponseEntity<?> anularPago(@PathVariable Long id) {
        pagoService.anularPago(id);
        return ResponseEntity.ok(Map.of("mensaje", "Pago anulado"));
    }

    @GetMapping("/membresias")
    public ResponseEntity<?> listarMembresias() {
        return ResponseEntity.ok(Map.of(
            "activas", membresiaService.getMembresiasPorEstado(com.gimnasio.enums.EstadoMembresia.ACTIVA),
            "pendientes", membresiaService.getMembresiasPorEstado(com.gimnasio.enums.EstadoMembresia.PENDIENTE_PAGO),
            "vencidas", membresiaService.getMembresiasPorEstado(com.gimnasio.enums.EstadoMembresia.VENCIDA)
        ));
    }

    @PostMapping("/membresias/{id}/suspender")
    public ResponseEntity<?> suspenderMembresia(@PathVariable Long id) {
        membresiaService.suspenderMembresia(id);
        return ResponseEntity.ok(Map.of("mensaje", "Membresía suspendida"));
    }

    @GetMapping("/reportes/cliente/{id}/pdf")
    public ResponseEntity<byte[]> reporteClientePDF(@PathVariable Long id) {
        try {
            byte[] pdf = reporteService.generarReporteClientePDF(id);
            return ResponseEntity.ok()
                    .header("Content-Type", "application/pdf")
                    .header("Content-Disposition", "attachment; filename=reporte_cliente_" + id + ".pdf")
                    .body(pdf);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/reportes/clientes/csv")
    public ResponseEntity<byte[]> reporteClientesCSV() {
        try {
            byte[] csv = reporteService.generarReporteTodosClientesCSV();
            return ResponseEntity.ok()
                    .header("Content-Type", "text/csv; charset=UTF-8")
                    .header("Content-Disposition", "attachment; filename=clientes.csv")
                    .body(csv);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/planes")
    public ResponseEntity<List<Plan>> listarPlanes() {
        return ResponseEntity.ok(membresiaService.getPlanes());
    }

    @PostMapping("/membresias/crear")
    public ResponseEntity<?> crearMembresia(@RequestBody Map<String, Long> body) {
        Membresia membresia = membresiaService.crearMembresia(body.get("clienteId"), body.get("planId"));
        return ResponseEntity.ok(membresia);
    }
}