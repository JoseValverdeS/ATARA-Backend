-- ============================================================
-- ATARA — Materias y separación de saberes por asignatura
--
-- Version : V6
--
-- Nota: La tabla materias ya existe desde V1/V2 con columnas
--       (id INTEGER, nombre, descripcion, created_at, updated_at).
--       Este script la adapta y añade el resto del esquema.
--
-- Cambios:
--   1. Añade columna clave a materias
--   2. Columna materia_id en ejes_tematicos
--   3. Nuevos ejes para Matemáticas, Ciencias y Estudios Sociales
--   4. Columna materia_id en evaluaciones_saber
--   5. Columna materia_id en alertas_tematicas (desnormalizada)
--   6. Actualización de vistas para incluir materia
-- ============================================================

-- ============================================================
-- 1. COLUMNA clave EN LA TABLA materias EXISTENTE
-- ============================================================
ALTER TABLE materias ADD COLUMN clave VARCHAR(30);

UPDATE materias SET clave = 'MATEMATICAS'      WHERE nombre = 'Matemáticas';
UPDATE materias SET clave = 'ESPANOL'           WHERE nombre = 'Español';
UPDATE materias SET clave = 'CIENCIAS'          WHERE nombre = 'Ciencias';
UPDATE materias SET clave = 'ESTUDIOS_SOCIALES' WHERE nombre = 'Estudios Sociales';
UPDATE materias SET clave = 'EDUCACION_FISICA'  WHERE nombre = 'Educación Física';
-- Fallback para cualquier otra materia sin clave
UPDATE materias SET clave = UPPER(REPLACE(REPLACE(nombre, ' ', '_'), 'á','a'))
    WHERE clave IS NULL;

ALTER TABLE materias ALTER COLUMN clave SET NOT NULL;
ALTER TABLE materias ADD CONSTRAINT uq_materia_clave UNIQUE (clave);

-- ============================================================
-- 2. COLUMNA materia_id EN ejes_tematicos
-- ============================================================
ALTER TABLE ejes_tematicos
    ADD COLUMN materia_id INTEGER REFERENCES materias(id) ON DELETE RESTRICT;

-- Los 21 ejes actuales son de Español
UPDATE ejes_tematicos
    SET materia_id = (SELECT id FROM materias WHERE clave = 'ESPANOL');

ALTER TABLE ejes_tematicos ALTER COLUMN materia_id SET NOT NULL;

-- Reemplazar constraint de unicidad (tipo_saber, orden) → (materia, tipo_saber, orden)
ALTER TABLE ejes_tematicos DROP CONSTRAINT uq_eje_por_tipo;
ALTER TABLE ejes_tematicos
    ADD CONSTRAINT uq_eje_por_materia_tipo UNIQUE (materia_id, tipo_saber_id, orden);

CREATE INDEX idx_ejes_materia ON ejes_tematicos(materia_id);

-- ============================================================
-- 3. EJES PARA MATEMÁTICAS
-- ============================================================

-- Conceptual (tipo_saber_id = 1)
INSERT INTO ejes_tematicos (materia_id, tipo_saber_id, clave, nombre, descripcion, orden) VALUES
((SELECT id FROM materias WHERE clave='MATEMATICAS'), 1, 'MC_NUMEROS',      'Números y operaciones',              'Comprensión del sistema numérico, valor posicional y operaciones básicas', 1),
((SELECT id FROM materias WHERE clave='MATEMATICAS'), 1, 'MC_FRACCIONES',   'Fracciones, decimales y porcentajes','Conceptos y relaciones entre fracciones, decimales y porcentajes', 2),
((SELECT id FROM materias WHERE clave='MATEMATICAS'), 1, 'MC_GEOMETRIA',    'Geometría y medición',               'Figuras geométricas, propiedades y sistemas de medida', 3),
((SELECT id FROM materias WHERE clave='MATEMATICAS'), 1, 'MC_ALGEBRA',      'Álgebra y patrones',                 'Patrones numéricos, propiedades algebraicas y ecuaciones básicas', 4),
((SELECT id FROM materias WHERE clave='MATEMATICAS'), 1, 'MC_ESTADISTICA',  'Estadística y probabilidad',         'Recopilación, representación e interpretación de datos; nociones de probabilidad', 5),
((SELECT id FROM materias WHERE clave='MATEMATICAS'), 1, 'MC_PROBLEMAS',    'Resolución de problemas',            'Estrategias y conceptos para abordar problemas matemáticos contextualizados', 6),
((SELECT id FROM materias WHERE clave='MATEMATICAS'), 1, 'MC_RAZONAMIENTO', 'Razonamiento lógico-matemático',     'Argumentación, justificación y pensamiento crítico en contextos matemáticos', 7);

