package com.gimnasio.service;

import com.gimnasio.enums.EstadoMembresia;
import com.gimnasio.enums.Objetivo;
import com.gimnasio.model.*;
import com.gimnasio.repository.EjercicioRepository;
import com.gimnasio.repository.RutinaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class SistemaGimnasioService {

    private final EjercicioRepository ejercicioRepository;
    private final RutinaRepository rutinaRepository;

    public SistemaGimnasioService(EjercicioRepository ejercicioRepository, RutinaRepository rutinaRepository) {
        this.ejercicioRepository = ejercicioRepository;
        this.rutinaRepository = rutinaRepository;
    }

    @Transactional
    public Rutina generarRutina(Cliente cliente) {
        // Desactivar rutina anterior si existe
        rutinaRepository.findByClienteId(cliente.getId()).ifPresent(r -> {
            r.setActiva(false);
            rutinaRepository.save(r);
        });

        Rutina rutina = new Rutina();
        rutina.setCliente(cliente);
        rutina.setObjetivo(cliente.getObjetivo());
        rutina.setGeneratedBySystem(true);
        rutina.setActiva(true);

        // Configurar según objetivo
        switch (cliente.getObjetivo()) {
            case AUMENTAR_MASA:
                rutina.setNivelDificultad("AVANZADO");
                rutina.setFrecuenciaSemanal(4);
                break;
            case DEFINICION:
                rutina.setNivelDificultad("INTERMEDIO");
                rutina.setFrecuenciaSemanal(4);
                break;
            case PERDER_PESO:
                rutina.setNivelDificultad("BASICO");
                rutina.setFrecuenciaSemanal(5);
                break;
        }

        // Seleccionar y asignar ejercicios
        List<Ejercicio> ejercicios = seleccionarEjercicios(cliente.getObjetivo());

        int orden = 1;
        for (Ejercicio ejercicio : ejercicios) {
            RutinaEjercicio re = new RutinaEjercicio();
            re.setRutina(rutina);
            re.setEjercicio(ejercicio);
            re.setSeries(ejercicio.getSeries());
            re.setRepeticiones(ejercicio.getRepeticiones());
            re.setDescansoSegundos(ejercicio.getDescansoSegundos());
            re.setOrdenEjercicio(orden++);
            rutina.getRutinaEjercicios().add(re);
        }

        return rutinaRepository.save(rutina);
    }

    public List<Ejercicio> seleccionarEjercicios(Objetivo objetivo) {
        List<Ejercicio> todos = ejercicioRepository.findByActivo(true);
        List<Ejercicio> seleccionados = new ArrayList<>();

        switch (objetivo) {
            case AUMENTAR_MASA:
                for (Ejercicio e : todos) {
                    if (e.getGrupoMuscular() != null && 
                        (e.getGrupoMuscular().equals("Pecho") || 
                         e.getGrupoMuscular().equals("Piernas") ||
                         e.getGrupoMuscular().equals("Espalda"))) {
                        seleccionados.add(e);
                        if (seleccionados.size() >= 6) break;
                    }
                }
                break;
            case DEFINICION:
                for (Ejercicio e : todos) {
                    if (seleccionados.size() < 8) {
                        seleccionados.add(e);
                    } else break;
                }
                break;
            case PERDER_PESO:
                for (Ejercicio e : todos) {
                    if (e.getGrupoMuscular() != null && 
                        (e.getGrupoMuscular().equals("Cardio") || 
                         e.getGrupoMuscular().equals("Core"))) {
                        seleccionados.add(e);
                        if (seleccionados.size() >= 6) break;
                    }
                }
                if (seleccionados.isEmpty() && !todos.isEmpty()) {
                    seleccionados.addAll(todos.subList(0, Math.min(6, todos.size())));
                }
                break;
        }

        if (seleccionados.isEmpty() && !todos.isEmpty()) {
            seleccionados.addAll(todos.subList(0, Math.min(6, todos.size())));
        }

        return seleccionados;
    }

    public BigDecimal calcularIMC(BigDecimal peso, BigDecimal altura) {
        if (altura == null || altura.compareTo(BigDecimal.ZERO) == 0) return BigDecimal.ZERO;
        return peso.divide(altura.multiply(altura), 2, RoundingMode.HALF_UP);
    }

    public void actualizarEstadoMembresia(Membresia membresia) {
        if (membresia.getEstado() == EstadoMembresia.ACTIVA
                && membresia.getFechaVencimiento().isBefore(LocalDate.now())) {
            membresia.setEstado(EstadoMembresia.VENCIDA);
        }
    }

    public String evaluarObjetivo(Objetivo objetivo, BigDecimal imc) {
        if (imc == null) return "Sin datos de IMC para evaluar.";
        double val = imc.doubleValue();
        
        switch (objetivo) {
            case PERDER_PESO:
                return val < 18.5 
                    ? "⚠️ No recomendado: tu IMC indica bajo peso. Considera un objetivo de aumento de masa."
                    : "✅ Objetivo adecuado para tu condición física.";
            case AUMENTAR_MASA:
                return val > 30 
                    ? "⚠️ Considera primero perder peso antes de enfocarte en masa muscular."
                    : "✅ Objetivo adecuado para tu condición física.";
            case DEFINICION:
                return "✅ Objetivo de definición adecuado para cualquier composición corporal.";
            default:
                return "Objetivo evaluado.";
        }
    }
}