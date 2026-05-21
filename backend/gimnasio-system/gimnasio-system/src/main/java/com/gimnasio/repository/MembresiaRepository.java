package com.gimnasio.repository;

import com.gimnasio.enums.EstadoMembresia;
import com.gimnasio.model.Membresia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface MembresiaRepository extends JpaRepository<Membresia, Long> {
    Optional<Membresia> findByClienteId(Long clienteId);
    List<Membresia> findByEstado(EstadoMembresia estado);
    List<Membresia> findByFechaVencimientoBefore(LocalDate fecha);

    @Query("SELECT m FROM Membresia m WHERE m.estado = 'ACTIVA' AND m.fechaVencimiento < CURRENT_DATE")
    List<Membresia> findMembresiasVencidas();

    @Query("SELECT m FROM Membresia m JOIN FETCH m.plan WHERE m.cliente.id = :clienteId")
    Optional<Membresia> findByClienteIdWithPlan(@Param("clienteId") Long clienteId);
}