-- ============================================================
-- ATARA — Datos de muestra / Seed
-- Version : V2
--
-- Passwords en usuarios: marcadores de posición BCrypt válidos
-- (60 caracteres, formato correcto). Spring Security retornará
-- false para cualquier contraseña — NO lanzará excepción.
-- Reemplazar con hashes reales antes de cualquier prueba de auth.
-- Ver instrucciones en el bloque de INSERT INTO usuarios.
--
-- Jerarquía de inserción (respeta FKs):
--   roles → anios_lectivos → niveles → escalas_valoracion
--   → dimensiones_evaluacion → centros_educativos → materias
--   → periodos → usuarios → secciones → usuarios_secciones
--   → estudiantes → matriculas → contenidos → criterios_indicadores
--   → configuracion_alertas → evaluaciones → detalle_evaluacion
--   → alertas
-- ============================================================

-- ============================================================
-- 1. ROLES -- DATOS FIJOS
-- ============================================================
INSERT INTO roles (nombre, descripcion) VALUES
('ADMIN',        'Administrador del sistema con acceso total'),
('DOCENTE',      'Docente que registra evaluaciones y genera alertas'),
('COORDINADOR',  'Coordinador pedagógico con acceso a reportes globales');

-- ============================================================
-- 2. AÑO LECTIVO -- DATOS FLEXIBLES
-- ============================================================
INSERT INTO anios_lectivos (anio, fecha_inicio, fecha_fin, activo) VALUES
(2025, '2025-02-03', '2025-11-28', TRUE);

-- ============================================================
-- 3. NIVELES ACADÉMICOS (catálogo completo 1°–6°) -- DATOS FIJOS
-- ============================================================
INSERT INTO niveles (numero_grado, nombre, descripcion) VALUES
(1, 'Primer Grado',  'Primer año de educación básica primaria'),
(2, 'Segundo Grado', 'Segundo año de educación básica primaria'),
(3, 'Tercer Grado',  'Tercer año de educación básica primaria'),
(4, 'Cuarto Grado',  'Cuarto año de educación básica primaria'),
(5, 'Quinto Grado',  'Quinto año de educación básica primaria'),
(6, 'Sexto Grado',   'Sexto año de educación básica primaria');

-- ============================================================
-- 4. ESCALAS DE VALORACIÓN LIKERT -- DATOS FIJOS
-- ============================================================
INSERT INTO escalas_valoracion (nombre, etiqueta, valor_numerico, descripcion) VALUES
('Insuficiente', 'I', 1, 'El estudiante no alcanza los aprendizajes esperados'),
('Básico',       'B', 2, 'El estudiante alcanza los aprendizajes mínimos'),
('Satisfactorio','S', 3, 'El estudiante alcanza los aprendizajes esperados'),
('Destacado',    'D', 4, 'El estudiante supera los aprendizajes esperados');

-- ============================================================
-- 5. CENTROS EDUCATIVOS -- DATOS FLEXIBLES
-- ============================================================
INSERT INTO centros_educativos (nombre, circuito, direccion_regional, telefono, correo) VALUES
('Escuela Simón Bolívar',          '01',  'DREH San José Central', '2222-1111', 'bolivar@mep.go.cr'),
('Escuela República de Colombia',  '02',  'DREH San José Norte',   '2233-4455', 'colombia@mep.go.cr');

-- ============================================================
-- 6. PERÍODOS (5 períodos bimensuales en 2025) -- DATOS FLEXIBLES
-- ============================================================
INSERT INTO periodos (anio_lectivo_id, nombre, numero_periodo, fecha_inicio, fecha_fin, activo) VALUES
(1, 'Período 1 – 2025', 1, '2025-02-03', '2025-03-28', FALSE),
(1, 'Período 2 – 2025', 2, '2025-03-31', '2025-05-30', TRUE),   -- activo
(1, 'Período 3 – 2025', 3, '2025-06-02', '2025-07-25', FALSE),
(1, 'Período 4 – 2025', 4, '2025-07-28', '2025-09-26', FALSE),
(1, 'Período 5 – 2025', 5, '2025-09-29', '2025-11-28', FALSE);

-- ============================================================
-- 7. USUARIOS -- DATOS FLEXIBLES
--
-- ACCIÓN REQUERIDA antes de probar autenticación:
--   Los hashes de abajo tienen formato BCrypt válido (60 chars)
--   pero son marcadores de posición — Spring Security retornará
--   false (contraseña incorrecta) en lugar de lanzar excepción.
--
--   Para generar hashes reales de "Password123!" ejecutar una vez:
--     BCryptPasswordEncoder enc = new BCryptPasswordEncoder(12);
--     System.out.println(enc.encode("Password123!"));
--
--   Luego actualizar con:
--     UPDATE usuarios SET password = '<hash_generado>'
--     WHERE correo IN (
--         'admin@atara.mep.go.cr',
--         'mgarcia@atara.mep.go.cr',
--         'jperez@atara.mep.go.cr',
--         'avargas@atara.mep.go.cr'
--     );
-- ============================================================
INSERT INTO usuarios (nombre, apellidos, correo, password, rol_id) VALUES
('Carlos',  'Rodríguez Mora',   'admin@atara.mep.go.cr',     '$2a$12$xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx', 1),
('María',   'García Ulate',     'mgarcia@atara.mep.go.cr',   '$2a$12$xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx', 2),
('Juan',    'Pérez Brenes',     'jperez@atara.mep.go.cr',    '$2a$12$xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx', 3),
('Andrea',  'Vargas Solano',    'avargas@atara.mep.go.cr',   '$2a$12$xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx', 2);

