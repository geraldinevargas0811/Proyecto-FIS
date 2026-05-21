package com.gimnasio.service;

import com.gimnasio.dto.RegistroClienteDTO;
import com.gimnasio.dto.RegistroInstructorDTO;
import com.gimnasio.enums.Rol;
import com.gimnasio.model.*;
import com.gimnasio.repository.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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
        usuarioRepository.save(usuario);
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
}