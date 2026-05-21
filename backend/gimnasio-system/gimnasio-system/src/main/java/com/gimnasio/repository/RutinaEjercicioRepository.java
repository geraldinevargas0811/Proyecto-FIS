package com.gimnasio.repository;

import com.gimnasio.model.RutinaEjercicio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RutinaEjercicioRepository extends JpaRepository<RutinaEjercicio, Long> {
    List<RutinaEjercicio> findByRutinaId(Long rutinaId);
    void deleteByRutinaId(Long rutinaId);
}