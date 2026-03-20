-- ============================================================
-- ATARA — V3: Vistas SQL (capa de lectura para el backend)
--
-- Las vistas proporcionan una API de solo lectura para:
--   - Repositorios Spring Data JPA con @Query nativo
--   - Endpoints de reportes y dashboard
--   - Consultas del coordinador y del docente
--
-- NOTAS TÉCNICAS:
--   - Todas las vistas que filtran por período usan p.activo = TRUE
--     para reflejar automáticamente el cambio de período.
--   - JOIN (inner) en dimensiones_evaluacion porque dimension_id
--     es NOT NULL en criterios_indicadores.
--   - ev.seccion_id (no matriculas) para capturar la sección
--     en el momento de la evaluación, no la matrícula actual.
--   - vw_alertas_activas obtiene la sección del estudiante
--     uniéndose a matriculas por (estudiante_id, anio_lectivo_id)
--     derivado del periodo de la alerta.
--   - e.estado = 'ACTIVO' excluye estudiantes inactivos/trasladados.
-- ============================================================


-- ============================================================
-- V1. Jerarquía completa de criterios
--     criterio → contenido → materia → dimensión
-- ============================================================
CREATE OR REPLACE VIEW vw_criterios_completos AS
SELECT
    ci.id                                   AS criterio_id,
    ci.nombre                               AS criterio,
    ci.descripcion                          AS criterio_descripcion,
    ci.peso                                 AS criterio_peso,
    c.id                                    AS contenido_id,
    c.nombre                                AS contenido,
    m.id                                    AS materia_id,
    m.nombre                                AS materia,
    d.id                                    AS dimension_id,
    d.nombre                                AS dimension,
    d.peso                                  AS dimension_peso
FROM criterios_indicadores  ci
JOIN contenidos             c   ON c.id  = ci.contenido_id
JOIN materias               m   ON m.id  = c.materia_id
JOIN dimensiones_evaluacion d   ON d.id  = ci.dimension_id;

COMMENT ON VIEW vw_criterios_completos IS 'Jerarquía completa: criterio → contenido → materia → dimensión.';


-- ============================================================
-- V2. Secciones activas del año lectivo en curso
-- ============================================================
CREATE OR REPLACE VIEW vw_secciones_activas AS
SELECT
    s.id                                    AS seccion_id,
    s.nombre                                AS seccion,
    n.numero_grado,
    n.nombre                                AS nivel,
    ce.id                                   AS centro_id,
    ce.nombre                               AS centro,
    ce.circuito,
    al.anio                                 AS anio_lectivo,
    s.capacidad,
    s.docente_id
FROM secciones              s
JOIN niveles                n   ON n.id  = s.nivel_id
JOIN centros_educativos     ce  ON ce.id = s.centro_id
JOIN anios_lectivos         al  ON al.id = s.anio_lectivo_id
WHERE al.activo = TRUE;

COMMENT ON VIEW vw_secciones_activas IS 'Secciones del año lectivo activo con contexto de nivel y centro.';


-- ============================================================
-- V3. Docentes asignados por sección (año activo)
-- ============================================================
CREATE OR REPLACE VIEW vw_docentes_por_seccion AS
SELECT
    u.id                                    AS usuario_id,
    u.nombre || ' ' || u.apellidos         AS docente,
    u.correo,
    s.id                                    AS seccion_id,
    s.nombre                                AS seccion,
    n.nombre                                AS nivel,
    ce.nombre                               AS centro,
    al.anio                                 AS anio_lectivo
FROM usuarios_secciones     us
JOIN usuarios               u   ON u.id  = us.usuario_id
JOIN secciones              s   ON s.id  = us.seccion_id
JOIN niveles                n   ON n.id  = s.nivel_id
JOIN centros_educativos     ce  ON ce.id = s.centro_id
JOIN anios_lectivos         al  ON al.id = s.anio_lectivo_id
WHERE u.estado  = 'ACTIVO'
  AND al.activo = TRUE;

COMMENT ON VIEW vw_docentes_por_seccion IS 'Asignaciones docente-sección del año lectivo activo.';


-- ============================================================
-- V4. Evaluaciones del período activo
-- ============================================================
CREATE OR REPLACE VIEW vw_evaluaciones_periodo_activo AS
SELECT
    ev.id                                   AS evaluacion_id,
    ev.estudiante_id,
    e.nombre || ' ' || e.apellido1         AS nombre_estudiante,
    ev.usuario_id,
    u.nombre || ' ' || u.apellidos        AS docente,
    ev.seccion_id,
    sec.nombre                              AS seccion,
    n.nombre                                AS nivel,
    ce.nombre                               AS centro,
    p.id                                    AS periodo_id,
    p.nombre                                AS periodo,
    ev.observacion_general,
    ev.origen_registro,
    ev.created_at
