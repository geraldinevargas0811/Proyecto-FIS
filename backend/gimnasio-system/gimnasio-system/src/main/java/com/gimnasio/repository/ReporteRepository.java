package com.gimnasio.repository;

import com.gimnasio.model.Reporte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReporteRepository extends JpaRepository<Reporte, Long> {
    List<Reporte> findByTipo(String tipo);
    List<Reporte> findByGeneradoPorId(Long usuarioId);
    List<Reporte> findByFormato(String formato);
}