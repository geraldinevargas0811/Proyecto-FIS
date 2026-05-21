package com.gimnasio.repository;

import com.gimnasio.enums.EstadoPago;
import com.gimnasio.model.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Long> {
    List<Pago> findByClienteId(Long clienteId);
    List<Pago> findByMembresiaId(Long membresiaId);
    List<Pago> findByEstado(EstadoPago estado);

    @Query("SELECT p FROM Pago p WHERE p.cliente.id = :clienteId ORDER BY p.fechaPago DESC")
    List<Pago> findByClienteIdOrderByFecha(@Param("clienteId") Long clienteId);

    @Query("SELECT p FROM Pago p WHERE p.membresia.id = :membresiaId AND p.estado = 'PAGADO'")
    List<Pago> findPagosConfirmadosByMembresia(@Param("membresiaId") Long membresiaId);
}