FROM evaluaciones           ev
JOIN estudiantes            e   ON e.id   = ev.estudiante_id
JOIN usuarios               u   ON u.id   = ev.usuario_id
JOIN secciones              sec ON sec.id = ev.seccion_id
JOIN niveles                n   ON n.id   = sec.nivel_id
JOIN centros_educativos     ce  ON ce.id  = sec.centro_id
JOIN periodos               p   ON p.id   = ev.periodo_id
WHERE p.activo    = TRUE
  AND e.estado    = 'ACTIVO';

COMMENT ON VIEW vw_evaluaciones_periodo_activo IS 'Cabeceras de evaluación del período activo con contexto completo.';


-- ============================================================
-- V5. Rendimiento (promedio Likert) por estudiante
--     en el período activo — menor promedio = mayor riesgo
-- ============================================================
CREATE OR REPLACE VIEW vw_rendimiento_periodo_activo AS
SELECT
    e.id                                    AS estudiante_id,
    e.nombre || ' ' || e.apellido1         AS nombre_completo,
    n.nombre                                AS nivel,
    sec.nombre                              AS seccion,
    ce.nombre                               AS centro,
    p.nombre                                AS periodo,
    ROUND(AVG(esc.valor_numerico), 2)       AS promedio_general,
    COUNT(de.id)                            AS criterios_evaluados
FROM estudiantes            e
JOIN evaluaciones           ev  ON ev.estudiante_id = e.id
JOIN secciones              sec ON sec.id            = ev.seccion_id
JOIN niveles                n   ON n.id              = sec.nivel_id
JOIN centros_educativos     ce  ON ce.id             = sec.centro_id
JOIN periodos               p   ON p.id              = ev.periodo_id
JOIN detalle_evaluacion     de  ON de.evaluacion_id  = ev.id
JOIN escalas_valoracion     esc ON esc.id            = de.escala_id
WHERE p.activo    = TRUE
  AND e.estado    = 'ACTIVO'
GROUP BY e.id, e.nombre, e.apellido1,
         n.nombre, sec.nombre, ce.nombre, p.nombre;

COMMENT ON VIEW vw_rendimiento_periodo_activo IS 'Promedio Likert por estudiante en el período activo. Ordenar ASC para ver mayor riesgo primero.';


-- ============================================================
-- V6. Estudiantes en riesgo (promedio < 2.5 en período activo)
--     Umbrales: < 1.5 CRITICA | < 2.0 MODERADA | < 2.5 PREVENTIVA
-- ============================================================
CREATE OR REPLACE VIEW vw_estudiantes_en_riesgo AS
SELECT
    estudiante_id,
    nombre_completo,
    nivel,
    seccion,
    centro,
    periodo,
    promedio_general,
    criterios_evaluados,
    CASE
        WHEN promedio_general < 1.5 THEN 'CRITICA'
        WHEN promedio_general < 2.0 THEN 'MODERADA'
        ELSE                             'PREVENTIVA'
    END AS tipo_alerta_sugerida,
    CASE
        WHEN promedio_general < 1.5 THEN 'ALTO'
        WHEN promedio_general < 2.0 THEN 'MEDIO'
        ELSE                             'BAJO'
    END AS nivel_riesgo_sugerido
FROM vw_rendimiento_periodo_activo
WHERE promedio_general < 2.5
ORDER BY promedio_general ASC;

COMMENT ON VIEW vw_estudiantes_en_riesgo IS 'Estudiantes con promedio < 2.5 en el período activo. Ordenados por promedio ASC (el más bajo = mayor riesgo primero).';


-- ============================================================
-- V7. Panel de alertas activas (dashboard de coordinador)
-- ============================================================
CREATE OR REPLACE VIEW vw_alertas_activas AS
SELECT
    a.id                                    AS alerta_id,
    a.tipo_alerta,
    a.nivel_alerta,
    a.fecha_generacion,
    a.motivo,
    e.id                                    AS estudiante_id,
    e.nombre || ' ' || e.apellido1         AS nombre_estudiante,
    ce.nombre                               AS centro,
    n.nombre                                AS nivel,
    sec.nombre                              AS seccion,
    m.nombre                                AS materia,
    c.nombre                                AS contenido_afectado,
    u.nombre || ' ' || u.apellidos        AS generada_por,
    p.nombre                                AS periodo
