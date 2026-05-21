package com.gimnasio.service;

import com.gimnasio.dto.ProgresoDTO;
import com.gimnasio.enums.Objetivo;
import com.gimnasio.model.*;
import com.gimnasio.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final ProgresoFisicoRepository progresoRepository;
    private final RutinaRepository rutinaRepository;
    private final RutinaEjercicioRepository rutinaEjercicioRepository;
    private final EjercicioRepository ejercicioRepository;
    private final SistemaGimnasioService sistemaService;

    public ClienteService(ClienteRepository clienteRepository,
                          ProgresoFisicoRepository progresoRepository,
                          RutinaRepository rutinaRepository,
                          RutinaEjercicioRepository rutinaEjercicioRepository,
                          EjercicioRepository ejercicioRepository,
                          SistemaGimnasioService sistemaService) {
        this.clienteRepository = clienteRepository;
        this.progresoRepository = progresoRepository;
        this.rutinaRepository = rutinaRepository;
        this.rutinaEjercicioRepository = rutinaEjercicioRepository;
        this.ejercicioRepository = ejercicioRepository;
        this.sistemaService = sistemaService;
    }

    public Optional<Cliente> findById(Long id) {
        return clienteRepository.findById(id);
    }

    public Optional<Cliente> findByCorreo(String correo) {
        return clienteRepository.findByCorreo(correo);
    }

    @Transactional
    public Cliente actualizarObjetivo(Long clienteId, Objetivo nuevoObjetivo) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado: " + clienteId));

        Objetivo objetivoAnterior = cliente.getObjetivo();
        cliente.setObjetivo(nuevoObjetivo);
        Cliente savedCliente = clienteRepository.save(cliente);

        // Regenerar rutina automáticamente
        if (!nuevoObjetivo.equals(objetivoAnterior)) {
            sistemaService.generarRutina(savedCliente);
        }

        return savedCliente;
    }

    @Transactional
    public ProgresoFisico registrarProgreso(Long clienteId, ProgresoDTO dto) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado: " + clienteId));

        ProgresoFisico progreso = new ProgresoFisico();
        progreso.setCliente(cliente);
        progreso.setFecha(dto.getFecha());
        progreso.setPeso(dto.getPeso());
        progreso.setMedidaCintura(dto.getMedidaCintura());
        progreso.setMedidaCadera(dto.getMedidaCadera());
        progreso.setMedidaPecho(dto.getMedidaPecho());
        progreso.setRendimiento(dto.getRendimiento());
        progreso.setObservaciones(dto.getObservaciones());

        // Actualizar peso actual del cliente
        if (dto.getPeso() != null) {
            cliente.setPeso(dto.getPeso());
            cliente.setImc(cliente.calcularIMC());
            clienteRepository.save(cliente);
        }

        return progresoRepository.save(progreso);
    }

    @Transactional
    public RutinaEjercicio modificarEjercicioRutina(Long rutinaEjercicioId,
                                                     Integer series,
                                                     Integer repeticiones,
                                                     Integer descansoSegundos,
                                                     Long nuevoEjercicioId,
                                                     String notas) {
        RutinaEjercicio re = rutinaEjercicioRepository.findById(rutinaEjercicioId)
                .orElseThrow(() -> new RuntimeException("Ejercicio de rutina no encontrado"));

        if (series != null) re.setSeries(series);
        if (repeticiones != null) re.setRepeticiones(repeticiones);
        if (descansoSegundos != null) re.setDescansoSegundos(descansoSegundos);
        if (notas != null) re.setNotas(notas);

        if (nuevoEjercicioId != null) {
            Ejercicio nuevoEjercicio = ejercicioRepository.findById(nuevoEjercicioId)
                    .orElseThrow(() -> new RuntimeException("Ejercicio no encontrado"));
            re.setEjercicio(nuevoEjercicio);
        }

        // Marcar rutina como modificada por usuario
        re.getRutina().setGeneratedBySystem(false);

        return rutinaEjercicioRepository.save(re);
    }

    public List<ProgresoFisico> getHistorialProgreso(Long clienteId) {
        return progresoRepository.findByClienteIdOrderByFechaDesc(clienteId);
    }

    public Optional<Rutina> getRutinaActiva(Long clienteId) {
        return rutinaRepository.findActivaWithEjerciciosByClienteId(clienteId);
    }

    public List<Ejercicio> getEjerciciosDisponibles() {
        return ejercicioRepository.findByActivo(true);
    }

    @Transactional
    public Cliente actualizarDatosFisicos(Long clienteId, java.math.BigDecimal peso, java.math.BigDecimal altura) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
        cliente.actualizarDatosFisicos(peso, altura);
        return clienteRepository.save(cliente);
    }
}