-- Procedimental (tipo_saber_id = 2)
INSERT INTO ejes_tematicos (materia_id, tipo_saber_id, clave, nombre, descripcion, orden) VALUES
((SELECT id FROM materias WHERE clave='MATEMATICAS'), 2, 'MP_NUMEROS',      'Cálculo y operaciones aplicadas',      'Ejecución fluida de operaciones y estimaciones en contextos reales', 1),
((SELECT id FROM materias WHERE clave='MATEMATICAS'), 2, 'MP_FRACCIONES',   'Operaciones con fracciones y decimales','Aplicación de fracciones y decimales en situaciones concretas', 2),
((SELECT id FROM materias WHERE clave='MATEMATICAS'), 2, 'MP_GEOMETRIA',    'Construcción y medición',               'Trazado, construcción y medición de figuras geométricas', 3),
((SELECT id FROM materias WHERE clave='MATEMATICAS'), 2, 'MP_ALGEBRA',      'Uso de patrones y expresiones',         'Identificación y extensión de patrones; uso de variables simples', 4),
((SELECT id FROM materias WHERE clave='MATEMATICAS'), 2, 'MP_ESTADISTICA',  'Representación e interpretación de datos','Construcción e interpretación de gráficos y tablas estadísticas', 5),
((SELECT id FROM materias WHERE clave='MATEMATICAS'), 2, 'MP_PROBLEMAS',    'Aplicación de estrategias de resolución','Selección y ejecución de estrategias para resolver problemas', 6),
((SELECT id FROM materias WHERE clave='MATEMATICAS'), 2, 'MP_RAZONAMIENTO', 'Argumentación matemática',               'Uso de evidencia matemática para justificar soluciones y procesos', 7);

-- Actitudinal (tipo_saber_id = 3)
INSERT INTO ejes_tematicos (materia_id, tipo_saber_id, clave, nombre, descripcion, orden) VALUES
((SELECT id FROM materias WHERE clave='MATEMATICAS'), 3, 'MA_NUMEROS',      'Disposición hacia el cálculo',       'Confianza y perseverancia en la práctica de operaciones matemáticas', 1),
((SELECT id FROM materias WHERE clave='MATEMATICAS'), 3, 'MA_FRACCIONES',   'Precisión y cuidado numérico',       'Actitud de exactitud y verificación en el trabajo con fracciones y decimales', 2),
((SELECT id FROM materias WHERE clave='MATEMATICAS'), 3, 'MA_GEOMETRIA',    'Orden y rigor geométrico',            'Cuidado y método en la construcción geométrica', 3),
((SELECT id FROM materias WHERE clave='MATEMATICAS'), 3, 'MA_ALGEBRA',      'Curiosidad por los patrones',         'Interés por descubrir regularidades y relaciones algebraicas', 4),
((SELECT id FROM materias WHERE clave='MATEMATICAS'), 3, 'MA_ESTADISTICA',  'Valoración del análisis de datos',    'Reconocimiento de la utilidad de la estadística en la toma de decisiones', 5),
((SELECT id FROM materias WHERE clave='MATEMATICAS'), 3, 'MA_PROBLEMAS',    'Actitud ante los retos matemáticos',  'Disposición, creatividad y tolerancia ante problemas matemáticos', 6),
((SELECT id FROM materias WHERE clave='MATEMATICAS'), 3, 'MA_RAZONAMIENTO', 'Valoración del pensamiento lógico',   'Aprecio por la argumentación rigurosa y el razonamiento sistemático', 7);

-- ============================================================
-- 4. EJES PARA CIENCIAS
-- ============================================================

