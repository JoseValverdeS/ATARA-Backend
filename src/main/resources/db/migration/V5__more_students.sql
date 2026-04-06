-- ============================================================
-- ATARA — Estudiantes adicionales
--
-- Version : V5
--
-- Agrega 27 estudiantes más (≈10 por sección) y sus matrículas
-- para el año lectivo 2025 (anio_lectivo_id = 1):
--
--   Sección 1 — Bolívar 1°A  → estudiantes  4–11  (7 nuevos + 3 existentes = 10 total)
--   Sección 2 — Bolívar 1°B  → estudiantes 12–21  (10 nuevos)
--   Sección 3 — Colombia 2°A → estudiantes 22–31  (10 nuevos)
-- ============================================================

-- ============================================================
-- 1. ESTUDIANTES
-- ============================================================
INSERT INTO estudiantes
    (identificacion, nombre, apellido1, apellido2, fecha_nacimiento, genero,
     nombre_acudiente, telefono_acudiente, correo_acudiente)
VALUES
-- ── Bolívar 1°A — adicionales (nacidos 2018) ─────────────────
('2018-1004', 'Valentina', 'Mora',       'Vargas',   '2018-02-10', 'F', 'Carmen Mora',       '8811-1100', 'carmen.mora@gmail.com'),
('2018-1005', 'Diego',     'Solís',      'Brenes',   '2018-05-03', 'M', 'Jorge Solís',       '8812-2200', 'jorge.solis@gmail.com'),
('2018-1006', 'Camila',    'Castro',     'Rojas',    '2018-08-17', 'F', 'Patricia Castro',   '8813-3300', 'patricia.castro@gmail.com'),
('2018-1007', 'Mateo',     'Jiménez',    'Ureña',    '2018-01-28', 'M', 'Ricardo Jiménez',   '8814-4400', 'ricardo.jimenez@gmail.com'),
('2018-1008', 'Isabella',  'Vargas',     'Fallas',   '2018-09-12', 'F', 'Marianela Vargas',  '8815-5500', 'marianela.vargas@gmail.com'),
('2018-1009', 'Sebastián', 'Blanco',     'Aguilar',  '2018-04-25', 'M', 'Fernando Blanco',   '8816-6600', 'fernando.blanco@gmail.com'),
('2018-1010', 'Lucía',     'Picado',     'Herrera',  '2018-11-07', 'F', 'Sandra Picado',     '8817-7700', 'sandra.picado@gmail.com'),

-- ── Bolívar 1°B — nuevos (nacidos 2018) ──────────────────────
('2018-2001', 'Mariana',   'Rodríguez',  'Calvo',    '2018-03-19', 'F', 'Laura Rodríguez',   '8821-1100', 'laura.rodriguez@gmail.com'),
('2018-2002', 'Gabriel',   'Esquivel',   'Chaves',   '2018-06-08', 'M', 'Oscar Esquivel',    '8822-2200', 'oscar.esquivel@gmail.com'),
('2018-2003', 'Valeria',   'Vega',       'Morales',  '2018-10-14', 'F', 'Ana Vega',          '8823-3300', 'ana.vega@gmail.com'),
('2018-2004', 'Daniel',    'Camacho',    'Segura',   '2018-02-27', 'M', 'Luis Camacho',      '8824-4400', 'luis.camacho@gmail.com'),
('2018-2005', 'Natalia',   'Portuguez',  'Arias',    '2018-07-31', 'F', 'Rosa Portuguez',    '8825-5500', 'rosa.portuguez@gmail.com'),
('2018-2006', 'Alejandro', 'Méndez',     'Quirós',   '2018-12-02', 'M', 'Carlos Méndez',     '8826-6600', 'carlos.mendez@gmail.com'),
('2018-2007', 'Daniela',   'Porras',     'Solano',   '2018-05-16', 'F', 'Silvia Porras',     '8827-7700', 'silvia.porras@gmail.com'),
('2018-2008', 'Josué',     'Araya',      'Benavides','2018-09-23', 'M', 'Mauricio Araya',    '8828-8800', 'mauricio.araya@gmail.com'),
('2018-2009', 'Fernanda',  'Meza',       'Gamboa',   '2018-01-09', 'F', 'Diana Meza',        '8829-9900', 'diana.meza@gmail.com'),
('2018-2010', 'Ricardo',   'Ugalde',     'Soto',     '2018-08-05', 'M', 'Walter Ugalde',     '8820-0011', 'walter.ugalde@gmail.com'),

