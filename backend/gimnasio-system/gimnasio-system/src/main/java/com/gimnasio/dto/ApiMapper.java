package com.gimnasio.dto;

import com.gimnasio.model.*;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Component
public class ApiMapper {

    public ClienteResponse toClienteResponse(Cliente cliente) {
        if (cliente == null) return null;
        ClienteResponse dto = new ClienteResponse();
        dto.setId(cliente.getId());
        dto.setNombre(cliente.getNombre());
        dto.setApellido(cliente.getApellido());
        dto.setNombreCompleto(cliente.getNombreCompleto());
        dto.setCorreo(cliente.getCorreo());
        dto.setDocumento(cliente.getDocumento());
        dto.setTelefono(cliente.getTelefono());
        dto.setActivo(cliente.isActivo());
        dto.setStatus(cliente.getStatus() != null ? cliente.getStatus().name() : null);
        dto.setPeso(cliente.getPeso());
        dto.setAltura(cliente.getAltura());
        dto.setImc(cliente.getImc());
        dto.setCategoriaPeso(cliente.getCategoriaPeso());
        dto.setObjetivo(cliente.getObjetivo() != null ? cliente.getObjetivo().name() : null);
        dto.setFrecuenciaEntrenamiento(cliente.getFrecuenciaEntrenamiento());
        dto.setFechaNacimiento(cliente.getFechaNacimiento());
        dto.setEdad(cliente.getEdad());
        dto.setGenero(cliente.getGenero() != null ? cliente.getGenero().name() : null);
        dto.setQuiereInstructor(cliente.isQuiereInstructor());
        dto.setInstructor(toInstructorResumen(cliente.getInstructor()));
        dto.setMembresia(toMembresiaResumen(cliente.getMembresia()));
        dto.setFechaRegistro(cliente.getFechaRegistro());
        return dto;
    }

    public ClienteResumenResponse toClienteResumen(Cliente cliente) {
        if (cliente == null) return null;
        ClienteResumenResponse dto = new ClienteResumenResponse();
        dto.setId(cliente.getId());
        dto.setNombreCompleto(cliente.getNombreCompleto());
        dto.setCorreo(cliente.getCorreo());
        dto.setDocumento(cliente.getDocumento());
        dto.setObjetivo(cliente.getObjetivo() != null ? cliente.getObjetivo().name() : null);
        dto.setActivo(cliente.isActivo());
        return dto;
    }

    public InstructorResponse toInstructorResponse(Instructor instructor) {
        if (instructor == null) return null;
        InstructorResponse dto = new InstructorResponse();
        dto.setId(instructor.getId());
        dto.setNombre(instructor.getNombre());
        dto.setApellido(instructor.getApellido());
        dto.setNombreCompleto(instructor.getNombreCompleto());
        dto.setCorreo(instructor.getCorreo());
        dto.setDocumento(instructor.getDocumento());
        dto.setTelefono(instructor.getTelefono());
        dto.setActivo(instructor.isActivo());
        dto.setStatus(instructor.getStatus() != null ? instructor.getStatus().name() : null);
        dto.setEspecialidad(instructor.getEspecialidad());
        dto.setCertificaciones(instructor.getCertificaciones());
        dto.setAnosExperiencia(instructor.getAnosExperiencia());
        dto.setDisponible(instructor.isDisponible());
        dto.setContractType(instructor.getContractType() != null ? instructor.getContractType().name() : null);
        dto.setSalario(instructor.getSalario());
        dto.setFechaContratacion(instructor.getFechaContratacion());
        dto.setHorarioTrabajo(instructor.getHorarioTrabajo());
        dto.setCantidadClientes(instructor.getCantidadClientes());
        return dto;
    }

