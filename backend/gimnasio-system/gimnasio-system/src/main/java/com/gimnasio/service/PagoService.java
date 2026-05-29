package com.gimnasio.service;

import com.gimnasio.enums.EstadoMembresia;
import com.gimnasio.enums.EstadoPago;
import com.gimnasio.enums.MetodoPago;
import com.gimnasio.model.*;
import com.gimnasio.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.math.BigDecimal;
import java.util.UUID;

@Service
public class PagoService {

    private final PagoRepository pagoRepository;
    private final MembresiaRepository membresiaRepository;
    private final ClienteRepository clienteRepository;

    public PagoService(PagoRepository pagoRepository,
                       MembresiaRepository membresiaRepository,
                       ClienteRepository clienteRepository) {
        this.pagoRepository = pagoRepository;
        this.membresiaRepository = membresiaRepository;
        this.clienteRepository = clienteRepository;
    }

    @Transactional
    public Pago registrarPago(Long clienteId, Long membresiaId, MetodoPago metodo) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        Membresia membresia = membresiaRepository.findById(membresiaId)
                .orElseThrow(() -> new RuntimeException("Membresía no encontrada"));

        Pago pago = new Pago();
        pago.setCliente(cliente);
        pago.setMembresia(membresia);
        pago.setMonto(membresia.getPlan().getPrecio());
        pago.setMetodoPago(metodo);
        pago.setEstado(EstadoPago.PENDIENTE);
        pago.setReferencia("PAG-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());

        return pagoRepository.save(pago);
    }

    @Transactional
    public Pago registrarPagoAdmin(Long clienteId, Long membresiaId, MetodoPago metodo, BigDecimal monto, String observaciones) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
        Membresia membresia = membresiaId == null ? null : membresiaRepository.findById(membresiaId)
                .orElseThrow(() -> new RuntimeException("Membresia no encontrada"));

        Pago pago = new Pago();
        pago.setCliente(cliente);
        pago.setMembresia(membresia);
        pago.setMonto(monto != null ? monto : membresia != null ? membresia.getPlan().getPrecio() : BigDecimal.ZERO);
        pago.setMetodoPago(metodo);
        pago.setEstado(EstadoPago.PENDIENTE);
        pago.setObservaciones(observaciones);
        pago.setReferencia("PAG-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        return pagoRepository.save(pago);
    }

    @Transactional
    public Pago validarPagoMembresia(Long pagoId) {
        Pago pago = pagoRepository.findById(pagoId)
                .orElseThrow(() -> new RuntimeException("Pago no encontrado"));

        pago.setEstado(EstadoPago.PAGADO);
        Pago saved = pagoRepository.save(pago);

        if (pago.getMembresia() != null) {
            Membresia membresia = pago.getMembresia();
            membresia.setEstado(EstadoMembresia.ACTIVA);
            if (membresia.getFechaInicio() == null) {
                membresia.setFechaInicio(java.time.LocalDate.now());
                membresia.setFechaVencimiento(java.time.LocalDate.now().plusMonths(membresia.getPlan().getDuracionMeses()));
            }
            membresiaRepository.save(membresia);
        }

        return saved;
    }

    @Transactional
    public Pago anularPago(Long pagoId) {
        Pago pago = pagoRepository.findById(pagoId)
                .orElseThrow(() -> new RuntimeException("Pago no encontrado"));
        pago.setEstado(EstadoPago.ANULADO);
        return pagoRepository.save(pago);
    }

    public List<Pago> getPagosPorCliente(Long clienteId) {
        return pagoRepository.findByClienteIdOrderByFecha(clienteId);
    }

    public List<Pago> getPagosPendientes() {
        return pagoRepository.findByEstado(EstadoPago.PENDIENTE);
    }

    public List<Pago> getTodosPagos() {
        return pagoRepository.findAll();
    }
}
