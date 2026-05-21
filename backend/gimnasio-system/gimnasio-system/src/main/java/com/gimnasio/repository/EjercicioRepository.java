package com.gimnasio.repository;

import com.gimnasio.model.Ejercicio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EjercicioRepository extends JpaRepository<Ejercicio, Long> {
    List<Ejercicio> findByActivo(boolean activo);
    List<Ejercicio> findByGrupoMuscular(String grupoMuscular);
    List<Ejercicio> findByGrupoMuscularAndActivo(String grupoMuscular, boolean activo);
}