    public InstructorResumenResponse toInstructorResumen(Instructor instructor) {
        if (instructor == null) return null;
        InstructorResumenResponse dto = new InstructorResumenResponse();
        dto.setId(instructor.getId());
        dto.setNombreCompleto(instructor.getNombreCompleto());
        dto.setCorreo(instructor.getCorreo());
        dto.setEspecialidad(instructor.getEspecialidad());
        dto.setDisponible(instructor.isDisponible());
        return dto;
    }

    public PlanResponse toPlanResponse(Plan plan) {
        if (plan == null) return null;
        PlanResponse dto = new PlanResponse();
        dto.setId(plan.getId());
        dto.setNombre(plan.getNombre());
        dto.setDescripcion(plan.getDescripcion());
        dto.setDuracionMeses(plan.getDuracionMeses());
        dto.setPrecio(plan.getPrecio());
        dto.setTipo(plan.getTipo());
        dto.setBeneficios(plan.getBeneficios());
        dto.setActivo(plan.isActivo());
        return dto;
    }

    public MembresiaResumenResponse toMembresiaResumen(Membresia membresia) {
        if (membresia == null) return null;
        MembresiaResumenResponse dto = new MembresiaResumenResponse();
        dto.setId(membresia.getId());
        dto.setPlanNombre(membresia.getPlan() != null ? membresia.getPlan().getNombre() : null);
        dto.setEstado(membresia.getEstado() != null ? membresia.getEstado().name() : null);
        dto.setFechaInicio(membresia.getFechaInicio());
        dto.setFechaVencimiento(membresia.getFechaVencimiento());
        dto.setDiasRestantes(membresia.getDiasRestantes());
        dto.setVigente(membresia.isVigente());
        return dto;
    }

    public MembresiaResponse toMembresiaResponse(Membresia membresia) {
        if (membresia == null) return null;
        MembresiaResponse dto = new MembresiaResponse();
        dto.setId(membresia.getId());
        dto.setCliente(toClienteResumen(membresia.getCliente()));
        dto.setPlan(toPlanResponse(membresia.getPlan()));
        dto.setFechaInicio(membresia.getFechaInicio());
        dto.setFechaVencimiento(membresia.getFechaVencimiento());
        dto.setEstado(membresia.getEstado() != null ? membresia.getEstado().name() : null);
        dto.setRenovacionAutomatica(membresia.isRenovacionAutomatica());
        dto.setActivo(membresia.isActivo());
        dto.setDiasRestantes(membresia.getDiasRestantes());
        dto.setVigente(membresia.isVigente());
        dto.setCreatedAt(membresia.getCreatedAt());
        return dto;
    }

    public PagoResponse toPagoResponse(Pago pago) {
        if (pago == null) return null;
        PagoResponse dto = new PagoResponse();
        dto.setId(pago.getId());
        dto.setCliente(toClienteResumen(pago.getCliente()));
        dto.setMembresiaId(pago.getMembresia() != null ? pago.getMembresia().getId() : null);
        dto.setPlanNombre(pago.getMembresia() != null && pago.getMembresia().getPlan() != null ? pago.getMembresia().getPlan().getNombre() : null);
        dto.setMonto(pago.getMonto());
        dto.setFechaPago(pago.getFechaPago());
        dto.setMetodoPago(pago.getMetodoPago() != null ? pago.getMetodoPago().name() : null);
        dto.setEstado(pago.getEstado() != null ? pago.getEstado().name() : null);
        dto.setReferencia(pago.getReferencia());
        dto.setObservaciones(pago.getObservaciones());
        dto.setCreatedAt(pago.getCreatedAt());
        return dto;
    }

    public EjercicioResponse toEjercicioResponse(Ejercicio ejercicio) {
        if (ejercicio == null) return null;
        EjercicioResponse dto = new EjercicioResponse();
        dto.setId(ejercicio.getId());
        dto.setNombre(ejercicio.getNombre());
        dto.setDescripcion(ejercicio.getDescripcion());
        dto.setGrupoMuscular(ejercicio.getGrupoMuscular());
        dto.setSeries(ejercicio.getSeries());
        dto.setRepeticiones(ejercicio.getRepeticiones());
        dto.setDescansoSegundos(ejercicio.getDescansoSegundos());
        dto.setEquipamiento(ejercicio.getEquipamiento());
        dto.setVideoUrl(ejercicio.getVideoUrl());
        dto.setActivo(ejercicio.isActivo());
        return dto;
    }

