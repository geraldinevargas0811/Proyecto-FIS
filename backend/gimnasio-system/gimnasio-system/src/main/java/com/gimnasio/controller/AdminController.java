package com.gimnasio.controller;

import com.gimnasio.dto.RegistroClienteDTO;
import com.gimnasio.dto.RegistroInstructorDTO;
import com.gimnasio.enums.EstadoMembresia;
import com.gimnasio.enums.MetodoPago;
import com.gimnasio.model.*;
import com.gimnasio.service.*;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdministradorService adminService;
    private final MembresiaService membresiaService;
    private final PagoService pagoService;
    private final ReporteService reporteService;

    public AdminController(AdministradorService adminService,
                           MembresiaService membresiaService,
                           PagoService pagoService,
                           ReporteService reporteService,
                           ClienteService clienteService) {
        this.adminService = adminService;
        this.membresiaService = membresiaService;
        this.pagoService = pagoService;
        this.reporteService = reporteService;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<?> getDashboardStats() {
        List<Pago> pagos = pagoService.getTodosPagos();
        BigDecimal ingresosMes = pagos.stream()
                .filter(p -> p.getEstado() == com.gimnasio.enums.EstadoPago.PAGADO)
                .filter(p -> p.getFechaPago() != null && p.getFechaPago().getMonth() == java.time.LocalDate.now().getMonth())
                .map(Pago::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return ResponseEntity.ok(Map.of(
            "totalClientes", adminService.listarClientes().size(),
            "totalInstructores", adminService.listarInstructores().size(),
            "totalPagosPendientes", pagoService.getPagosPendientes().size(),
            "totalMembresiasActivas", membresiaService.getMembresiasPorEstado(EstadoMembresia.ACTIVA).size(),
            "ingresosMes", ingresosMes
        ));
    }

    @GetMapping("/clientes")
    public ResponseEntity<List<Map<String, Object>>> listarClientes() {
        return ResponseEntity.ok(adminService.listarClientes().stream().map(this::clienteResponse).toList());
    }

    @GetMapping("/clientes/{id}")
    public ResponseEntity<Map<String, Object>> getCliente(@PathVariable Long id) {
        return adminService.buscarClientePorId(id)
                .map(cliente -> ResponseEntity.ok(clienteResponse(cliente)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/clientes")
    public ResponseEntity<?> registrarCliente(@Valid @RequestBody RegistroClienteDTO dto) {
        try {
            return ResponseEntity.ok(clienteResponse(adminService.registrarCliente(dto)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/clientes/{id}")
    public ResponseEntity<?> actualizarCliente(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        try {
            return ResponseEntity.ok(clienteResponse(adminService.actualizarCliente(id, body)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/clientes/{id}")
    public ResponseEntity<?> desactivarCliente(@PathVariable Long id) {
        adminService.desactivarUsuario(id);
        return ResponseEntity.ok(Map.of("mensaje", "Cliente desactivado"));
    }

    @PostMapping("/clientes/{id}/asignar-instructor")
    public ResponseEntity<?> asignarInstructor(@PathVariable Long id, @RequestBody Map<String, Long> body) {
        return ResponseEntity.ok(clienteResponse(adminService.asignarInstructor(id, body.get("instructorId"))));
    }

    @GetMapping("/instructores")
    public ResponseEntity<List<Map<String, Object>>> listarInstructores() {
        return ResponseEntity.ok(adminService.listarInstructores().stream().map(this::instructorResponse).toList());
    }

    @GetMapping("/instructores/{id}")
    public ResponseEntity<Map<String, Object>> getInstructor(@PathVariable Long id) {
        return adminService.buscarInstructorPorId(id)
                .map(instructor -> ResponseEntity.ok(instructorResponse(instructor)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/instructores/disponibles")
    public ResponseEntity<List<Map<String, Object>>> listarInstructoresDisponibles() {
        return ResponseEntity.ok(adminService.listarInstructoresDisponibles().stream().map(this::instructorResponse).toList());
    }

    @PostMapping("/instructores")
    public ResponseEntity<?> registrarInstructor(@Valid @RequestBody RegistroInstructorDTO dto) {
        try {
            return ResponseEntity.ok(instructorResponse(adminService.registrarInstructor(dto)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/instructores/{id}")
    public ResponseEntity<?> actualizarInstructor(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        try {
            return ResponseEntity.ok(instructorResponse(adminService.actualizarInstructor(id, body)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/instructores/{id}")
    public ResponseEntity<?> desactivarInstructor(@PathVariable Long id) {
        adminService.desactivarUsuario(id);
        return ResponseEntity.ok(Map.of("mensaje", "Instructor desactivado"));
    }

    @GetMapping("/pagos")
    public ResponseEntity<List<Map<String, Object>>> listarPagos() {
        return ResponseEntity.ok(pagoService.getTodosPagos().stream().map(this::pagoResponse).toList());
    }

    @GetMapping("/pagos/pendientes")
    public ResponseEntity<List<Map<String, Object>>> listarPagosPendientes() {
        return ResponseEntity.ok(pagoService.getPagosPendientes().stream().map(this::pagoResponse).toList());
    }

    @PostMapping("/pagos")
    public ResponseEntity<?> crearPago(@RequestBody Map<String, Object> body) {
        try {
            Pago pago = pagoService.registrarPagoAdmin(
                    toLong(body.get("clienteId")),
                    toLong(body.get("membresiaId")),
                    parseMetodoPago(body.get("metodoPago")),
                    toBigDecimal(body.get("monto")),
                    toStringValue(body.get("observaciones"))
            );
            return ResponseEntity.ok(pagoResponse(pago));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/pagos/{id}/validar")
    public ResponseEntity<?> validarPago(@PathVariable Long id) {
        pagoService.validarPagoMembresia(id);
        return ResponseEntity.ok(Map.of("mensaje", "Pago validado y membresia activada"));
    }

    @PostMapping("/pagos/{id}/anular")
    public ResponseEntity<?> anularPago(@PathVariable Long id) {
        pagoService.anularPago(id);
        return ResponseEntity.ok(Map.of("mensaje", "Pago anulado"));
    }

    @GetMapping("/membresias")
    public ResponseEntity<?> listarMembresias() {
        return ResponseEntity.ok(Map.of(
            "activas", membresiaService.getMembresiasPorEstado(EstadoMembresia.ACTIVA).stream().map(this::membresiaResponse).toList(),
            "pendientes", membresiaService.getMembresiasPorEstado(EstadoMembresia.PENDIENTE_PAGO).stream().map(this::membresiaResponse).toList(),
            "vencidas", membresiaService.getMembresiasPorEstado(EstadoMembresia.VENCIDA).stream().map(this::membresiaResponse).toList(),
            "suspendidas", membresiaService.getMembresiasPorEstado(EstadoMembresia.SUSPENDIDA).stream().map(this::membresiaResponse).toList()
        ));
    }

    @PostMapping("/membresias")
    public ResponseEntity<?> crearMembresiaAdmin(@RequestBody Map<String, Long> body) {
        return ResponseEntity.ok(membresiaResponse(membresiaService.crearMembresia(body.get("clienteId"), body.get("planId"))));
    }

    @PostMapping("/membresias/crear")
    public ResponseEntity<?> crearMembresia(@RequestBody Map<String, Long> body) {
        return crearMembresiaAdmin(body);
    }

    @PostMapping("/membresias/{id}/renovar")
    public ResponseEntity<?> renovarMembresia(@PathVariable Long id) {
        return ResponseEntity.ok(membresiaResponse(membresiaService.renovarMembresia(id)));
    }

    @PostMapping("/membresias/{id}/suspender")
    public ResponseEntity<?> suspenderMembresia(@PathVariable Long id) {
        membresiaService.suspenderMembresia(id);
        return ResponseEntity.ok(Map.of("mensaje", "Membresia suspendida"));
    }

    @GetMapping("/planes")
    public ResponseEntity<List<Map<String, Object>>> listarPlanes() {
        return ResponseEntity.ok(membresiaService.getPlanes().stream().map(this::planResponse).toList());
    }

    @PostMapping("/planes")
    public ResponseEntity<?> crearPlan(@RequestBody Map<String, Object> body) {
        try {
            return ResponseEntity.ok(planResponse(membresiaService.crearPlan(body)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/planes/{id}")
    public ResponseEntity<?> actualizarPlan(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        try {
            return ResponseEntity.ok(planResponse(membresiaService.actualizarPlan(id, body)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/planes/{id}")
    public ResponseEntity<?> desactivarPlan(@PathVariable Long id) {
        membresiaService.desactivarPlan(id);
        return ResponseEntity.ok(Map.of("mensaje", "Plan desactivado"));
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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidation(MethodArgumentNotValidException ex) {
        List<String> errors = new ArrayList<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            errors.add(fieldError.getDefaultMessage());
        }
        return ResponseEntity.badRequest().body(Map.of("error", String.join(". ", errors)));
    }

    private Map<String, Object> usuarioBase(Usuario usuario) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", usuario.getId());
        map.put("nombre", usuario.getNombre());
        map.put("apellido", usuario.getApellido());
        map.put("correo", usuario.getCorreo());
        map.put("documento", usuario.getDocumento());
        map.put("telefono", usuario.getTelefono());
        map.put("activo", usuario.isActivo());
        map.put("status", usuario.getStatus());
        map.put("fechaRegistro", usuario.getFechaRegistro());
        return map;
    }

    private Map<String, Object> clienteResponse(Cliente cliente) {
        Map<String, Object> map = usuarioBase(cliente);
        map.put("peso", cliente.getPeso());
        map.put("altura", cliente.getAltura());
        map.put("imc", cliente.getImc());
        map.put("objetivo", cliente.getObjetivo());
        map.put("frecuenciaEntrenamiento", cliente.getFrecuenciaEntrenamiento());
        map.put("fechaNacimiento", cliente.getFechaNacimiento());
        map.put("genero", cliente.getGenero());
        map.put("quiereInstructor", cliente.isQuiereInstructor());
        map.put("active", cliente.isActive());
        if (cliente.getInstructor() != null) map.put("instructor", instructorResponse(cliente.getInstructor()));
        if (cliente.getMembresia() != null) map.put("membresia", membresiaSimpleResponse(cliente.getMembresia()));
        return map;
    }

    private Map<String, Object> instructorResponse(Instructor instructor) {
        Map<String, Object> map = usuarioBase(instructor);
        map.put("especialidad", instructor.getEspecialidad());
        map.put("certificaciones", instructor.getCertificaciones());
        map.put("anosExperiencia", instructor.getAnosExperiencia());
        map.put("salario", instructor.getSalario());
        map.put("horarioTrabajo", instructor.getHorarioTrabajo());
        map.put("contractType", instructor.getContractType());
        map.put("disponible", instructor.isDisponible());
        map.put("cantidadClientes", adminService.listarClientes().stream().filter(c -> c.getInstructor() != null && c.getInstructor().getId().equals(instructor.getId())).count());
        return map;
    }

    private Map<String, Object> planResponse(Plan plan) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", plan.getId());
        map.put("nombre", plan.getNombre());
        map.put("descripcion", plan.getDescripcion());
        map.put("duracionMeses", plan.getDuracionMeses());
        map.put("precio", plan.getPrecio());
        map.put("tipo", plan.getTipo());
        map.put("beneficios", plan.getBeneficios());
        map.put("activo", plan.isActivo());
        return map;
    }

    private Map<String, Object> membresiaResponse(Membresia membresia) {
        Map<String, Object> map = membresiaSimpleResponse(membresia);
        map.put("cliente", clienteSummary(membresia.getCliente()));
        return map;
    }

    private Map<String, Object> membresiaSimpleResponse(Membresia membresia) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", membresia.getId());
        map.put("plan", planResponse(membresia.getPlan()));
        map.put("fechaInicio", membresia.getFechaInicio());
        map.put("fechaVencimiento", membresia.getFechaVencimiento());
        map.put("estado", membresia.getEstado());
        map.put("activo", membresia.isActivo());
        map.put("diasRestantes", membresia.getDiasRestantes());
        return map;
    }

    private Map<String, Object> pagoResponse(Pago pago) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", pago.getId());
        map.put("monto", pago.getMonto());
        map.put("estado", pago.getEstado());
        map.put("referencia", pago.getReferencia());
        map.put("fechaPago", pago.getFechaPago());
        map.put("metodoPago", pago.getMetodoPago());
        map.put("observaciones", pago.getObservaciones());
        map.put("cliente", clienteSummary(pago.getCliente()));
        if (pago.getMembresia() != null) map.put("membresia", membresiaSimpleResponse(pago.getMembresia()));
        return map;
    }

    private Map<String, Object> clienteSummary(Cliente cliente) {
        Map<String, Object> map = usuarioBase(cliente);
        map.put("objetivo", cliente.getObjetivo());
        return map;
    }

    private Long toLong(Object value) {
        if (value == null || value.toString().isBlank()) return null;
        return Long.parseLong(value.toString());
    }

    private BigDecimal toBigDecimal(Object value) {
        if (value == null || value.toString().isBlank()) return null;
        return new BigDecimal(value.toString());
    }

    private String toStringValue(Object value) {
        return value == null ? null : value.toString();
    }

    private MetodoPago parseMetodoPago(Object value) {
        if (value == null || value.toString().isBlank()) throw new IllegalArgumentException("El metodo de pago es requerido");
        String normalized = value.toString().trim().toUpperCase();
        if (normalized.equals("EFECTIVO")) return MetodoPago.CASH;
        if (normalized.equals("TARJETA")) return MetodoPago.CARD;
        if (normalized.equals("TRANSFERENCIA")) return MetodoPago.TRANSFER;
        return MetodoPago.valueOf(normalized);
    }
}
