# Cambios pendientes — Base de datos

> Estado: **Pendiente de implementar**
> Prioridad: Media — no bloquea el desarrollo actual

---

## Contexto

Al analizar los datos que extrae el **Extractor PIAD** (`scripts/extractor-piad/`) y compararlos contra el esquema actual de la DB, se identificaron campos faltantes y restricciones que no cubren todos los casos del MEP.

---

## Regla de Flyway (antes de tocar cualquier cosa)

- **Nunca modificar** `V1__init_schema.sql`, `V2__sample_data.sql`, ni `V3__queries_reference.sql`.
- Todos los cambios van en un archivo nuevo: `src/main/resources/db/migration/V4__descripcion.sql`.
- El nombre del archivo debe seguir el patrón `V{numero}__{descripcion_con_guiones_bajos}.sql`.
- Flyway ejecuta los archivos pendientes automáticamente al iniciar el backend.

---

## Cambio 1 — Agregar `tipo_adecuacion` a `estudiantes` ⭐ Prioritario

**Problema:** El campo `tipoAdecuacion` viene del PIAD y no tiene columna en la DB. Es crítico para ATARA porque los estudiantes con adecuación significativa tienen criterios de evaluación distintos y umbrales de alerta diferentes. Sin este dato el sistema puede generar falsos positivos.

**Valores posibles en el MEP:**
| Valor | Descripción |
|---|---|
| `Sin adecuación` | Estudiante regular |
| `Adecuación de acceso` | Ajuste físico o técnico, no cambia contenido |
| `Adecuación no significativa` | Ajuste de método o tiempo, mismos objetivos |
| `Adecuación significativa` | Currículo modificado, objetivos diferentes |

**Qué tocar (3 capas):**

1. **SQL** — nuevo archivo `V4__agrega_tipo_adecuacion.sql`:
   ```sql
   ALTER TABLE estudiantes
       ADD COLUMN tipo_adecuacion VARCHAR(50);
   ```

2. **Entidad JPA** — `src/main/java/com/atara/deb/ataraapi/model/Estudiante.java`:
   - Agregar campo `String tipoAdecuacion` con `@Column(name = "tipo_adecuacion", length = 50)`
   - Considerar convertirlo a un enum `TipoAdecuacion` en `model/enums/`

3. **DTOs** — `src/main/java/com/atara/deb/ataraapi/dto/estudiante/`:
   - Agregar el campo en `EstudianteRequestDto` y `EstudianteResponseDto`

**Dificultad:** Baja. Columna nullable, no rompe nada existente.

---

## Lo que NO necesita cambios

Las siguientes columnas de `estudiantes` están vacías al importar desde el PIAD pero **no representan un problema** porque son nullable y pueden ser útiles para carga manual posterior:

- `fecha_nacimiento`
- `genero`
- `nombre_acudiente`
- `telefono_acudiente`
- `correo_acudiente`

No eliminarlas — no ocupan espacio significativo y podrían necesitarse.

---

## Notas de calidad de datos del PIAD

Al importar estudiantes desde el extractor hay casos con datos que parecen ruido OCR. De momento se dejan pasar tal como vienen (incluyendo valores en el campo `codigoEstado` que podrían ser ruido). Si se confirma que son artefactos, se evaluará agregar una opción de limpieza en el extractor.

Casos observados que requieren revisión manual:

| # | Problema | Detalle |
|---|---|---|
| 7 | Apellido con artefacto OCR | `ÑGALEANO` → probablemente `GALEANO` |
| 18 | Nombre no reconocido por OCR | `nombre = "nn"` |
| 19 | Fecha de matrícula inválida | `fechaMatricula = 1/1/1` |