    public RutinaResponse toRutinaResponse(Rutina rutina) {
        if (rutina == null) return null;
        RutinaResponse dto = new RutinaResponse();
        dto.setId(rutina.getId());
        dto.setClienteId(rutina.getCliente() != null ? rutina.getCliente().getId() : null);
        dto.setObjetivo(rutina.getObjetivo() != null ? rutina.getObjetivo().name() : null);
        dto.setNivelDificultad(rutina.getNivelDificultad());
        dto.setFrecuenciaSemanal(rutina.getFrecuenciaSemanal());
        dto.setFechaGeneracion(rutina.getFechaGeneracion());
        dto.setActiva(rutina.isActiva());
        dto.setGeneratedBySystem(rutina.isGeneratedBySystem());
        dto.setEjercicios(rutina.getRutinaEjercicios().stream()
                .sorted(Comparator.comparing(RutinaEjercicio::getOrdenEjercicio, Comparator.nullsLast(Integer::compareTo)))
                .map(this::toRutinaEjercicioResponse)
                .toList());
        return dto;
    }

    public RutinaEjercicioResponse toRutinaEjercicioResponse(RutinaEjercicio rutinaEjercicio) {
        if (rutinaEjercicio == null) return null;
        RutinaEjercicioResponse dto = new RutinaEjercicioResponse();
        dto.setId(rutinaEjercicio.getId());
        dto.setEjercicio(toEjercicioResponse(rutinaEjercicio.getEjercicio()));
        dto.setDiaSemana(rutinaEjercicio.getDiaSemana());
        dto.setSeries(rutinaEjercicio.getSeries());
        dto.setRepeticiones(rutinaEjercicio.getRepeticiones());
        dto.setDescansoSegundos(rutinaEjercicio.getDescansoSegundos());
        dto.setOrdenEjercicio(rutinaEjercicio.getOrdenEjercicio());
        dto.setNotas(rutinaEjercicio.getNotas());
        return dto;
    }

    public ProgresoResponse toProgresoResponse(ProgresoFisico progreso) {
        if (progreso == null) return null;
        ProgresoResponse dto = new ProgresoResponse();
        dto.setId(progreso.getId());
        dto.setClienteId(progreso.getCliente() != null ? progreso.getCliente().getId() : null);
        dto.setFecha(progreso.getFecha());
        dto.setPeso(progreso.getPeso());
        dto.setMedidaCintura(progreso.getMedidaCintura());
        dto.setMedidaCadera(progreso.getMedidaCadera());
        dto.setMedidaPecho(progreso.getMedidaPecho());
        dto.setRendimiento(progreso.getRendimiento());
        dto.setObservaciones(progreso.getObservaciones());
        dto.setRecordedAt(progreso.getRecordedAt());
        return dto;
    }

    public NotaInstructorResponse toNotaInstructorResponse(NotaInstructor nota) {
        if (nota == null) return null;
        NotaInstructorResponse dto = new NotaInstructorResponse();
        dto.setId(nota.getId());
        dto.setInstructor(toInstructorResumen(nota.getInstructor()));
        dto.setCliente(toClienteResumen(nota.getCliente()));
        dto.setNota(nota.getNota());
        dto.setFecha(nota.getFecha());
        return dto;
    }

    public List<ClienteResponse> toClientesResponse(List<Cliente> clientes) {
        return clientes.stream().map(this::toClienteResponse).toList();
    }

    public List<InstructorResponse> toInstructoresResponse(List<Instructor> instructores) {
        return instructores.stream().map(this::toInstructorResponse).toList();
    }
}
