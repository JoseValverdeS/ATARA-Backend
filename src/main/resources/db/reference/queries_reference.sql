-- ============================================================
-- ATARA — Consultas de referencia
--
-- IMPORTANTE: Este archivo es de SOLO LECTURA.
-- NO es una migración Flyway ni debe ejecutarse como tal.
-- Flyway ignora archivos sin prefijo V/U/R en su nombre.
--
-- Usar como base para:
--   - @Query en repositorios Spring Data JPA
--   - Vistas SQL (ver V4__corrections.sql para ejemplos)
--   - Reportes del backend / dashboard frontend
--
-- NOTA: :param indica bind parameters (JPQL / PreparedStatement).
--       Reemplazar con ?1, ?2 (posicional) o :nombre (Spring @Query).
--
-- CORRECCIONES respecto a la versión anterior:
--   Q1  usa ev2.seccion_id en lugar de e.seccion_id
--       (sección al momento de la evaluación, no la actual)
--   Q2  LEFT JOIN en dimension (criterios sin dimensión = omitidos antes)
--   Q5  filtra estudiantes inactivos
--   Q7  filtra por rol DOCENTE
--   Q8  LEFT JOIN en dimension (mismo ajuste que Q2)
-- ============================================================


-- ============================================================
-- Q1. Promedio Likert por estudiante en un período dado
--     Menor promedio = mayor riesgo
--     NOTA: usa ev2.seccion_id para reflejar la sección al
--     momento de la evaluación, no la sección actual.
-- ============================================================
SELECT
    e.id                                            AS estudiante_id,
    e.nombre || ' ' || e.apellido1                  AS nombre_completo,
    n.nombre                                        AS nivel,
    sec.nombre                                      AS seccion,
    ce.nombre                                       AS centro,
    p.nombre                                        AS periodo,
    ROUND(AVG(esc.valor_numerico), 2)               AS promedio_general,
    COUNT(de.id)                                    AS criterios_evaluados
FROM estudiantes        e
JOIN evaluaciones       ev  ON ev.estudiante_id     = e.id
JOIN secciones          sec ON sec.id               = ev.seccion_id   -- sección en el momento de la evaluación
JOIN niveles            n   ON n.id                 = sec.nivel_id
JOIN centros_educativos ce  ON ce.id                = sec.centro_id
JOIN periodos           p   ON p.id                 = ev.periodo_id
JOIN detalle_evaluacion de  ON de.evaluacion_id     = ev.id
JOIN escalas_valoracion esc ON esc.id               = de.escala_id
WHERE p.id = :periodoId
  AND e.estado = 'ACTIVO'
GROUP BY e.id, e.nombre, e.apellido1,
         n.nombre, sec.nombre, ce.nombre, p.nombre
ORDER BY promedio_general ASC;


-- ============================================================
-- Q2. Desglose por dimensión para un estudiante
--     (datos para gráfico de radar en el dashboard)
--     LEFT JOIN en dimensiones para no perder criterios
--     transversales (dimension_id nullable).
-- ============================================================
SELECT
    e.nombre || ' ' || e.apellido1                  AS nombre_completo,
    COALESCE(d.nombre, 'Sin dimensión')             AS dimension,
    ROUND(AVG(esc.valor_numerico), 2)               AS promedio_dimension,
    d.peso                                          AS peso_dimension
FROM estudiantes            e
JOIN evaluaciones           ev  ON ev.estudiante_id  = e.id
JOIN detalle_evaluacion     de  ON de.evaluacion_id  = ev.id
JOIN criterios_indicadores  ci  ON ci.id             = de.criterio_id
LEFT JOIN dimensiones_evaluacion d ON d.id           = ci.dimension_id   -- LEFT: dimension_id es nullable
JOIN escalas_valoracion     esc ON esc.id            = de.escala_id
WHERE e.id         = :estudianteId
  AND ev.periodo_id = :periodoId
GROUP BY e.nombre, e.apellido1, d.id, d.nombre, d.peso
ORDER BY d.nombre NULLS LAST;


-- ============================================================
-- Q3. Estudiantes en riesgo en el período activo
--     Umbrales: < 1.5 = CRITICA | < 2.0 = MODERADA | < 2.5 = PREVENTIVA
--     (ajustar según configuracion_alertas del sistema)
-- ============================================================
SELECT
    e.id                                            AS estudiante_id,
    e.nombre || ' ' || e.apellido1                  AS nombre_completo,
    ce.nombre                                       AS centro,
    n.nombre                                        AS nivel,
    sec.nombre                                      AS seccion,
    ROUND(AVG(esc.valor_numerico), 2)               AS promedio_general,
    CASE
        WHEN AVG(esc.valor_numerico) < 1.5 THEN 'CRITICA'
        WHEN AVG(esc.valor_numerico) < 2.0 THEN 'MODERADA'
        ELSE                                    'PREVENTIVA'
    END                                             AS tipo_alerta_sugerida,
    CASE
        WHEN AVG(esc.valor_numerico) < 1.5 THEN 'ALTO'
        WHEN AVG(esc.valor_numerico) < 2.0 THEN 'MEDIO'
        ELSE                                    'BAJO'
    END                                             AS nivel_riesgo_sugerido