-- ============================================================
-- 8. SECCIONES -- DATOS FLEXIBLES
-- ============================================================
INSERT INTO secciones (nombre, nivel_id, centro_id, anio_lectivo_id, docente_id, capacidad) VALUES
('A', 1, 1, 1, 2, 28),   -- Simón Bolívar  | 1° A | titular: María
('B', 1, 1, 1, 4, 30),   -- Simón Bolívar  | 1° B | titular: Andrea
('A', 2, 2, 1, 2, 25);   -- Rep. Colombia  | 2° A | titular: María

-- 8b. Asignar docentes a secciones
INSERT INTO usuarios_secciones (usuario_id, seccion_id) VALUES
(2, 1),  -- María   → Bolívar 1°A
(2, 3),  -- María   → Colombia 2°A
(4, 2);  -- Andrea  → Bolívar 1°B

-- ============================================================
-- 9. ESTUDIANTES -- DATOS FLEXIBLES
--
--  Solo datos personales estables. La asignación a la sección
--  del año 2025 se registra a continuación en matriculas.
-- ============================================================
INSERT INTO estudiantes
    (identificacion, nombre, apellido1, apellido2, fecha_nacimiento, genero,
     nombre_acudiente, telefono_acudiente, correo_acudiente)
VALUES
('2018-1001', 'Luis',   'Hernández', 'Campos',  '2018-03-15', 'M',
    'Roberto Hernández', '8801-1122', 'roberto.h@gmail.com'),
('2018-1002', 'Sofía',  'Ramírez',   'Jiménez', '2018-07-22', 'F',
    'Laura Ramírez',     '8803-3344', 'laura.r@gmail.com'),
('2018-1003', 'Andrés', 'Torres',    'Mora',    '2018-11-05', 'M',
    'Gloria Torres',     '8805-5566', 'gloria.t@gmail.com');

-- ============================================================
-- 10. MATRÍCULAS — asignación de estudiantes a secciones -- DATOS FLEXIBLES
--
--  Los tres estudiantes quedan asignados a Bolívar 1°A (seccion_id=1)
--  para el año lectivo 2025 (anio_lectivo_id=1).
--  La restricción UNIQUE (estudiante_id, anio_lectivo_id) garantiza
--  que un estudiante no pueda aparecer en dos secciones el mismo año.
-- ============================================================
INSERT INTO matriculas (estudiante_id, seccion_id, anio_lectivo_id, fecha_matricula) VALUES
(1, 1, 1, '2025-02-03'),  -- Luis   → Bolívar 1°A, año 2025
(2, 1, 1, '2025-02-03'),  -- Sofía  → Bolívar 1°A, año 2025
(3, 1, 1, '2025-02-03');  -- Andrés → Bolívar 1°A, año 2025

-- ============================================================
-- 11. MATERIAS -- DATOS FIJOS
-- ============================================================
INSERT INTO materias (nombre, descripcion) VALUES
('Matemáticas',        'Aritmética, geometría y pensamiento lógico'),
('Español',            'Lectura, escritura y expresión oral'),
('Ciencias',           'Ciencias naturales y el entorno'),
('Estudios Sociales',  'Historia, geografía y ciudadanía'),
('Educación Física',   'Desarrollo motriz, deporte y salud');

-- ============================================================
-- 12. CONTENIDOS (sample: 2 por materia relevante) -- DATOS FLEXIBLES
-- ============================================================
INSERT INTO contenidos (nombre, descripcion, materia_id) VALUES
-- Matemáticas (materia_id = 1)
('Operaciones básicas',        'Suma, resta, multiplicación y división',               1),
('Resolución de problemas',    'Aplicación de operaciones en contextos cotidianos',    1),
-- Español (materia_id = 2)
('Comprensión lectora',        'Lectura e interpretación de textos',                   2),
('Expresión escrita',          'Producción de textos cortos y estructurados',          2),
-- Ciencias (materia_id = 3)
('El ser vivo y su entorno',   'Ecosistemas, seres vivos y adaptaciones',              3);

-- ============================================================
-- 13. DIMENSIONES DE EVALUACIÓN -- DATOS FIJOS
-- ============================================================
INSERT INTO dimensiones_evaluacion (nombre, descripcion, peso) VALUES
('Rendimiento Académico',     'Desempeño en las áreas curriculares',                  2.0),
('Participación',             'Participación activa e interacción en el aula',        1.0),
('Hábitos de Estudio',        'Organización, responsabilidad y dedicación',           1.5),
('Factores Socioemocionales', 'Bienestar emocional y relaciones interpersonales',     1.5),
('Asistencia',                'Regularidad y puntualidad en la asistencia',           1.0);