-- ── Colombia 2°A — nuevos (nacidos 2017) ─────────────────────
('2017-3001', 'Paola',       'Monge',    'Alfaro',     '2017-04-11', 'F', 'Yolanda Monge',    '8831-1100', 'yolanda.monge@gmail.com'),
('2017-3002', 'Bryan',       'Navarro',  'Chavarría',  '2017-07-24', 'M', 'Esteban Navarro',  '8832-2200', 'esteban.navarro@gmail.com'),
('2017-3003', 'Adriana',     'Pérez',    'Sanabria',   '2017-02-18', 'F', 'Mónica Pérez',     '8833-3300', 'monica.perez@gmail.com'),
('2017-3004', 'Kevin',       'Salazar',  'Gutiérrez',  '2017-10-30', 'M', 'Roberto Salazar',  '8834-4400', 'roberto.salazar@gmail.com'),
('2017-3005', 'Stephanie',   'Cordero',  'Madrigal',   '2017-06-06', 'F', 'Irene Cordero',    '8835-5500', 'irene.cordero@gmail.com'),
('2017-3006', 'Jonathan',    'Ulate',    'Zamora',     '2017-03-22', 'M', 'Héctor Ulate',     '8836-6600', 'hector.ulate@gmail.com'),
('2017-3007', 'Michelle',    'Bonilla',  'Mora',       '2017-11-15', 'F', 'Andrea Bonilla',   '8837-7700', 'andrea.bonilla@gmail.com'),
('2017-3008', 'Christopher', 'Rojas',    'Badilla',    '2017-08-09', 'M', 'Pablo Rojas',      '8838-8800', 'pablo.rojas@gmail.com'),
('2017-3009', 'Alexa',       'Segura',   'Espinoza',   '2017-05-14', 'F', 'Lorena Segura',    '8839-9900', 'lorena.segura@gmail.com'),
('2017-3010', 'Marco',       'Vargas',   'Rodríguez',  '2017-01-27', 'M', 'Álvaro Vargas',    '8830-0011', 'alvaro.vargas@gmail.com');

-- ============================================================
-- 2. MATRÍCULAS — año lectivo 2025 (anio_lectivo_id = 1)
-- ============================================================
INSERT INTO matriculas (estudiante_id, seccion_id, anio_lectivo_id, fecha_matricula)
VALUES
-- Bolívar 1°A (seccion_id = 1)
(4,  1, 1, '2025-02-03'),
(5,  1, 1, '2025-02-03'),
(6,  1, 1, '2025-02-03'),
(7,  1, 1, '2025-02-03'),
(8,  1, 1, '2025-02-03'),
(9,  1, 1, '2025-02-03'),
(10, 1, 1, '2025-02-03'),

-- Bolívar 1°B (seccion_id = 2)
(11, 2, 1, '2025-02-03'),
(12, 2, 1, '2025-02-03'),
(13, 2, 1, '2025-02-03'),
(14, 2, 1, '2025-02-03'),
(15, 2, 1, '2025-02-03'),
(16, 2, 1, '2025-02-03'),
(17, 2, 1, '2025-02-03'),
(18, 2, 1, '2025-02-03'),
(19, 2, 1, '2025-02-03'),
(20, 2, 1, '2025-02-03'),

-- Colombia 2°A (seccion_id = 3)
(21, 3, 1, '2025-02-03'),
(22, 3, 1, '2025-02-03'),
(23, 3, 1, '2025-02-03'),
(24, 3, 1, '2025-02-03'),
(25, 3, 1, '2025-02-03'),
(26, 3, 1, '2025-02-03'),
(27, 3, 1, '2025-02-03'),
(28, 3, 1, '2025-02-03'),
(29, 3, 1, '2025-02-03'),
(30, 3, 1, '2025-02-03');