FROM estudiantes        e
JOIN evaluaciones       ev  ON ev.estudiante_id = e.id
JOIN secciones          sec ON sec.id           = ev.seccion_id
JOIN niveles            n   ON n.id             = sec.nivel_id
JOIN centros_educativos ce  ON ce.id            = sec.centro_id
JOIN periodos           p   ON p.id             = ev.periodo_id
JOIN detalle_evaluacion de  ON de.evaluacion_id = ev.id
JOIN escalas_valoracion esc ON esc.id           = de.escala_id
WHERE p.activo    = TRUE
  AND e.estado    = 'ACTIVO'
GROUP BY e.id, e.nombre, e.apellido1, ce.nombre, n.nombre, sec.nombre
HAVING AVG(esc.valor_numerico) < 2.5
ORDER BY promedio_general ASC;


-- ============================================================
-- Q4. Reporte por nivel — todos los estudiantes de un grado
--     Agrupado por dimensión — vista de coordinador
-- ============================================================
SELECT
    n.nombre                                        AS nivel,
    ce.nombre                                       AS centro,
    sec.nombre                                      AS seccion,
    e.nombre || ' ' || e.apellido1                  AS nombre_completo,
    COALESCE(d.nombre, 'Sin dimensión')             AS dimension,
    p.nombre                                        AS periodo,
    ROUND(AVG(esc.valor_numerico), 2)               AS promedio_dimension
FROM niveles            n
JOIN secciones          sec ON sec.nivel_id        = n.id
JOIN centros_educativos ce  ON ce.id               = sec.centro_id
JOIN estudiantes        e   ON e.seccion_id        = sec.id
JOIN evaluaciones       ev  ON ev.estudiante_id    = e.id
JOIN periodos           p   ON p.id                = ev.periodo_id
JOIN detalle_evaluacion de  ON de.evaluacion_id    = ev.id
JOIN criterios_indicadores  ci  ON ci.id           = de.criterio_id
LEFT JOIN dimensiones_evaluacion d ON d.id         = ci.dimension_id
JOIN escalas_valoracion esc ON esc.id              = de.escala_id
WHERE n.numero_grado     = :numeroGrado
  AND p.anio_lectivo_id  = :anioLectivoId
  AND e.estado           = 'ACTIVO'
GROUP BY n.nombre, ce.nombre, sec.nombre,
         e.nombre, e.apellido1, d.nombre, p.nombre
ORDER BY ce.nombre, sec.nombre, nombre_completo, d.nombre NULLS LAST;


-- ============================================================
-- Q5. Evolución histórica de un estudiante por dimensión
--     (datos para gráficos de tendencia temporal)
-- ============================================================
SELECT
    p.numero_periodo                                AS numero_periodo,
    p.nombre                                        AS periodo,
    p.fecha_inicio,
    al.anio                                         AS anio_lectivo,
    COALESCE(d.nombre, 'Sin dimensión')             AS dimension,
    ROUND(AVG(esc.valor_numerico), 2)               AS promedio_dimension
FROM evaluaciones           ev
JOIN periodos               p   ON p.id            = ev.periodo_id
JOIN anios_lectivos         al  ON al.id            = p.anio_lectivo_id
JOIN detalle_evaluacion     de  ON de.evaluacion_id = ev.id
JOIN criterios_indicadores  ci  ON ci.id            = de.criterio_id
LEFT JOIN dimensiones_evaluacion d ON d.id          = ci.dimension_id
JOIN escalas_valoracion     esc ON esc.id           = de.escala_id
JOIN estudiantes            e   ON e.id             = ev.estudiante_id
WHERE ev.estudiante_id = :estudianteId
  AND e.estado         = 'ACTIVO'
GROUP BY p.numero_periodo, p.nombre, p.fecha_inicio, al.anio, d.nombre
ORDER BY al.anio, p.numero_periodo, d.nombre NULLS LAST;


