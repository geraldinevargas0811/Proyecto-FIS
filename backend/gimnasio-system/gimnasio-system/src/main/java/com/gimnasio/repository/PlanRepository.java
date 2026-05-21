package com.gimnasio.repository;

import com.gimnasio.model.Plan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlanRepository extends JpaRepository<Plan, Long> {
    List<Plan> findByActivo(boolean activo);
    List<Plan> findByTipo(String tipo);
}