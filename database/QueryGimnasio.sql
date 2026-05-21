-- =========================================
-- ELIMINAR TABLAS SI EXISTEN (orden inverso por FK)
-- =========================================
DROP TABLE IF EXISTS instructor_notes CASCADE;
DROP TABLE IF EXISTS routine_exercises CASCADE;
DROP TABLE IF EXISTS routines CASCADE;
DROP TABLE IF EXISTS physical_progress CASCADE;
DROP TABLE IF EXISTS payments CASCADE;
DROP TABLE IF EXISTS memberships CASCADE;
DROP TABLE IF EXISTS membership_plans CASCADE;
DROP TABLE IF EXISTS exercises CASCADE;
DROP TABLE IF EXISTS clients CASCADE;
DROP TABLE IF EXISTS instructors CASCADE;
DROP TABLE IF EXISTS reports CASCADE;
DROP TABLE IF EXISTS users CASCADE;
DROP TYPE IF EXISTS user_status CASCADE;
DROP TYPE IF EXISTS gender_type CASCADE;
DROP TYPE IF EXISTS training_goal CASCADE;
DROP TYPE IF EXISTS membership_status CASCADE;
DROP TYPE IF EXISTS payment_method CASCADE;
DROP TYPE IF EXISTS payment_status CASCADE;
DROP TYPE IF EXISTS contract_type CASCADE;
DROP TYPE IF EXISTS user_role CASCADE;

-- =========================================
-- ENUMS
-- =========================================
CREATE TYPE user_status AS ENUM ('ACTIVE', 'INACTIVE', 'BLOCKED');
CREATE TYPE gender_type AS ENUM ('MALE', 'FEMALE', 'OTHER');
CREATE TYPE training_goal AS ENUM ('AUMENTAR_MASA', 'DEFINICION', 'PERDER_PESO');
CREATE TYPE membership_status AS ENUM ('ACTIVA', 'VENCIDA', 'SUSPENDIDA', 'PENDIENTE_PAGO');
CREATE TYPE payment_method AS ENUM ('CASH', 'CARD', 'TRANSFER');
CREATE TYPE payment_status AS ENUM ('PAGADO', 'PENDIENTE', 'ANULADO');
CREATE TYPE contract_type AS ENUM ('FULL_TIME', 'PART_TIME', 'TEMPORARY');
CREATE TYPE user_role AS ENUM ('ADMIN', 'INSTRUCTOR', 'CLIENTE');

-- =========================================
-- USERS (CLASE BASE)
-- =========================================
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    contrasena VARCHAR(255) NOT NULL,
    documento VARCHAR(50) UNIQUE,
    telefono VARCHAR(30),
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    activo BOOLEAN DEFAULT TRUE,
    rol user_role NOT NULL,
    status user_status DEFAULT 'ACTIVE',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =========================================
