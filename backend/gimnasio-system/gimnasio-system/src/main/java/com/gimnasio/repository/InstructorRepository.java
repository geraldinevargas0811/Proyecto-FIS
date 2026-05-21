package com.gimnasio.repository;

import com.gimnasio.model.Instructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InstructorRepository extends JpaRepository<Instructor, Long> {
    
    Optional<Instructor> findByCorreo(String correo);
    
    List<Instructor> findByDisponible(boolean disponible);
    
    List<Instructor> findByActivo(boolean activo);

    @Query("SELECT i FROM Instructor i WHERE i.disponible = true AND i.activo = true ORDER BY SIZE(i.clientesAsignados) ASC")
    List<Instructor> findDisponiblesOrdenadosPorCarga();

    @Query("SELECT i FROM Instructor i LEFT JOIN FETCH i.clientesAsignados WHERE i.id = :id")
    Optional<Instructor> findByIdWithClientes(@Param("id") Long id);
}