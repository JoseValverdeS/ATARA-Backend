-- ============================================================
-- ATARA — Usuario docente de prueba: Keylor Cortés Cascante
--
-- Version : V9
--
-- Crea:
--   1. Usuario Keylor Cortés Cascante (DOCENTE) - contraseña: Admin1234!
--   2. Sección 4°A en Escuela Simón Bolívar para el año 2025
--   3. Asigna a Keylor como docente titular de esa sección
--   4. Asigna la sección a Keylor vía usuarios_secciones
--   5. Asigna únicamente Matemáticas a Keylor vía usuario_materias
--   6. Agrega 5 estudiantes de 4° grado y los matricula en esa sección
-- ============================================================

-- ============================================================
-- 1. USUARIO
-- ============================================================
INSERT INTO usuarios (nombre, apellidos, correo, password, rol_id)
VALUES (
    'Keylor',
    'Cortés Cascante',
    'kcortes@atara.mep.go.cr',
    '$2a$10$F1U30x64ierJnoOw7Dx8MuaqkbgIVzfdrZ38.uQRGz24uivyd96dm',
    (SELECT id FROM roles WHERE nombre = 'DOCENTE')
);

-- ============================================================
-- 2. SECCIÓN: 4°A en Bolívar, año 2025
-- ============================================================
INSERT INTO secciones (nombre, nivel_id, centro_id, anio_lectivo_id, docente_id, capacidad)
VALUES (
    'A',
    (SELECT id FROM niveles WHERE numero_grado = 4),
    (SELECT id FROM centros_educativos WHERE nombre = 'Escuela Simón Bolívar'),
    (SELECT id FROM anios_lectivos WHERE anio = 2025),
    (SELECT id FROM usuarios WHERE correo = 'kcortes@atara.mep.go.cr'),
    30
);

-- ============================================================
-- 3. ASIGNAR SECCIÓN AL USUARIO (usuarios_secciones)
-- ============================================================
INSERT INTO usuarios_secciones (usuario_id, seccion_id)
VALUES (
    (SELECT id FROM usuarios WHERE correo = 'kcortes@atara.mep.go.cr'),
    (SELECT id FROM secciones
     WHERE nivel_id = (SELECT id FROM niveles WHERE numero_grado = 4)
       AND centro_id = (SELECT id FROM centros_educativos WHERE nombre = 'Escuela Simón Bolívar')
       AND anio_lectivo_id = (SELECT id FROM anios_lectivos WHERE anio = 2025)
       AND nombre = 'A')
);

-- ============================================================
-- 4. ASIGNAR SOLO MATEMÁTICAS AL DOCENTE (usuario_materias)
-- ============================================================
INSERT INTO usuario_materias (usuario_id, materia_id)
VALUES (
    (SELECT id FROM usuarios WHERE correo = 'kcortes@atara.mep.go.cr'),
    (SELECT id FROM materias WHERE clave = 'MATEMATICAS')
);

-- ============================================================
-- 5. ESTUDIANTES DE 4° GRADO
-- ============================================================
INSERT INTO estudiantes
    (identificacion, nombre, apellido1, apellido2, fecha_nacimiento, genero,
     nombre_acudiente, telefono_acudiente, correo_acudiente)
VALUES
('4001-2015', 'Sofía',    'Mora',      'Jiménez',  '2015-03-10', 'F', 'Laura Jiménez',    '8811-0001', 'laura.jimenez@correo.cr'),
('4002-2015', 'Diego',    'Quirós',    'Ulate',    '2015-07-22', 'M', 'Roberto Quirós',   '8811-0002', 'roberto.quiros@correo.cr'),
('4003-2015', 'Valentina','Sandoval',  'Mora',     '2015-01-05', 'F', 'Carmen Mora',      '8811-0003', 'carmen.mora@correo.cr'),
('4004-2015', 'Sebastián','Ramírez',   'Blanco',   '2015-11-18', 'M', 'Patricia Blanco',  '8811-0004', 'patricia.blanco@correo.cr'),
('4005-2015', 'Daniela',  'Castro',    'Vega',     '2015-05-30', 'F', 'Marcos Vega',      '8811-0005', 'marcos.vega@correo.cr');

-- ============================================================
-- 6. MATRÍCULAS EN 4°A - BOLÍVAR 2025
-- ============================================================
INSERT INTO matriculas (estudiante_id, seccion_id, anio_lectivo_id, fecha_matricula)
SELECT
    e.id,
    s.id,
    a.id,
    '2025-02-03'
FROM estudiantes e
CROSS JOIN secciones s
CROSS JOIN anios_lectivos a
WHERE e.identificacion IN ('4001-2015','4002-2015','4003-2015','4004-2015','4005-2015')
  AND s.nivel_id = (SELECT id FROM niveles WHERE numero_grado = 4)
  AND s.centro_id = (SELECT id FROM centros_educativos WHERE nombre = 'Escuela Simón Bolívar')
  AND s.nombre = 'A'
  AND a.anio = 2025;
