package com.gimnasio.config;

import com.gimnasio.enums.Genero;
import com.gimnasio.enums.MetodoPago;
import com.gimnasio.enums.Objetivo;
import com.gimnasio.enums.Rol;
import com.gimnasio.enums.EstadoMembresia;
import com.gimnasio.enums.EstadoPago;
import com.gimnasio.enums.ContractType;

import com.gimnasio.model.*;
import com.gimnasio.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class DemoDevDataSeeder implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DemoDevDataSeeder.class);

    @Value("${app.demo.enabled:false}")
    private boolean demoEnabled;

    private final PasswordEncoder passwordEncoder;

    private final ClienteRepository clienteRepository;
    private final InstructorRepository instructorRepository;
    private final UsuarioRepository usuarioRepository;
    private final PlanRepository planRepository;
    private final MembresiaRepository membresiaRepository;
    private final PagoRepository pagoRepository;
    private final RutinaRepository rutinaRepository;
    private final RutinaEjercicioRepository rutinaEjercicioRepository;
    private final EjercicioRepository ejercicioRepository;

    public DemoDevDataSeeder(
            PasswordEncoder passwordEncoder,
            ClienteRepository clienteRepository,
            InstructorRepository instructorRepository,
            UsuarioRepository usuarioRepository,
            PlanRepository planRepository,
            MembresiaRepository membresiaRepository,
            PagoRepository pagoRepository,
            RutinaRepository rutinaRepository,
            RutinaEjercicioRepository rutinaEjercicioRepository,
            EjercicioRepository ejercicioRepository
    ) {
        this.passwordEncoder = passwordEncoder;
        this.clienteRepository = clienteRepository;
        this.instructorRepository = instructorRepository;
        this.usuarioRepository = usuarioRepository;
        this.planRepository = planRepository;
        this.membresiaRepository = membresiaRepository;
        this.pagoRepository = pagoRepository;
        this.rutinaRepository = rutinaRepository;
        this.rutinaEjercicioRepository = rutinaEjercicioRepository;
        this.ejercicioRepository = ejercicioRepository;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (!demoEnabled) {
            log.info("DemoDevDataSeeder deshabilitado. app.demo.enabled=false");
            return;
        }

        // Usuarios demo
        UsuarioCreationResult admin = ensureAdministrador("admin@demo.com", "123456");
        UsuarioCreationResult instructor = ensureInstructor("instructor@demo.com", "123456");
        UsuarioCreationResult cliente = ensureCliente("cliente@demo.com", "123456", instructor.userId);

        // Datos mínimos relacionados (solo si falta alguno)
        ensureMinimalPlansAndEnrollment(cliente.userId);


        log.info("Semilla demo completada (solo entorno desarrollo)." );
    }

    private record UsuarioCreationResult(Long userId) {}

    private UsuarioCreationResult ensureAdministrador(String correo, String rawPassword) {
        return usuarioRepository.findByCorreo(correo)
                .map(u -> {
                    // Asegurar que la contraseña demo funcione incluso si el usuario ya existía.
                    // Esto evita el 401 cuando el usuario se creó antes con otra contraseña.
                    u.setContrasena(passwordEncoder.encode(rawPassword));
                    u.setRol(Rol.ADMIN);
                    u.setActivo(true);
                    u.setStatus(com.gimnasio.enums.UserStatus.ACTIVE);
                    Usuario saved = usuarioRepository.save(u);
                    log.info("ADMIN demo existente; password actualizado: {}", correo);
                    return new UsuarioCreationResult(saved.getId());
                })
                .orElseGet(() -> {
                    Administrador a = new Administrador();
                    a.setRol(Rol.ADMIN);
                    a.setNombre("Admin");
                    a.setApellido("Demo");
                    a.setCorreo(correo);
                    a.setContrasena(passwordEncoder.encode(rawPassword));
                    a.setDocumento("DOC-ADMIN-DEMO");
                    a.setTelefono("0000000000");
                    a.setActivo(true);
                    a.setStatus(com.gimnasio.enums.UserStatus.ACTIVE);
                    Administrador saved = (Administrador) usuarioRepository.save(a);
                    log.info("ADMIN demo creado: {}", correo);
                    return new UsuarioCreationResult(saved.getId());

                });
    }


    private UsuarioCreationResult ensureInstructor(String correo, String rawPassword) {
        return usuarioRepository.findByCorreo(correo)
                .map(u -> {
                    u.setContrasena(passwordEncoder.encode(rawPassword));
                    u.setRol(Rol.INSTRUCTOR);
                    u.setActivo(true);
                    u.setStatus(com.gimnasio.enums.UserStatus.ACTIVE);
                    Usuario saved = usuarioRepository.save(u);
                    log.info("INSTRUCTOR demo existente; password actualizado: {}", correo);
                    return new UsuarioCreationResult(saved.getId());
                })
                .orElseGet(() -> {
                    Instructor i = new Instructor();
                    i.setRol(Rol.INSTRUCTOR);
                    i.setNombre("Instruct");
                    i.setApellido("Demo");
                    i.setCorreo(correo);
                    i.setContrasena(passwordEncoder.encode(rawPassword));
                    i.setEspecialidad("Entrenamiento funcional");
                    i.setCertificaciones("Demo");
                    i.setAnosExperiencia(3);
                    i.setContractType(ContractType.PART_TIME);

                    i.setSalario(new BigDecimal("2500"));
                    i.setFechaContratacion(LocalDate.now().minusYears(1));
                    i.setHorarioTrabajo("Lun-Jue 6pm-9pm");
                    i.setDisponible(true);
                    i.setDocumento("DOC-INSTR-DEMO");
                    i.setTelefono("0000000000");
                    i.setActivo(true);
                    i.setStatus(com.gimnasio.enums.UserStatus.ACTIVE);

                    // Nota: la entidad está mapeada como subclase. Guardarla vía usuarioRepository funciona.
                    Usuario saved = usuarioRepository.save(i);
                    log.info("INSTRUCTOR demo creado: {}", correo);
                    return new UsuarioCreationResult(saved.getId());
                });
    }

    private UsuarioCreationResult ensureCliente(String correo, String rawPassword, Long instructorId) {
        return usuarioRepository.findByCorreo(correo)
                .map(u -> {
                    u.setContrasena(passwordEncoder.encode(rawPassword));
                    u.setRol(Rol.CLIENTE);
                    u.setActivo(true);
                    u.setStatus(com.gimnasio.enums.UserStatus.ACTIVE);
                    Usuario saved = usuarioRepository.save(u);
                    log.info("CLIENTE demo existente; password actualizado: {}", correo);
                    return new UsuarioCreationResult(saved.getId());
                })
                .orElseGet(() -> {
                    Instructor instructor = instructorRepository.findById(instructorId)
                            .orElseThrow(() -> new IllegalStateException("No existe el instructor demo"));

                    Cliente c = new Cliente();
                    c.setRol(Rol.CLIENTE);
                    c.setNombre("Cliente");
                    c.setApellido("Demo");
                    c.setCorreo(correo);
                    c.setContrasena(passwordEncoder.encode(rawPassword));
                    c.setDocumento("DOC-CLIENTE-DEMO");
                    c.setTelefono("0000000000");

                    c.setPeso(new BigDecimal("70"));
                    c.setAltura(new BigDecimal("1.75"));
                    c.setObjetivo(Objetivo.AUMENTAR_MASA);
                    c.setFrecuenciaEntrenamiento("3 dias/semana");
                    c.setFechaNacimiento(LocalDate.now().minusYears(25));
                    c.setGenero(Genero.OTHER);

                    c.setInstructor(instructor);
                    c.setQuiereInstructor(false);
                    c.setActive(true);

                    Usuario saved = usuarioRepository.save(c);
                    log.info("CLIENTE demo creado: {}", correo);
                    return new UsuarioCreationResult(saved.getId());
                });
    }

    private void ensureMinimalPlansAndEnrollment(Long clienteId) {
        if (clienteId == null) return;

        // A) Plan básico (si no existe)
        Plan plan = planRepository.findByTipo("BASICO_DEMO").stream().findFirst().orElseGet(() -> {
            Plan p = new Plan();
            p.setNombre("Plan Básico Demo");
            p.setDescripcion("Plan mínimo para pruebas visuales");
            p.setDuracionMeses(1);
            p.setPrecio(new BigDecimal("50000"));
            p.setTipo("BASICO_DEMO");
            p.setBeneficios("Acceso gimnasio, 1 rutina demo");
            p.setActivo(true);
            return planRepository.save(p);
        });

        // B) Membresía + pagos (si no existe)
        Membresia memb = membresiaRepository.findByClienteId(clienteId).orElseGet(() -> {
            Cliente cliente = clienteRepository.findById(clienteId)
                    .orElseThrow(() -> new IllegalStateException("Cliente demo no encontrado"));

            Membresia m = new Membresia();
            m.setCliente(cliente);
            m.setPlan(plan);
            m.setFechaInicio(LocalDate.now().minusDays(5));
            m.setFechaVencimiento(LocalDate.now().plusMonths(1));
            m.setEstado(EstadoMembresia.ACTIVA);
            m.setRenovacionAutomatica(false);
            m.setActivo(true);
            m.setCreatedAt(LocalDateTime.now());
            return membresiaRepository.save(m);
        });

        // C) Pago demo (si no existe al menos uno)
        // Usamos pagoRepository para chequear duplicado por simple existencia.
        // Como no hay query por referencia, creamos solo si no hay pagos.
        if (pagoRepository.findByClienteId(clienteId).isEmpty()) {
            Cliente cliente = clienteRepository.findById(clienteId).orElseThrow();
            Pago pago = new Pago();
            pago.setCliente(cliente);
            pago.setMembresia(memb);
            pago.setMonto(new BigDecimal("50000"));
            pago.setFechaPago(LocalDateTime.now().minusDays(2));
            pago.setMetodoPago(MetodoPago.CASH);
            pago.setEstado(EstadoPago.PENDIENTE);
            pago.setReferencia("PAGO-DEMO-1");
            pago.setObservaciones("Pago inicial para visualización");
            pagoRepository.save(pago);
        }

        // D) Rutina básica (si no existe)
        Rutina rutina = rutinaRepository.findByClienteId(clienteId).orElseGet(() -> {
            Cliente cliente = clienteRepository.findById(clienteId).orElseThrow();

            Rutina r = new Rutina();
            r.setCliente(cliente);
            r.setObjetivo(cliente.getObjetivo());
            r.setNivelDificultad("INTERMEDIO");
            r.setFrecuenciaSemanal(3);
            r.setActiva(true);
            r.setGeneratedBySystem(true);
            return rutinaRepository.save(r);
        });

        if (rutinaEjercicioRepository.findByRutinaId(rutina.getId()).isEmpty()) {
            // Ejercicios mínimos (si no existen en BD no los podemos crear sin su modelo completo; por eso intentamos buscar y si no existen, dejamos rutina vacía.
            // En este proyecto seguramente existen en DB por migración/seed manual.
            // Aun así intentamos crear al menos 1 ejercicio si la entidad permite.

            RutinaEjercicio re1 = new RutinaEjercicio();
            re1.setRutina(rutina);
            re1.setOrdenEjercicio(1);

            // Asegurar campos NO NULL según entidad `RutinaEjercicio`
            // (series y repeticiones tienen @Column(nullable=false))
            re1.setSeries(3);
            re1.setRepeticiones(10);
            re1.setDescansoSegundos(60);

            // Importante: el `exercise` es ManyToOne con nullable=false
            // Por eso debemos enlazar un Ejercicio existente.
            Ejercicio ejercicio = ejercicioRepository.findAll().stream().findFirst()
                    .orElseGet(() -> {
                        Ejercicio e = new Ejercicio();
                        e.setNombre("Ejercicio Demo");
                        e.setDescripcion("Ejercicio mínimo para seed de demo");
                        e.setGrupoMuscular("GENERAL");
                        e.setSeries(3);
                        e.setRepeticiones(10);
                        e.setDescansoSegundos(60);
                        e.setEquipamiento("N/A");
                        e.setVideoUrl("");
                        e.setActivo(true);
                        return ejercicioRepository.save(e);
                    });

            re1.setEjercicio(ejercicio);

            rutinaEjercicioRepository.save(re1);

        }
    }
}