-- ADMINISTRADORES
-- =========================================
CREATE TABLE administradores (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT UNIQUE NOT NULL,
    nivel_acceso VARCHAR(50) DEFAULT 'STANDARD',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_admin_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- =========================================
-- INSTRUCTORS
-- =========================================
CREATE TABLE instructors (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT UNIQUE NOT NULL,
    especialidad VARCHAR(100),
    certificaciones TEXT,
    anos_experiencia INTEGER DEFAULT 0,
    disponible BOOLEAN DEFAULT TRUE,
    contract_type contract_type DEFAULT 'PART_TIME',
    salario NUMERIC(10,2),
    fecha_contratacion DATE DEFAULT CURRENT_DATE,
    horario_trabajo TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_instructor_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- =========================================
-- CLIENTS
-- =========================================
CREATE TABLE clients (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT UNIQUE NOT NULL,
    peso NUMERIC(5,2),
    altura NUMERIC(5,2),
    imc NUMERIC(5,2),
    objetivo training_goal NOT NULL,
    frecuencia_entrenamiento VARCHAR(50),
    fecha_nacimiento DATE,
    genero gender_type,
    instructor_id BIGINT,
    quiere_instructor BOOLEAN DEFAULT FALSE,
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_client_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_client_instructor FOREIGN KEY (instructor_id) REFERENCES instructors(id) ON DELETE SET NULL
);

-- =========================================
-- INSTRUCTOR NOTES (asesorías)
-- =========================================
CREATE TABLE instructor_notes (
    id BIGSERIAL PRIMARY KEY,
    instructor_id BIGINT NOT NULL,
    client_id BIGINT NOT NULL,
    nota TEXT NOT NULL,
    fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_note_instructor FOREIGN KEY (instructor_id) REFERENCES instructors(id),
    CONSTRAINT fk_note_client FOREIGN KEY (client_id) REFERENCES clients(id)
);

-- =========================================
-- EXERCISES
-- =========================================
CREATE TABLE exercises (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(150) NOT NULL,
    descripcion TEXT,
    grupo_muscular VARCHAR(100),
    series INTEGER DEFAULT 3,
    repeticiones INTEGER DEFAULT 10,
    descanso_segundos INTEGER DEFAULT 60,
    equipamiento VARCHAR(100),
    video_url TEXT,
    activo BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =========================================
-- ROUTINES
-- =========================================
CREATE TABLE routines (
    id BIGSERIAL PRIMARY KEY,
    client_id BIGINT NOT NULL,
    objetivo training_goal NOT NULL,
    nivel_dificultad VARCHAR(50) DEFAULT 'INTERMEDIO',
    frecuencia_semanal INTEGER DEFAULT 3,
    fecha_generacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    activa BOOLEAN DEFAULT TRUE,
    generated_by_system BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_routine_client FOREIGN KEY (client_id) REFERENCES clients(id) ON DELETE CASCADE
);

-- =========================================
-- ROUTINE EXERCISES
-- =========================================
CREATE TABLE routine_exercises (
    id BIGSERIAL PRIMARY KEY,
    routine_id BIGINT NOT NULL,
    exercise_id BIGINT NOT NULL,
    dia_semana VARCHAR(20),
    series INTEGER NOT NULL,
    repeticiones INTEGER NOT NULL,
    descanso_segundos INTEGER,
    orden_ejercicio INTEGER,
    notas TEXT,
    CONSTRAINT fk_re_routine FOREIGN KEY (routine_id) REFERENCES routines(id) ON DELETE CASCADE,
    CONSTRAINT fk_re_exercise FOREIGN KEY (exercise_id) REFERENCES exercises(id)
);

-- =========================================
-- MEMBERSHIP PLANS
-- =========================================
CREATE TABLE membership_plans (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT,
    duracion_meses INTEGER NOT NULL,
    precio NUMERIC(10,2) NOT NULL,
    tipo VARCHAR(50),
    beneficios TEXT,
    activo BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =========================================
-- MEMBERSHIPS
-- =========================================
CREATE TABLE memberships (
    id BIGSERIAL PRIMARY KEY,
    client_id BIGINT NOT NULL,
    plan_id BIGINT NOT NULL,
    fecha_inicio DATE NOT NULL,
    fecha_vencimiento DATE NOT NULL,
    estado membership_status DEFAULT 'PENDIENTE_PAGO',
    renovacion_automatica BOOLEAN DEFAULT FALSE,
    activo BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_membership_client FOREIGN KEY (client_id) REFERENCES clients(id),
    CONSTRAINT fk_membership_plan FOREIGN KEY (plan_id) REFERENCES membership_plans(id)
);

-- =========================================
-- PAYMENTS
-- =========================================
CREATE TABLE payments (
    id BIGSERIAL PRIMARY KEY,
    client_id BIGINT NOT NULL,
    membership_id BIGINT,
    monto NUMERIC(10,2) NOT NULL,
    fecha_pago TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    metodo_pago payment_method NOT NULL,
    estado payment_status DEFAULT 'PENDIENTE',
    referencia VARCHAR(100),
    observaciones TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_payment_client FOREIGN KEY (client_id) REFERENCES clients(id),
    CONSTRAINT fk_payment_membership FOREIGN KEY (membership_id) REFERENCES memberships(id)
);

-- =========================================
-- PHYSICAL PROGRESS
-- =========================================
CREATE TABLE physical_progress (
    id BIGSERIAL PRIMARY KEY,
    client_id BIGINT NOT NULL,
    fecha DATE NOT NULL,
    peso NUMERIC(5,2),
    medida_cintura NUMERIC(5,2),
    medida_cadera NUMERIC(5,2),
    medida_pecho NUMERIC(5,2),
    rendimiento INTEGER,
    observaciones TEXT,
    recorded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_progress_client FOREIGN KEY (client_id) REFERENCES clients(id) ON DELETE CASCADE
);

-- =========================================
-- REPORTS
-- =========================================
CREATE TABLE reports (
    id BIGSERIAL PRIMARY KEY,
    tipo VARCHAR(50) NOT NULL,
    fecha_generacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    contenido TEXT,
    formato VARCHAR(20),
    generado_por BIGINT,
    CONSTRAINT fk_report_admin FOREIGN KEY (generado_por) REFERENCES users(id) ON DELETE SET NULL
);

-- =========================================
-- ÍNDICES
-- =========================================
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_rol ON users(rol);
CREATE INDEX idx_clients_objetivo ON clients(objetivo);
CREATE INDEX idx_clients_instructor ON clients(instructor_id);
CREATE INDEX idx_routines_client ON routines(client_id);
CREATE INDEX idx_payments_client ON payments(client_id);
CREATE INDEX idx_progress_client ON physical_progress(client_id);
CREATE INDEX idx_membership_client ON memberships(client_id);

-- =========================================
-- DATOS INICIALES
-- =========================================

-- Planes de membresía
INSERT INTO membership_plans (nombre, descripcion, duracion_meses, precio, tipo, beneficios, activo) VALUES
('Básico Mensual', 'Acceso a sala de pesas y cardio', 1, 29.99, 'BASICO', 'Horario libre, locker incluido', true),
('Premium Trimestral', 'Acceso completo + clases dirigidas', 3, 79.99, 'PREMIUM', 'Sala + clases + nutricionista', true),
('VIP Anual', 'Todo incluido + entrenador personal', 12, 299.99, 'VIP', 'Acceso 24/7, personal trainer, spa', true),
('Estudiante', 'Descuento para estudiantes', 1, 19.99, 'ESTUDIANTE', 'Horario restringido', true);

-- Ejercicios predefinidos
INSERT INTO exercises (nombre, descripcion, grupo_muscular, series, repeticiones, descanso_segundos, equipamiento, activo) VALUES
('Press de Banca', 'Ejercicio fundamental para pecho', 'Pecho', 4, 10, 90, 'Barra', true),
('Sentadilla', 'Ejercicio compuesto para piernas', 'Piernas', 4, 12, 90, 'Barra', true),
('Dominadas', 'Ejercicio para espalda', 'Espalda', 3, 8, 60, 'Barra fija', true),
('Curl de Bíceps', 'Aislamiento para brazos', 'Brazos', 3, 12, 45, 'Mancuernas', true),
('Plancha', 'Estabilidad core', 'Core', 3, 60, 30, 'Colchoneta', true),
('Burpees', 'Ejercicio cardiovascular', 'Cardio', 3, 15, 30, 'Peso corporal', true),
('Peso Muerto', 'Ejercicio para espalda baja', 'Espalda', 4, 8, 120, 'Barra', true),
('Press Militar', 'Desarrollo de hombros', 'Hombros', 4, 10, 60, 'Mancuernas', true);

-- Usuario ADMIN (contraseña: admin123 encriptada con BCrypt)
INSERT INTO users (nombre, apellido, email, contrasena, documento, telefono, rol, status, activo) VALUES
('Admin', 'Sistema', 'admin@gimnasio.com', '$2a$10$NkM5C6qQxUqXqXqXqXqXqO', '00000000', '123456789', 'ADMIN', 'ACTIVE', true);

INSERT INTO administradores (user_id, nivel_acceso) 
SELECT id, 'TOTAL' FROM users WHERE email = 'admin@gimnasio.com';

-- Usuario INSTRUCTOR (contraseña: instructor123)
INSERT INTO users (nombre, apellido, email, contrasena, documento, telefono, rol, status, activo) VALUES
('Carlos', 'Lopez', 'carlos.lopez@gimnasio.com', '$2a$10$NkM5C6qQxUqXqXqXqXqXqO', '11111111', '987654321', 'INSTRUCTOR', 'ACTIVE', true);

INSERT INTO instructors (user_id, especialidad, certificaciones, anos_experiencia, disponible, contract_type, salario, fecha_contratacion) 
SELECT id, 'Fuerza y Acondicionamiento', 'CrossFit Level 1, Personal Trainer Certificado', 5, true, 'FULL_TIME', 2500.00, '2023-01-15' 
FROM users WHERE email = 'carlos.lopez@gimnasio.com';

-- Usuario CLIENTE (contraseña: cliente123)
INSERT INTO users (nombre, apellido, email, contrasena, documento, telefono, rol, status, activo) VALUES
('María', 'García', 'maria.garcia@email.com', '$2a$10$NkM5C6qQxUqXqXqXqXqXqO', '22222222', '555123456', 'CLIENTE', 'ACTIVE', true);

INSERT INTO clients (user_id, peso, altura, objetivo, frecuencia_entrenamiento, fecha_nacimiento, genero, quiere_instructor, instructor_id) 
SELECT id, 65.5, 1.65, 'DEFINICION', '4 días/semana', '1995-05-15', 'FEMALE', true, (SELECT id FROM instructors LIMIT 1)
FROM users WHERE email = 'maria.garcia@email.com';

UPDATE clients SET imc = peso / (altura * altura) 
WHERE user_id = (SELECT id FROM users WHERE email = 'maria.garcia@email.com');

-- Membresía para el cliente
INSERT INTO memberships (client_id, plan_id, fecha_inicio, fecha_vencimiento, estado, renovacion_automatica) 
SELECT c.id, 2, CURRENT_DATE, CURRENT_DATE + INTERVAL '3 months', 'ACTIVA', false
FROM clients c JOIN users u ON c.user_id = u.id WHERE u.email = 'maria.garcia@email.com';

-- Rutina para el cliente
INSERT INTO routines (client_id, objetivo, nivel_dificultad, frecuencia_semanal, generated_by_system, activa)
SELECT c.id, 'DEFINICION', 'INTERMEDIO', 4, true, true
FROM clients c JOIN users u ON c.user_id = u.id WHERE u.email = 'maria.garcia@email.com';