FROM alertas                a
JOIN estudiantes            e   ON e.id              = a.estudiante_id
JOIN periodos               p   ON p.id              = a.periodo_id
JOIN matriculas             mat ON mat.estudiante_id  = e.id
                                AND mat.anio_lectivo_id = p.anio_lectivo_id
JOIN secciones              sec ON sec.id             = mat.seccion_id
JOIN niveles                n   ON n.id               = sec.nivel_id
JOIN centros_educativos     ce  ON ce.id              = sec.centro_id
JOIN contenidos             c   ON c.id               = a.contenido_id
JOIN materias               m   ON m.id               = c.materia_id
LEFT JOIN usuarios          u   ON u.id               = a.generada_por
WHERE a.estado = 'ACTIVA'
ORDER BY
    CASE a.nivel_alerta
        WHEN 'ALTO'  THEN 1
        WHEN 'MEDIO' THEN 2
        WHEN 'BAJO'  THEN 3
    END,
    a.fecha_generacion DESC;

COMMENT ON VIEW vw_alertas_activas IS
    'Alertas en estado ACTIVA ordenadas por severidad y fecha. '
    'Usa INNER JOIN con matriculas: alertas sin matrícula registrada para ese año lectivo '
    'no aparecen en esta vista. Usar vw_historial_por_dimension para consultas históricas.';


-- ============================================================
-- V8. Historial de rendimiento por dimensión
--     (datos para gráficos de tendencia — todos los períodos)
--     El backend filtra por estudiante_id y/o anio_lectivo_id.
-- ============================================================
CREATE OR REPLACE VIEW vw_historial_por_dimension AS
SELECT
    ev.estudiante_id,
    e.nombre || ' ' || e.apellido1         AS nombre_completo,
    p.numero_periodo,
    p.nombre                                AS periodo,
    p.fecha_inicio,
    al.anio                                 AS anio_lectivo,
    d.nombre                                AS dimension,
    d.peso                                  AS dimension_peso,
    ROUND(AVG(esc.valor_numerico), 2)       AS promedio_dimension
FROM evaluaciones           ev
JOIN periodos               p   ON p.id             = ev.periodo_id
JOIN anios_lectivos         al  ON al.id             = p.anio_lectivo_id
JOIN detalle_evaluacion     de  ON de.evaluacion_id  = ev.id
JOIN criterios_indicadores  ci  ON ci.id             = de.criterio_id
JOIN dimensiones_evaluacion d   ON d.id              = ci.dimension_id
JOIN escalas_valoracion     esc ON esc.id            = de.escala_id
JOIN estudiantes            e   ON e.id              = ev.estudiante_id
WHERE e.estado = 'ACTIVO'
GROUP BY ev.estudiante_id, e.nombre, e.apellido1,
         p.numero_periodo, p.nombre, p.fecha_inicio,
         al.anio, d.id, d.nombre, d.peso
ORDER BY ev.estudiante_id, al.anio, p.numero_periodo, d.nombre;

COMMENT ON VIEW vw_historial_por_dimension IS 'Promedio por dimensión en cada período histórico. Filtrar por estudiante_id en el backend.';


-- ============================================================
-- V9. Estudiantes matriculados en el año lectivo activo
--     Fuente canónica para saber en qué sección está cada
--     estudiante durante el año en curso.
-- ============================================================
CREATE OR REPLACE VIEW vw_matriculas_anio_activo AS
SELECT
    e.id                                    AS estudiante_id,
    e.identificacion,
    e.nombre || ' ' || e.apellido1         AS nombre_completo,
    e.apellido2,
    e.genero,
    e.estado                                AS estado_estudiante,
    mat.id                                  AS matricula_id,
    mat.estado                              AS estado_matricula,
    mat.fecha_matricula,
    s.id                                    AS seccion_id,
    s.nombre                                AS seccion,
    n.numero_grado,
    n.nombre                                AS nivel,
    ce.id                                   AS centro_id,
    ce.nombre                               AS centro,
    al.anio                                 AS anio_lectivo
FROM matriculas             mat
JOIN estudiantes            e   ON e.id   = mat.estudiante_id
JOIN secciones              s   ON s.id   = mat.seccion_id
JOIN niveles                n   ON n.id   = s.nivel_id
JOIN centros_educativos     ce  ON ce.id  = s.centro_id
JOIN anios_lectivos         al  ON al.id  = mat.anio_lectivo_id
WHERE al.activo          = TRUE
  AND mat.estado         = 'ACTIVO'
  AND e.estado           = 'ACTIVO';

COMMENT ON VIEW vw_matriculas_anio_activo IS 'Estudiantes activos con su sección asignada en el año lectivo activo. Fuente canónica para consultas de sección actual.';