-- Conceptual (tipo_saber_id = 1)
INSERT INTO ejes_tematicos (materia_id, tipo_saber_id, clave, nombre, descripcion, orden) VALUES
((SELECT id FROM materias WHERE clave='CIENCIAS'), 1, 'CC_SERES_VIVOS',  'Seres vivos y biodiversidad',         'Características, clasificación y funciones de los seres vivos', 1),
((SELECT id FROM materias WHERE clave='CIENCIAS'), 1, 'CC_CUERPO',       'Cuerpo humano y salud',               'Sistemas del cuerpo humano, salud y prevención de enfermedades', 2),
((SELECT id FROM materias WHERE clave='CIENCIAS'), 1, 'CC_MATERIA',      'Materia y energía',                   'Propiedades, estados y transformaciones de la materia; formas de energía', 3),
((SELECT id FROM materias WHERE clave='CIENCIAS'), 1, 'CC_ECOSISTEMAS',  'Ecosistemas y ambiente',              'Relaciones entre seres vivos y su ambiente; cadenas alimenticias', 4),
((SELECT id FROM materias WHERE clave='CIENCIAS'), 1, 'CC_FENOMENOS',    'Fenómenos físicos y naturales',       'Comprensión de fenómenos físicos, meteorológicos y geológicos', 5),
((SELECT id FROM materias WHERE clave='CIENCIAS'), 1, 'CC_METODO',       'Metodología científica',              'Conceptos del método científico: hipótesis, experimento, conclusión', 6),
((SELECT id FROM materias WHERE clave='CIENCIAS'), 1, 'CC_TECNOLOGIA',   'Tecnología y sociedad',               'Relación entre ciencia, tecnología, ambiente y calidad de vida', 7);

-- Procedimental (tipo_saber_id = 2)
INSERT INTO ejes_tematicos (materia_id, tipo_saber_id, clave, nombre, descripcion, orden) VALUES
((SELECT id FROM materias WHERE clave='CIENCIAS'), 2, 'CP_SERES_VIVOS',  'Clasificación y observación de seres vivos','Uso de criterios para clasificar y observar organismos', 1),
((SELECT id FROM materias WHERE clave='CIENCIAS'), 2, 'CP_CUERPO',       'Cuidado y prácticas de salud',             'Aplicación de hábitos y prácticas para preservar la salud', 2),
((SELECT id FROM materias WHERE clave='CIENCIAS'), 2, 'CP_MATERIA',      'Experimentación con materia y energía',     'Realización de experimentos básicos con propiedades y transformaciones', 3),
((SELECT id FROM materias WHERE clave='CIENCIAS'), 2, 'CP_ECOSISTEMAS',  'Análisis de relaciones ecológicas',         'Identificación y análisis de cadenas tróficas y relaciones ecosistémicas', 4),
((SELECT id FROM materias WHERE clave='CIENCIAS'), 2, 'CP_FENOMENOS',    'Observación y registro de fenómenos',       'Observación sistemática y registro de fenómenos naturales', 5),
((SELECT id FROM materias WHERE clave='CIENCIAS'), 2, 'CP_METODO',       'Aplicación del método científico',          'Diseño y ejecución de sencillos experimentos escolares', 6),
((SELECT id FROM materias WHERE clave='CIENCIAS'), 2, 'CP_TECNOLOGIA',   'Uso responsable de tecnología',             'Aplicación de herramientas tecnológicas con criterio ambiental', 7);

-- Actitudinal (tipo_saber_id = 3)
INSERT INTO ejes_tematicos (materia_id, tipo_saber_id, clave, nombre, descripcion, orden) VALUES
((SELECT id FROM materias WHERE clave='CIENCIAS'), 3, 'CA_SERES_VIVOS',  'Respeto por la vida y biodiversidad',  'Valoración y cuidado de la diversidad biológica', 1),
((SELECT id FROM materias WHERE clave='CIENCIAS'), 3, 'CA_CUERPO',       'Responsabilidad con la salud propia',  'Disposición a adoptar hábitos saludables y preventivos', 2),
((SELECT id FROM materias WHERE clave='CIENCIAS'), 3, 'CA_MATERIA',      'Curiosidad científica',                'Interés y asombro ante fenómenos de la naturaleza', 3),
((SELECT id FROM materias WHERE clave='CIENCIAS'), 3, 'CA_ECOSISTEMAS',  'Compromiso ambiental',                 'Actitud de cuidado, conservación y responsabilidad ambiental', 4),
((SELECT id FROM materias WHERE clave='CIENCIAS'), 3, 'CA_FENOMENOS',    'Observación crítica del entorno',      'Disposición a observar, preguntar y cuestionar el entorno natural', 5),
((SELECT id FROM materias WHERE clave='CIENCIAS'), 3, 'CA_METODO',       'Actitud científica y honestidad',      'Rigor, honestidad y perseverancia en el trabajo científico escolar', 6),
((SELECT id FROM materias WHERE clave='CIENCIAS'), 3, 'CA_TECNOLOGIA',   'Uso ético de la tecnología',           'Conciencia sobre el impacto social y ambiental del uso tecnológico', 7);

