package com.gimnasio.service;

import com.gimnasio.dto.RegistroClienteDTO;
import com.gimnasio.dto.RegistroInstructorDTO;
import com.gimnasio.enums.Rol;
import com.gimnasio.model.*;
import com.gimnasio.repository.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class AdministradorService {

    private final UsuarioRepository usuarioRepository;
    private final ClienteRepository clienteRepository;
    private final InstructorRepository instructorRepository;
    private final MembresiaRepository membresiaRepository;
    private final PlanRepository planRepository;
    private final PasswordEncoder passwordEncoder;
    private final SistemaGimnasioService sistemaService;

    public AdministradorService(UsuarioRepository usuarioRepository,
                                ClienteRepository clienteRepository,
                                InstructorRepository instructorRepository,
                                MembresiaRepository membresiaRepository,
                                PlanRepository planRepository,
                                PasswordEncoder passwordEncoder,
                                SistemaGimnasioService sistemaService) {
        this.usuarioRepository = usuarioRepository;
        this.clienteRepository = clienteRepository;
        this.instructorRepository = instructorRepository;
        this.membresiaRepository = membresiaRepository;
        this.planRepository = planRepository;
        this.passwordEncoder = passwordEncoder;
        this.sistemaService = sistemaService;
    }

    @Transactional
    public Cliente registrarCliente(RegistroClienteDTO dto) {
        if (usuarioRepository.existsByCorreo(dto.getCorreo())) {
            throw new IllegalArgumentException("Ya existe un usuario con el correo: " + dto.getCorreo());
        }
        if (dto.getDocumento() != null && usuarioRepository.existsByDocumento(dto.getDocumento())) {
            throw new IllegalArgumentException("Ya existe un usuario con el documento: " + dto.getDocumento());
        }

        Cliente cliente = new Cliente();
        cliente.setNombre(dto.getNombre());
        cliente.setApellido(dto.getApellido());
        cliente.setCorreo(dto.getCorreo());
        cliente.setContrasena(passwordEncoder.encode(dto.getContrasena()));
        cliente.setDocumento(dto.getDocumento());
        cliente.setTelefono(dto.getTelefono());
        cliente.setRol(Rol.CLIENTE);
        cliente.setActivo(true);
        cliente.setPeso(dto.getPeso());
        cliente.setAltura(dto.getAltura());
        cliente.setObjetivo(dto.getObjetivo());
        cliente.setFrecuenciaEntrenamiento(dto.getFrecuenciaEntrenamiento());
        cliente.setFechaNacimiento(dto.getFechaNacimiento());
        cliente.setQuiereInstructor(dto.isQuiereInstructor());

        if (dto.getGenero() != null) {
            try {
                cliente.setGenero(com.gimnasio.enums.Genero.valueOf(dto.getGenero().toUpperCase()));
            } catch (IllegalArgumentException e) {
                // Ignorar, mantener null
            }
        }

        // Calcular IMC si hay datos
        if (dto.getPeso() != null && dto.getAltura() != null) {
            cliente.setImc(cliente.calcularIMC());
        }

        // Asignar instructor si se solicitó
        if (dto.isQuiereInstructor() && dto.getInstructorId() != null) {
            instructorRepository.findById(dto.getInstructorId()).ifPresent(cliente::setInstructor);
        }

        Cliente savedCliente = clienteRepository.save(cliente);

        if (dto.getPlanId() != null) {
            Plan plan = planRepository.findById(dto.getPlanId())
                    .orElseThrow(() -> new IllegalArgumentException("Plan no encontrado"));
            Membresia membresia = new Membresia();
            membresia.setCliente(savedCliente);
            membresia.setPlan(plan);
            membresia.setFechaInicio(java.time.LocalDate.now());
            membresia.setFechaVencimiento(java.time.LocalDate.now().plusMonths(plan.getDuracionMeses()));
            membresia.setEstado(com.gimnasio.enums.EstadoMembresia.PENDIENTE_PAGO);
            membresia.setActivo(true);
            membresiaRepository.save(membresia);
        }

        // Generar rutina automáticamente
        try {
            sistemaService.generarRutina(savedCliente);
        } catch (Exception e) {
            // Log warning
        }

        return savedCliente;
    }

    @Transactional
    public Instructor registrarInstructor(RegistroInstructorDTO dto) {
        if (usuarioRepository.existsByCorreo(dto.getCorreo())) {
            throw new IllegalArgumentException("Ya existe un usuario con el correo: " + dto.getCorreo());
        }
        if (dto.getDocumento() != null && usuarioRepository.existsByDocumento(dto.getDocumento())) {
            throw new IllegalArgumentException("Ya existe un usuario con el documento: " + dto.getDocumento());
        }

        Instructor instructor = new Instructor();
        instructor.setNombre(dto.getNombre());
        instructor.setApellido(dto.getApellido());
        instructor.setCorreo(dto.getCorreo());
        instructor.setContrasena(passwordEncoder.encode(dto.getContrasena()));
        instructor.setDocumento(dto.getDocumento());
        instructor.setTelefono(dto.getTelefono());
        instructor.setRol(Rol.INSTRUCTOR);
        instructor.setActivo(true);
        instructor.setEspecialidad(dto.getEspecialidad());
        instructor.setCertificaciones(dto.getCertificaciones());
        instructor.setAnosExperiencia(dto.getAnosExperiencia());
        instructor.setSalario(dto.getSalario());
        instructor.setFechaContratacion(dto.getFechaContratacion());
        instructor.setHorarioTrabajo(dto.getHorarioTrabajo());
        instructor.setDisponible(true);

        try {
            if (dto.getContractType() != null) {
                instructor.setContractType(com.gimnasio.enums.ContractType.valueOf(dto.getContractType().toUpperCase()));
            }
        } catch (IllegalArgumentException e) {
            // Usar valor por defecto
        }

        return instructorRepository.save(instructor);
    }

    @Transactional
    public void desactivarUsuario(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + usuarioId));
        usuario.desactivarCuenta();
        if (usuario instanceof Cliente cliente) {
            cliente.setActive(false);
        }
        if (usuario instanceof Instructor instructor) {
            instructor.setDisponible(false);
        }
        usuarioRepository.save(usuario);
    }

    @Transactional
    public Cliente actualizarCliente(Long clienteId, Map<String, Object> body) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado"));

        actualizarUsuarioBase(cliente, body);

        if (body.containsKey("peso")) cliente.setPeso(toBigDecimal(body.get("peso")));
        if (body.containsKey("altura")) cliente.setAltura(toBigDecimal(body.get("altura")));
        if (body.containsKey("objetivo") && body.get("objetivo") != null) {
            cliente.setObjetivo(com.gimnasio.enums.Objetivo.valueOf(body.get("objetivo").toString()));
        }
        if (body.containsKey("frecuenciaEntrenamiento")) cliente.setFrecuenciaEntrenamiento(toStringValue(body.get("frecuenciaEntrenamiento")));
        if (body.containsKey("genero")) {
            Object genero = body.get("genero");
            if (genero == null || genero.toString().isBlank()) {
                cliente.setGenero(null);
            } else {
                cliente.setGenero(com.gimnasio.enums.Genero.valueOf(genero.toString().toUpperCase()));
            }
        }
        if (body.containsKey("quiereInstructor")) cliente.setQuiereInstructor(Boolean.TRUE.equals(body.get("quiereInstructor")));
        if (body.containsKey("instructorId")) {
            Object instructorId = body.get("instructorId");
            if (instructorId == null || instructorId.toString().isBlank()) {
                cliente.setInstructor(null);
            } else {
                Instructor instructor = instructorRepository.findById(Long.parseLong(instructorId.toString()))
                        .orElseThrow(() -> new IllegalArgumentException("Instructor no encontrado"));
                cliente.setInstructor(instructor);
            }
        }
        if (cliente.getPeso() != null && cliente.getAltura() != null) {
            cliente.setImc(cliente.calcularIMC());
        }

        return clienteRepository.save(cliente);
    }

    @Transactional
    public Instructor actualizarInstructor(Long instructorId, Map<String, Object> body) {
        Instructor instructor = instructorRepository.findById(instructorId)
                .orElseThrow(() -> new IllegalArgumentException("Instructor no encontrado"));

        actualizarUsuarioBase(instructor, body);

        if (body.containsKey("especialidad")) instructor.setEspecialidad(toStringValue(body.get("especialidad")));
        if (body.containsKey("certificaciones")) instructor.setCertificaciones(toStringValue(body.get("certificaciones")));
        if (body.containsKey("anosExperiencia")) instructor.setAnosExperiencia(toInteger(body.get("anosExperiencia")));
        if (body.containsKey("salario")) instructor.setSalario(toBigDecimal(body.get("salario")));
        if (body.containsKey("horarioTrabajo")) instructor.setHorarioTrabajo(toStringValue(body.get("horarioTrabajo")));
        if (body.containsKey("disponible")) instructor.setDisponible(Boolean.TRUE.equals(body.get("disponible")));
        if (body.containsKey("contractType") && body.get("contractType") != null) {
            instructor.setContractType(com.gimnasio.enums.ContractType.valueOf(body.get("contractType").toString().toUpperCase()));
        }

        return instructorRepository.save(instructor);
    }

    @Transactional
    public Cliente asignarInstructor(Long clienteId, Long instructorId) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        if (instructorId == null) {
            cliente.setInstructor(null);
        } else {
            Instructor instructor = instructorRepository.findById(instructorId)
                    .orElseThrow(() -> new RuntimeException("Instructor no encontrado"));
            cliente.setInstructor(instructor);
        }

        return clienteRepository.save(cliente);
    }

    public List<Cliente> listarClientes() {
        return clienteRepository.findAll();
    }

    public List<Instructor> listarInstructores() {
        return instructorRepository.findAll();
    }

    public List<Instructor> listarInstructoresDisponibles() {
        return instructorRepository.findByDisponible(true);
    }

    public List<Plan> listarPlanes() {
        return planRepository.findByActivo(true);
    }

    public Optional<Cliente> buscarClientePorId(Long id) {
        return clienteRepository.findById(id);
    }

    public Optional<Instructor> buscarInstructorPorId(Long id) {
        return instructorRepository.findById(id);
    }

    public List<Cliente> listarClientesActivos() {
        return clienteRepository.findByActive(true);
    }

    private void actualizarUsuarioBase(Usuario usuario, Map<String, Object> body) {
        if (body.containsKey("nombre")) usuario.setNombre(validarTextoHumano(toStringValue(body.get("nombre")), "nombre"));
        if (body.containsKey("apellido")) usuario.setApellido(validarTextoHumano(toStringValue(body.get("apellido")), "apellido"));
        if (body.containsKey("correo")) {
            String correo = toStringValue(body.get("correo"));
            if (correo == null || !correo.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
                throw new IllegalArgumentException("Ingrese un correo valido");
            }
            if (usuarioRepository.existsByCorreoAndIdNot(correo, usuario.getId())) {
                throw new IllegalArgumentException("Ya existe un usuario con este correo");
            }
            usuario.setCorreo(correo);
        }
        if (body.containsKey("documento")) {
            String documento = toStringValue(body.get("documento"));
            if (documento == null || !documento.matches("^[0-9]{5,20}$")) {
                throw new IllegalArgumentException("El documento debe contener entre 5 y 20 numeros");
            }
            if (usuarioRepository.existsByDocumentoAndIdNot(documento, usuario.getId())) {
                throw new IllegalArgumentException("Ya existe un usuario con este documento");
            }
            usuario.setDocumento(documento);
        }
        if (body.containsKey("telefono")) {
            String telefono = toStringValue(body.get("telefono"));
            if (telefono != null && !telefono.isBlank() && !telefono.matches("^[0-9]{7,15}$")) {
                throw new IllegalArgumentException("El telefono debe contener entre 7 y 15 numeros");
            }
            usuario.setTelefono(telefono);
        }
        if (body.containsKey("activo")) {
            boolean activo = Boolean.TRUE.equals(body.get("activo"));
            usuario.setActivo(activo);
            if (usuario instanceof Cliente cliente) cliente.setActive(activo);
        }
    }

    private String validarTextoHumano(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("El " + field + " es requerido");
        }
        if (!value.matches("^[A-Za-zÁÉÍÓÚáéíóúÑñ ]+$")) {
            throw new IllegalArgumentException("El " + field + " solo puede contener letras y espacios");
        }
        return value.trim();
    }

    private String toStringValue(Object value) {
        return value == null ? null : value.toString();
    }

    private Integer toInteger(Object value) {
        if (value == null || value.toString().isBlank()) return null;
        if (value instanceof Number number) return number.intValue();
        return Integer.parseInt(value.toString());
    }

    private BigDecimal toBigDecimal(Object value) {
        if (value == null || value.toString().isBlank()) return null;
        if (value instanceof BigDecimal decimal) return decimal;
        if (value instanceof Number number) return BigDecimal.valueOf(number.doubleValue());
        return new BigDecimal(value.toString());
    }
}
