package com.gimnasio.repository;

import com.gimnasio.enums.Objetivo;
import com.gimnasio.model.Cliente;
import com.gimnasio.model.Instructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    Optional<Cliente> findByCorreo(String correo);
    List<Cliente> findByInstructor(Instructor instructor);
    List<Cliente> findByInstructorIsNull();
    List<Cliente> findByObjetivo(Objetivo objetivo);
    List<Cliente> findByActive(boolean active);
    List<Cliente> findByQuiereInstructor(boolean quiereInstructor);

    @Query("SELECT c FROM Cliente c LEFT JOIN FETCH c.membresia WHERE c.id = :id")
    Optional<Cliente> findByIdWithMembresia(@Param("id") Long id);

    @Query("SELECT c FROM Cliente c LEFT JOIN FETCH c.rutina WHERE c.id = :id")
    Optional<Cliente> findByIdWithRutina(@Param("id") Long id);

    @Query("SELECT c FROM Cliente c WHERE c.instructor IS NULL AND c.quiereInstructor = true AND c.active = true")
    List<Cliente> findClientesSinInstructorQueDesean();

    @Query("SELECT COUNT(c) FROM Cliente c WHERE c.instructor.id = :instructorId")
    long countByInstructorId(@Param("instructorId") Long instructorId);
}