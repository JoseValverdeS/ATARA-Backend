-- =============================================================================
-- V11: Corregir titularidad de la sección "Primero D" (Escuela Simón Bolívar 2025)
--
-- Problema: la sección fue creada con docente_id = Andrea Vargas, cuando
--           la docente titular correcta es Keylor Cortés Cascante.
--           La query UNION incluía la sección en las secciones de Andrea.
-- =============================================================================

-- 1. Reasignar docente titular a Keylor en las secciones de 1° en Bolívar 2025
--    que no corresponden a Andrea (su sección legítima es la 'B').
UPDATE secciones
SET docente_id = (SELECT id FROM usuarios WHERE correo = 'kcortes@atara.mep.go.cr')
WHERE centro_id      = (SELECT id FROM centros_educativos WHERE nombre = 'Escuela Simón Bolívar')
  AND nivel_id       = (SELECT id FROM niveles WHERE numero_grado = 1)
  AND anio_lectivo_id = (SELECT id FROM anios_lectivos WHERE anio = 2025)
  AND nombre         <> 'B'
  AND docente_id     = (SELECT id FROM usuarios WHERE correo = 'avargas@atara.mep.go.cr');

-- 2. Quitar a Andrea de usuarios_secciones para las secciones que no le corresponden
--    (conserva únicamente las secciones donde sigue siendo docente_id).
DELETE FROM usuarios_secciones
WHERE usuario_id = (SELECT id FROM usuarios WHERE correo = 'avargas@atara.mep.go.cr')
  AND seccion_id NOT IN (
      SELECT id FROM secciones
      WHERE docente_id = (SELECT id FROM usuarios WHERE correo = 'avargas@atara.mep.go.cr')
  );

-- 3. Asegurar que Keylor esté en usuarios_secciones para todas sus secciones titulares
--    (idempotente, no falla si ya existe).
INSERT INTO usuarios_secciones (usuario_id, seccion_id)
SELECT (SELECT id FROM usuarios WHERE correo = 'kcortes@atara.mep.go.cr'), s.id
FROM secciones s
WHERE s.docente_id = (SELECT id FROM usuarios WHERE correo = 'kcortes@atara.mep.go.cr')
ON CONFLICT DO NOTHING;
