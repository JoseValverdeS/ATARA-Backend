-- ============================================================
-- ATARA - Unicidad de alertas tematicas por materia
--
-- Version : V7
--
-- Ajusta la unicidad de alertas_tematicas para que alertas del
-- mismo estudiante/periodo/eje/nivel puedan coexistir cuando
-- pertenecen a materias distintas.
-- ============================================================

ALTER TABLE alertas_tematicas
    DROP CONSTRAINT IF EXISTS uq_alerta_tematica;

ALTER TABLE alertas_tematicas
    ADD CONSTRAINT uq_alerta_tematica
        UNIQUE (estudiante_id, periodo_id, eje_tematico_id, materia_id, nivel_alerta);
