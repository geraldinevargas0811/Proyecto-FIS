package com.gimnasio.repository;

import com.gimnasio.model.NotaInstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotaInstructorRepository extends JpaRepository<NotaInstructor, Long> {
    List<NotaInstructor> findByClienteIdOrderByFechaDesc(Long clienteId);
    List<NotaInstructor> findByInstructorId(Long instructorId);
    List<NotaInstructor> findByInstructorIdAndClienteId(Long instructorId, Long clienteId);
}