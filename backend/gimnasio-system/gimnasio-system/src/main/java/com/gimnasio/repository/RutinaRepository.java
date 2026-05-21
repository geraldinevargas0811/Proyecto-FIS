package com.gimnasio.repository;

import com.gimnasio.model.Rutina;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RutinaRepository extends JpaRepository<Rutina, Long> {
    Optional<Rutina> findByClienteId(Long clienteId);
    Optional<Rutina> findByClienteIdAndActivaTrue(Long clienteId);

    @Query("SELECT r FROM Rutina r LEFT JOIN FETCH r.rutinaEjercicios re LEFT JOIN FETCH re.ejercicio WHERE r.cliente.id = :clienteId AND r.activa = true")
    Optional<Rutina> findActivaWithEjerciciosByClienteId(@Param("clienteId") Long clienteId);
}