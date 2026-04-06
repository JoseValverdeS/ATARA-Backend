-- =============================================================================
-- V8: Tabla de asignación de materias por usuario (docente)
-- Permite que un docente tenga acceso solo a las materias que le corresponden.
-- =============================================================================

CREATE TABLE usuario_materias (
    id          SERIAL PRIMARY KEY,
    usuario_id  INT NOT NULL REFERENCES usuarios(id) ON DELETE CASCADE,
    materia_id  INT NOT NULL REFERENCES materias(id) ON DELETE CASCADE,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT uq_usuario_materia UNIQUE (usuario_id, materia_id)
);

CREATE INDEX idx_um_usuario ON usuario_materias(usuario_id);
CREATE INDEX idx_um_materia ON usuario_materias(materia_id);

-- Seed seguro: asignar todas las materias a los docentes existentes.
-- Garantiza que los docentes ya creados no queden sin acceso tras la migración.
INSERT INTO usuario_materias (usuario_id, materia_id)
SELECT u.id, m.id
FROM usuarios u
CROSS JOIN materias m
WHERE u.rol_id = (SELECT id FROM roles WHERE nombre = 'DOCENTE')
ON CONFLICT DO NOTHING;