-- ============================================================
-- 5. EJES PARA ESTUDIOS SOCIALES
-- ============================================================

-- Conceptual (tipo_saber_id = 1)
INSERT INTO ejes_tematicos (materia_id, tipo_saber_id, clave, nombre, descripcion, orden) VALUES
((SELECT id FROM materias WHERE clave='ESTUDIOS_SOCIALES'), 1, 'SC_HISTORIA',   'Historia y cronología',            'Hechos históricos costarricenses y mundiales; líneas de tiempo y contexto', 1),
((SELECT id FROM materias WHERE clave='ESTUDIOS_SOCIALES'), 1, 'SC_GEOGRAFIA',  'Geografía y territorio',           'Características físicas y humanas del territorio costarricense y mundial', 2),
((SELECT id FROM materias WHERE clave='ESTUDIOS_SOCIALES'), 1, 'SC_IDENTIDAD',  'Identidad costarricense',          'Cultura, tradiciones, símbolos patrios e identidad nacional', 3),
((SELECT id FROM materias WHERE clave='ESTUDIOS_SOCIALES'), 1, 'SC_POLITICA',   'Organización política y cívica',   'Sistemas de gobierno, instituciones democráticas y participación ciudadana', 4),
((SELECT id FROM materias WHERE clave='ESTUDIOS_SOCIALES'), 1, 'SC_ECONOMIA',   'Economía y desarrollo sostenible', 'Conceptos económicos básicos, producción, consumo y sostenibilidad', 5),
((SELECT id FROM materias WHERE clave='ESTUDIOS_SOCIALES'), 1, 'SC_DERECHOS',   'Derechos humanos y ciudadanía',    'Derechos y deberes del ciudadano; Declaración Universal de DDHH', 6),
((SELECT id FROM materias WHERE clave='ESTUDIOS_SOCIALES'), 1, 'SC_SOCIEDAD',   'Interacción sociocultural',        'Diversidad cultural, intercambio social y convivencia pacífica', 7);

-- Procedimental (tipo_saber_id = 2)
INSERT INTO ejes_tematicos (materia_id, tipo_saber_id, clave, nombre, descripcion, orden) VALUES
((SELECT id FROM materias WHERE clave='ESTUDIOS_SOCIALES'), 2, 'SP_HISTORIA',   'Análisis de fuentes históricas',    'Lectura e interpretación de fuentes primarias y secundarias', 1),
((SELECT id FROM materias WHERE clave='ESTUDIOS_SOCIALES'), 2, 'SP_GEOGRAFIA',  'Lectura e interpretación de mapas', 'Uso de mapas, globos y herramientas geográficas', 2),
((SELECT id FROM materias WHERE clave='ESTUDIOS_SOCIALES'), 2, 'SP_IDENTIDAD',  'Expresión de la identidad cultural','Participación en tradiciones, celebraciones y expresiones culturales', 3),
((SELECT id FROM materias WHERE clave='ESTUDIOS_SOCIALES'), 2, 'SP_POLITICA',   'Participación cívica activa',       'Ejercicio de derechos y deberes en contextos escolares y comunitarios', 4),
((SELECT id FROM materias WHERE clave='ESTUDIOS_SOCIALES'), 2, 'SP_ECONOMIA',   'Toma de decisiones económicas',     'Aplicación de conceptos económicos en situaciones cotidianas', 5),
((SELECT id FROM materias WHERE clave='ESTUDIOS_SOCIALES'), 2, 'SP_DERECHOS',   'Defensa y promoción de derechos',   'Reconocimiento y práctica de derechos en la vida escolar y comunitaria', 6),
((SELECT id FROM materias WHERE clave='ESTUDIOS_SOCIALES'), 2, 'SP_SOCIEDAD',   'Resolución de conflictos sociales', 'Aplicación de estrategias de diálogo y resolución pacífica de conflictos', 7);

