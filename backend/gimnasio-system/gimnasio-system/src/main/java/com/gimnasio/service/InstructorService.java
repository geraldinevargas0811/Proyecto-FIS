package com.gimnasio.service;

import com.gimnasio.model.*;
import com.gimnasio.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class InstructorService {

    private final InstructorRepository instructorRepository;
    private final ClienteRepository clienteRepository;
    private final ProgresoFisicoRepository progresoRepository;
    private final NotaInstructorRepository notaRepository;

    public InstructorService(InstructorRepository instructorRepository,
                             ClienteRepository clienteRepository,
                             ProgresoFisicoRepository progresoRepository,
                             NotaInstructorRepository notaRepository) {
        this.instructorRepository = instructorRepository;
        this.clienteRepository = clienteRepository;
        this.progresoRepository = progresoRepository;
        this.notaRepository = notaRepository;
    }

    public Optional<Instructor> findById(Long id) {
        return instructorRepository.findById(id);
    }

    public Optional<Instructor> findByCorreo(String correo) {
        return instructorRepository.findByCorreo(correo);
    }

    public List<Cliente> consultarClientes(Long instructorId) {
        Instructor instructor = instructorRepository.findById(instructorId)
                .orElseThrow(() -> new RuntimeException("Instructor no encontrado"));
        return clienteRepository.findByInstructor(instructor);
    }

    public Optional<Rutina> consultarRutinaCliente(Long clienteId) {
        return clienteRepository.findById(clienteId).map(Cliente::getRutina);
    }

    public List<ProgresoFisico> consultarProgresoCliente(Long clienteId) {
        return progresoRepository.findByClienteIdOrderByFechaDesc(clienteId);
    }

    @Transactional
    public NotaInstructor brindarAsesoria(Long instructorId, Long clienteId, String nota) {
        Instructor instructor = instructorRepository.findById(instructorId)
                .orElseThrow(() -> new RuntimeException("Instructor no encontrado"));
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        // Verificar que el cliente pertenece al instructor
        if (cliente.getInstructor() == null || !cliente.getInstructor().getId().equals(instructorId)) {
            throw new IllegalArgumentException("Este cliente no está asignado a tu perfil");
        }

        NotaInstructor notaInstructor = new NotaInstructor(instructor, cliente, nota);
        return notaRepository.save(notaInstructor);
    }

    public List<NotaInstructor> getAsesoriasPorCliente(Long clienteId) {
        return notaRepository.findByClienteIdOrderByFechaDesc(clienteId);
    }

    public List<NotaInstructor> getAsesoriasPropias(Long instructorId, Long clienteId) {
        return notaRepository.findByInstructorIdAndClienteId(instructorId, clienteId);
    }

    public Optional<Cliente> findClienteById(Long id) {
        return clienteRepository.findById(id);
    }
}