-- ============================================================
-- 14. CRITERIOS / INDICADORES -- DATOS FLEXIBLES
-- ============================================================
INSERT INTO criterios_indicadores (nombre, descripcion, contenido_id, dimension_id, peso) VALUES
-- Contenido: Operaciones básicas (id=1) → Rendimiento Académico (id=1)
('Dominio de la suma y resta',
    'Resuelve operaciones de suma y resta con precisión',    1, 1, 1.0),
('Dominio de la multiplicación y división',
    'Resuelve operaciones de multiplicación y división',     1, 1, 1.0),

-- Contenido: Comprensión lectora (id=3) → Rendimiento Académico (id=1)
('Comprensión de textos narrativos',
    'Identifica personajes, lugar y secuencia en un relato', 3, 1, 1.0),

-- Contenido: Comprensión lectora (id=3) → Participación (id=2)
('Participación en discusión de lectura',
    'Interviene con aportes durante la discusión del texto', 3, 2, 1.0),

-- Contenido: Operaciones básicas (id=1) → Hábitos de Estudio (id=3)
('Entrega puntual de tareas de Matemáticas',
    'Cumple con las asignaciones en los plazos establecidos', 1, 3, 1.0),

-- Contenido: El ser vivo (id=5) → Factores Socioemocionales (id=4)
('Trabajo colaborativo en Ciencias',
    'Coopera activamente en prácticas y proyectos grupales', 5, 4, 1.0),

-- Contenido: Operaciones básicas (id=1) → Asistencia (id=5)
('Asistencia a lecciones de Matemáticas',
    'Regularidad en la asistencia a las lecciones',          1, 5, 1.0);

-- ============================================================
-- 15. CONFIGURACIÓN DE ALERTAS (motor de reglas) -- DATOS FLEXIBLES
-- ============================================================
INSERT INTO configuracion_alertas
    (nombre, descripcion, umbral_minimo, umbral_maximo, tipo_alerta, nivel_resultante) VALUES
('Alerta Preventiva – Rendimiento Bajo',
    'Promedio entre 1.5 y 2.0 en criterios académicos',
    1.50, 2.00, 'PREVENTIVA', 'BAJO'),
('Alerta Moderada – Rendimiento Deficiente',
    'Promedio entre 1.0 y 1.49 en criterios académicos',
    1.00, 1.49, 'MODERADA',   'MEDIO'),
('Alerta Crítica – Sin aprendizajes mínimos',
    'Promedio menor a 1.0 en cualquier dimensión',
    NULL, 0.99, 'CRITICA',    'ALTO'),
('Alerta Preventiva – Inasistencia',
    'Más de 3 ausencias injustificadas en el período',
    NULL, NULL,  'PREVENTIVA', 'BAJO');

-- ============================================================
-- 16. EVALUACIÓN — cabecera (Luis, Período 2, sección 1) -- DATOS FLEXIBLES
--
--  seccion_id=1 se toma de la matrícula de Luis en 2025.
--  En producción, el backend lee matriculas para derivar seccion_id.
-- ============================================================
INSERT INTO evaluaciones
    (estudiante_id, periodo_id, usuario_id, seccion_id, observacion_general, origen_registro)
VALUES
(1, 2, 2, 1,
 'Luis muestra dificultad en Matemáticas y baja asistencia. Participación limitada.',
 'MANUAL');

-- ============================================================
-- 17. DETALLE DE EVALUACIÓN (7 criterios evaluados) -- DATOS FLEXIBLES
--     escala_id: 1=Insuficiente  2=Básico  3=Satisfactorio  4=Destacado
-- ============================================================
INSERT INTO detalle_evaluacion (evaluacion_id, criterio_id, escala_id, observacion) VALUES
(1, 1, 2, 'Resuelve sumas pero comete errores en restas con llevada'),
(1, 2, 1, 'No domina la tabla de multiplicar; requiere refuerzo inmediato'),
(1, 3, 3, 'Comprende el hilo narrativo de los textos leídos en clase'),
(1, 4, 2, 'Participa solo cuando el docente le pregunta directamente'),
(1, 5, 2, 'Ha entregado tarde 3 de las 5 tareas del período'),
(1, 6, 3, 'Se integra adecuadamente en los trabajos de grupo'),
(1, 7, 2, 'Faltó 4 lecciones durante el período sin justificación');

-- ============================================================
-- 18. ALERTA (generada con base en criterio 2 – Multiplicación)
-- ============================================================
INSERT INTO alertas
    (estudiante_id, contenido_id, periodo_id, config_alerta_id,
     generada_por, tipo_alerta, nivel_alerta, motivo)
VALUES
(1, 1, 2, 2,
 2,
 'MODERADA', 'MEDIO',
 'Luis Hernández obtuvo calificación Insuficiente (1/4) en "Dominio de la '
 'multiplicación y división". Requiere plan de refuerzo pedagógico diferenciado '
 'y comunicación inmediata con el acudiente.');