-- Actitudinal (tipo_saber_id = 3)
INSERT INTO ejes_tematicos (materia_id, tipo_saber_id, clave, nombre, descripcion, orden) VALUES
((SELECT id FROM materias WHERE clave='ESTUDIOS_SOCIALES'), 3, 'SA_HISTORIA',   'Valoración del patrimonio histórico','Respeto y aprecio por la historia y el legado cultural', 1),
((SELECT id FROM materias WHERE clave='ESTUDIOS_SOCIALES'), 3, 'SA_GEOGRAFIA',  'Sentido de pertenencia territorial', 'Apego y compromiso con el territorio y el patrimonio natural', 2),
((SELECT id FROM materias WHERE clave='ESTUDIOS_SOCIALES'), 3, 'SA_IDENTIDAD',  'Orgullo y respeto por la identidad', 'Actitud positiva hacia la identidad nacional y la diversidad cultural', 3),
((SELECT id FROM materias WHERE clave='ESTUDIOS_SOCIALES'), 3, 'SA_POLITICA',   'Compromiso democrático',             'Valoración de la democracia, la justicia y la participación ciudadana', 4),
((SELECT id FROM materias WHERE clave='ESTUDIOS_SOCIALES'), 3, 'SA_ECONOMIA',   'Responsabilidad económica',          'Actitud de consumo responsable y valoración del trabajo productivo', 5),
((SELECT id FROM materias WHERE clave='ESTUDIOS_SOCIALES'), 3, 'SA_DERECHOS',   'Compromiso con los derechos humanos','Disposición activa a defender los derechos propios y ajenos', 6),
((SELECT id FROM materias WHERE clave='ESTUDIOS_SOCIALES'), 3, 'SA_SOCIEDAD',   'Respeto a la diversidad',            'Tolerancia, empatía y valoración de la diversidad sociocultural', 7);

-- ============================================================
-- 6. COLUMNA materia_id EN evaluaciones_saber
-- ============================================================
ALTER TABLE evaluaciones_saber
    ADD COLUMN materia_id INTEGER REFERENCES materias(id) ON DELETE RESTRICT;

-- Evaluaciones existentes → Español
UPDATE evaluaciones_saber
    SET materia_id = (SELECT id FROM materias WHERE clave = 'ESPANOL');

ALTER TABLE evaluaciones_saber ALTER COLUMN materia_id SET NOT NULL;

CREATE INDEX idx_eval_saber_materia ON evaluaciones_saber(materia_id);

-- ============================================================
-- 7. COLUMNA materia_id EN alertas_tematicas (desnormalizada)
-- ============================================================
ALTER TABLE alertas_tematicas
    ADD COLUMN materia_id INTEGER REFERENCES materias(id) ON DELETE RESTRICT;

UPDATE alertas_tematicas
    SET materia_id = (SELECT id FROM materias WHERE clave = 'ESPANOL');

ALTER TABLE alertas_tematicas ALTER COLUMN materia_id SET NOT NULL;

CREATE INDEX idx_alerta_tem_materia ON alertas_tematicas(materia_id);

-- ============================================================
-- 8. ACTUALIZAR VISTAS
-- ============================================================
-- Hay que eliminar las vistas dependientes antes de recrearlas con nuevas columnas
DROP VIEW IF EXISTS vw_promedios_tipo_saber_periodo CASCADE;
DROP VIEW IF EXISTS vw_promedios_ejes_periodo CASCADE;

CREATE VIEW vw_promedios_ejes_periodo AS
SELECT
    es.estudiante_id,
    es.periodo_id,
    es.materia_id,
    m.nombre        AS materia_nombre,
    det.eje_tematico_id,
    ej.nombre       AS eje_nombre,
    ej.tipo_saber_id,
    ts.nombre       AS tipo_saber_nombre,
    COUNT(det.id)   AS total_evaluaciones,
    ROUND(AVG(det.valor), 2) AS promedio,
    MIN(det.valor)  AS valor_minimo,
    MAX(det.valor)  AS valor_maximo
FROM detalle_evaluacion_saber det
JOIN evaluaciones_saber es ON es.id = det.evaluacion_saber_id
JOIN ejes_tematicos     ej ON ej.id = det.eje_tematico_id
JOIN tipos_saber        ts ON ts.id = ej.tipo_saber_id
JOIN materias            m ON m.id  = es.materia_id
GROUP BY es.estudiante_id, es.periodo_id, es.materia_id, m.nombre,
         det.eje_tematico_id, ej.nombre, ej.tipo_saber_id, ts.nombre;

CREATE VIEW vw_promedios_tipo_saber_periodo AS
SELECT
    v.estudiante_id,
    v.periodo_id,
    v.materia_id,
    v.materia_nombre,
    v.tipo_saber_id,
    v.tipo_saber_nombre,
    COUNT(DISTINCT v.eje_tematico_id) AS ejes_evaluados,
    ROUND(AVG(v.promedio), 2)         AS promedio_tipo_saber
FROM vw_promedios_ejes_periodo v
GROUP BY v.estudiante_id, v.periodo_id, v.materia_id, v.materia_nombre,
         v.tipo_saber_id, v.tipo_saber_nombre;