-- ============================================================
-- Q6. Panel de alertas activas (dashboard de coordinador)
-- ============================================================
SELECT
    a.id                                            AS alerta_id,
    a.tipo_alerta,
    a.nivel_alerta,
    a.fecha_generacion,
    a.motivo,
    e.id                                            AS estudiante_id,
    e.nombre || ' ' || e.apellido1                  AS nombre_estudiante,
    ce.nombre                                       AS centro,
    n.nombre                                        AS nivel,
    sec.nombre                                      AS seccion,
    m.nombre                                        AS materia,
    c.nombre                                        AS contenido_afectado,
    u.nombre || ' ' || u.apellidos                  AS generada_por,
    p.nombre                                        AS periodo
FROM alertas            a
JOIN estudiantes        e   ON e.id   = a.estudiante_id
JOIN secciones          sec ON sec.id = e.seccion_id
JOIN niveles            n   ON n.id   = sec.nivel_id
JOIN centros_educativos ce  ON ce.id  = sec.centro_id
JOIN contenidos         c   ON c.id   = a.contenido_id
JOIN materias           m   ON m.id   = c.materia_id
JOIN periodos           p   ON p.id   = a.periodo_id
LEFT JOIN usuarios      u   ON u.id   = a.generada_por
WHERE a.estado = 'ACTIVA'
ORDER BY
    CASE a.nivel_alerta
        WHEN 'ALTO'  THEN 1
        WHEN 'MEDIO' THEN 2
        WHEN 'BAJO'  THEN 3
    END,
    a.fecha_generacion DESC;


-- ============================================================
-- Q7. Carga de trabajo docente por período
--     Filtra por rol DOCENTE — excluye admins y coordinadores
-- ============================================================
SELECT
    u.id                                            AS usuario_id,
    u.nombre || ' ' || u.apellidos                  AS docente,
    ce.nombre                                       AS centro,
    n.nombre                                        AS nivel,
    sec.nombre                                      AS seccion,
    COUNT(DISTINCT est.id)                          AS total_estudiantes,
    COUNT(DISTINCT ev.id)                           AS evaluaciones_registradas,
    ROUND(
        COUNT(DISTINCT ev.id) * 100.0
        / NULLIF(COUNT(DISTINCT est.id), 0)
    , 1)                                            AS porcentaje_completado
FROM usuarios               u
JOIN roles                  r   ON r.id            = u.rol_id
JOIN usuarios_secciones     us  ON us.usuario_id   = u.id
JOIN secciones              sec ON sec.id          = us.seccion_id
JOIN centros_educativos     ce  ON ce.id           = sec.centro_id
JOIN niveles                n   ON n.id            = sec.nivel_id
LEFT JOIN estudiantes       est ON est.seccion_id  = sec.id  AND est.estado = 'ACTIVO'
LEFT JOIN evaluaciones      ev  ON ev.usuario_id   = u.id
                                AND ev.seccion_id  = sec.id
                                AND ev.periodo_id  = :periodoId
WHERE u.estado  = 'ACTIVO'
  AND r.nombre  = 'DOCENTE'                   -- excluye ADMIN y COORDINADOR
GROUP BY u.id, u.nombre, u.apellidos, ce.nombre, n.nombre, sec.nombre
ORDER BY docente, ce.nombre;


-- ============================================================
-- Q8. Distribución de escala por contenido en un período
--     (¿cuántos estudiantes en cada nivel Likert por materia?)
--     LEFT JOIN en dimensiones para no perder criterios sin dimensión
-- ============================================================
SELECT
    m.nombre                                        AS materia,
    c.nombre                                        AS contenido,
    COALESCE(d.nombre, 'Sin dimensión')             AS dimension,
    SUM(CASE WHEN esc.valor_numerico = 1 THEN 1 ELSE 0 END) AS insuficiente,
    SUM(CASE WHEN esc.valor_numerico = 2 THEN 1 ELSE 0 END) AS basico,
    SUM(CASE WHEN esc.valor_numerico = 3 THEN 1 ELSE 0 END) AS satisfactorio,
    SUM(CASE WHEN esc.valor_numerico = 4 THEN 1 ELSE 0 END) AS destacado,
    COUNT(de.id)                                    AS total_respuestas,
    ROUND(AVG(esc.valor_numerico), 2)               AS promedio
FROM detalle_evaluacion     de
JOIN evaluaciones           ev  ON ev.id            = de.evaluacion_id
JOIN criterios_indicadores  ci  ON ci.id            = de.criterio_id
JOIN contenidos             c   ON c.id             = ci.contenido_id
JOIN materias               m   ON m.id             = c.materia_id
LEFT JOIN dimensiones_evaluacion d ON d.id          = ci.dimension_id
JOIN escalas_valoracion     esc ON esc.id           = de.escala_id
WHERE ev.periodo_id = :periodoId
GROUP BY m.nombre, c.nombre, d.nombre
ORDER BY m.nombre, c.nombre, promedio ASC;
