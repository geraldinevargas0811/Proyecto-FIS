package com.gimnasio.repository;

import com.gimnasio.model.ProgresoFisico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProgresoFisicoRepository extends JpaRepository<ProgresoFisico, Long> {
    List<ProgresoFisico> findByClienteIdOrderByFechaDesc(Long clienteId);
    List<ProgresoFisico> findByClienteIdAndFechaBetween(Long clienteId, LocalDate inicio, LocalDate fin);
    Optional<ProgresoFisico> findTopByClienteIdOrderByFechaDesc(Long clienteId);

    @Query("SELECT p FROM ProgresoFisico p WHERE p.cliente.id = :clienteId ORDER BY p.fecha DESC")
    List<ProgresoFisico> findUltimosProgresos(@Param("clienteId") Long clienteId);
}