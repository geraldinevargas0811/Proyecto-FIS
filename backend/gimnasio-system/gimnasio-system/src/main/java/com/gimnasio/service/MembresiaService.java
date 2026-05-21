package com.gimnasio.service;

import com.gimnasio.enums.EstadoMembresia;
import com.gimnasio.model.*;
import com.gimnasio.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class MembresiaService {

    private final MembresiaRepository membresiaRepository;
    private final ClienteRepository clienteRepository;
    private final PlanRepository planRepository;

    public MembresiaService(MembresiaRepository membresiaRepository,
                            ClienteRepository clienteRepository,
                            PlanRepository planRepository) {
        this.membresiaRepository = membresiaRepository;
        this.clienteRepository = clienteRepository;
        this.planRepository = planRepository;
    }

    @Transactional
    public Membresia crearMembresia(Long clienteId, Long planId) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new RuntimeException("Plan no encontrado"));

        // Desactivar membresía anterior si existe
        membresiaRepository.findByClienteId(clienteId).ifPresent(m -> {
            m.setActivo(false);
            membresiaRepository.save(m);
        });

        Membresia membresia = new Membresia();
        membresia.setCliente(cliente);
        membresia.setPlan(plan);
        membresia.setFechaInicio(LocalDate.now());
        membresia.setFechaVencimiento(LocalDate.now().plusMonths(plan.getDuracionMeses()));
        membresia.setEstado(EstadoMembresia.PENDIENTE_PAGO);
        membresia.setActivo(true);

        return membresiaRepository.save(membresia);
    }

    @Transactional
    public Membresia activarMembresia(Long membresiaId) {
        Membresia membresia = membresiaRepository.findById(membresiaId)
                .orElseThrow(() -> new RuntimeException("Membresía no encontrada"));
        membresia.setEstado(EstadoMembresia.ACTIVA);
        membresia.setFechaInicio(LocalDate.now());
        membresia.setFechaVencimiento(LocalDate.now().plusMonths(membresia.getPlan().getDuracionMeses()));
        return membresiaRepository.save(membresia);
    }

    @Transactional
    public Membresia suspenderMembresia(Long membresiaId) {
        Membresia membresia = membresiaRepository.findById(membresiaId)
                .orElseThrow(() -> new RuntimeException("Membresía no encontrada"));
        membresia.setEstado(EstadoMembresia.SUSPENDIDA);
        return membresiaRepository.save(membresia);
    }

    public Optional<Membresia> getMembresiaByCliente(Long clienteId) {
        return membresiaRepository.findByClienteIdWithPlan(clienteId);
    }

    public List<Membresia> getMembresiasPorEstado(EstadoMembresia estado) {
        return membresiaRepository.findByEstado(estado);
    }

    @Transactional
    public int verificarMembresiasVencidas() {
        List<Membresia> vencidas = membresiaRepository.findMembresiasVencidas();
        vencidas.forEach(m -> m.setEstado(EstadoMembresia.VENCIDA));
        membresiaRepository.saveAll(vencidas);
        return vencidas.size();
    }

    public List<Plan> getPlanes() {
        return planRepository.findByActivo(true);
    }

    public Optional<Plan> getPlanById(Long id) {
        return planRepository.findById(id);
    }
}