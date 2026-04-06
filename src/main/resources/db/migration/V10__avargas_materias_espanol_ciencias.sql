-- =============================================================================
-- V10: Restringir materias de Andrea Vargas (avargas@atara.mep.go.cr)
--      Solo debe tener acceso a Español y Ciencias.
-- =============================================================================

DELETE FROM usuario_materias
WHERE usuario_id = (SELECT id FROM usuarios WHERE correo = 'avargas@atara.mep.go.cr')
  AND materia_id NOT IN (
      SELECT id FROM materias WHERE nombre IN ('Español', 'Ciencias')
  );

-- Insertar en caso de que no existan (idempotente)
INSERT INTO usuario_materias (usuario_id, materia_id)
SELECT u.id, m.id
FROM usuarios u
CROSS JOIN materias m
WHERE u.correo  = 'avargas@atara.mep.go.cr'
  AND m.nombre IN ('Español', 'Ciencias')
ON CONFLICT DO NOTHING;
