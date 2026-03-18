# ATARA — Sistema Web de Alertas Tempranas y Análisis del Rendimiento Académico

## Tabla de contenidos

1. [Descripción del sistema](#1-descripción-del-sistema)
2. [Stack tecnológico](#2-stack-tecnológico)
3. [Arquitectura de la base de datos](#3-arquitectura-de-la-base-de-datos)
4. [Tablas principales](#4-tablas-principales)
5. [Relaciones clave](#5-relaciones-clave)
6. [Jerarquía pedagógica](#6-jerarquía-pedagógica)
7. [Sistema de evaluación](#7-sistema-de-evaluación)
8. [Sistema de alertas](#8-sistema-de-alertas)
9. [Estructura académica](#9-estructura-académica)
10. [Decisiones de diseño](#10-decisiones-de-diseño)
11. [Cómo ejecutar el script SQL](#11-cómo-ejecutar-el-script-sql)
12. [Integración con Spring Boot](#12-integración-con-spring-boot)

---

## 1. Descripción del sistema

ATARA es una plataforma web para instituciones de educación primaria (1° a 6° grado) que permite:

- **Gestionar** la estructura académica: centros educativos, niveles, secciones y estudiantes.
- **Registrar evaluaciones** basadas en una escala Likert de 1 a 4 por criterio pedagógico.
- **Analizar** el rendimiento en cinco dimensiones: Rendimiento Académico, Participación, Hábitos de Estudio, Factores Socioemocionales .
- **Generar alertas tempranas** automáticas y manuales cuando un estudiante cae por debajo de umbrales configurables.
- **Producir reportes** por nivel, sección, período, docente o centro educativo.

El sistema está diseñado para el contexto del **Ministerio de Educación Pública (MEP) de Costa Rica**, incorporando conceptos como circuito educativo y dirección regional.

---

## 2. Stack tecnológico

| Capa         | Tecnología                    |
|--------------|-------------------------------|
| Frontend     | React (SPA)                   |
| Backend      | Java 17 + Spring Boot 4.x     |
| ORM          | Spring Data JPA (Hibernate)   |
| Seguridad    | Spring Security + JWT         |
| Base de datos| PostgreSQL 15+                |
| Build        | Maven                         |
| Utilidades   | Lombok                        |

---

## 3. Arquitectura de la base de datos

### Diagrama de módulos

```
┌─────────────────────────────────────────────────────────────────┐
│  CATÁLOGOS BASE                                                 │
│  roles · anios_lectivos · niveles · escalas_valoracion         │
└──────────────────────────┬──────────────────────────────────────┘
                           │
┌──────────────────────────▼──────────────────────────────────────┐
│  ESTRUCTURA EDUCATIVA                                           │
│  centros_educativos ──< secciones (nivel_id FK, anio_lectivo)  │
│  periodos (anio_lectivo_id FK)                                  │
│  usuarios ──< usuarios_secciones >── secciones  (M:N)          │
└──────────────────────────┬──────────────────────────────────────┘
                           │ seccion_id
┌──────────────────────────▼──────────────────────────────────────┐
│  ESTUDIANTES                                                    │
│  estudiantes (seccion_id FK, datos acudiente)                  │
└──────────────────────────┬──────────────────────────────────────┘
                           │ estudiante_id
┌──────────────────────────▼──────────────────────────────────────┐
│  MODELO PEDAGÓGICO                                              │
│  materias ──< contenidos ──< criterios_indicadores             │
│                              └── dimensiones_evaluacion        │
└──────────────────────────┬──────────────────────────────────────┘
                           │
┌──────────────────────────▼──────────────────────────────────────┐
│  EVALUACIÓN (cabecera + detalle)                                │
│  evaluaciones ──< detalle_evaluacion                           │
│                   criterio_id FK + escala_id FK                │
│  CHECK: escala.valor_numerico BETWEEN 1 AND 4                  │
│  UNIQUE: (estudiante_id, usuario_id, periodo_id)               │
└──────────────────────────┬──────────────────────────────────────┘
                           │ estudiante_id / contenido_id
┌──────────────────────────▼──────────────────────────────────────┐
│  ALERTAS                                                        │
│  configuracion_alertas (motor de reglas con umbrales)          │
│  alertas (tipo · nivel · estado · resolución)                  │
└─────────────────────────────────────────────────────────────────┘
│  AUDITORÍA                                                      │
│  registro_auditoria (accion · tabla · JSONB antes/después)     │
└─────────────────────────────────────────────────────────────────┘
```

### Conteo de objetos

| Tipo      | Cantidad |
|-----------|----------|
| Tablas    | 20       |
| Vistas    | 8        |
| Índices   | 40+      |
| Triggers  | 13       |
| Funciones | 1        |

---

## 4. Tablas principales

### Catálogos base

| Tabla                | Propósito |
|----------------------|-----------|
| `roles`              | Roles del sistema: ADMIN, DOCENTE, COORDINADOR |
| `anios_lectivos`     | Año calendario lectivo. Ancla períodos y secciones |
| `niveles`            | Grados académicos del 1° al 6°. Reemplaza el campo `nivel varchar` original |
| `escalas_valoracion` | Etiquetas Likert: 1=Insuficiente · 2=Básico · 3=Satisfactorio · 4=Destacado |

### Estructura educativa

| Tabla                 | Propósito |
|-----------------------|-----------|
| `centros_educativos`  | Escuelas registradas. Incluye `circuito` y `direccion_regional` (MEP) |
| `periodos`            | Períodos bimensuales dentro de un año lectivo |
| `usuarios`            | Usuarios con rol, estado y timestamps |
| `secciones`           | Grupo de clase: centro + nivel + año + nombre ('A', 'B', …) |
| `usuarios_secciones`  | Asignación M:N docente ↔ sección |

### Estudiantes

| Tabla         | Propósito |
|---------------|-----------|
| `estudiantes` | Estudiante con datos del acudiente y estado (ACTIVO/INACTIVO/RETIRADO) |

### Modelo pedagógico

| Tabla                    | Propósito |
|--------------------------|-----------|
| `materias`               | Asignaturas (Matemáticas, Español, Ciencias…) |
| `contenidos`             | Temas dentro de cada materia |
| `dimensiones_evaluacion` | Los 5 ejes de evaluación con peso relativo |
| `criterios_indicadores`  | Ítems observables evaluados con la escala Likert |

### Evaluación

| Tabla                | Propósito |
|----------------------|-----------|
| `evaluaciones`       | Cabecera: estudiante + docente + período + sección |
| `detalle_evaluacion` | Una fila por criterio evaluado, con `escala_id` FK |

### Alertas

| Tabla                   | Propósito |
|-------------------------|-----------|
| `configuracion_alertas` | Reglas configurables con umbrales numéricos |
| `alertas`               | Alertas generadas con tipo, nivel, estado y resolución |

### Seguridad / Sesiones

| Tabla                | Propósito |
|----------------------|-----------|
| `tokens_refresh`     | Refresh tokens JWT; almacena hash SHA-256 para revocación segura |
| `registro_auditoria` | Log inmutable de acciones: INSERT/UPDATE/DELETE/LOGIN/EXPORT |

---

## 5. Relaciones clave

```
anios_lectivos       ──< periodos
anios_lectivos       ──< secciones
niveles              ──< secciones
centros_educativos   ──< secciones
roles                ──< usuarios
usuarios             ──< usuarios_secciones >── secciones
secciones            ──< estudiantes
materias             ──< contenidos
contenidos           ──< criterios_indicadores
dimensiones_evaluacion ──< criterios_indicadores
evaluaciones         ──< detalle_evaluacion
criterios_indicadores ──< detalle_evaluacion
escalas_valoracion   ──< detalle_evaluacion
estudiantes          ──< evaluaciones
configuracion_alertas ──< alertas
estudiantes          ──< alertas
contenidos           ──< alertas
periodos             ──< alertas
```

---

## 6. Jerarquía pedagógica

El modelo sigue una jerarquía de tres niveles:

```
MATERIA  (Matemáticas)
  └── CONTENIDO  (Operaciones básicas)
        └── CRITERIO / INDICADOR  (Dominio de la multiplicación)
              ├── dimension_id  →  DIMENSIÓN  (Rendimiento Académico)
              └── peso          →  ponderación individual del criterio
```

Esta jerarquía permite reportes cruzados:

- Por **materia** — ¿cómo van en Español?
- Por **contenido** — ¿cómo van en Comprensión lectora?
- Por **dimensión** — ¿cómo está el nivel socioemocional del grupo?
- Por combinación materia + dimensión

---

## 7. Sistema de evaluación

### Escala Likert (tabla `escalas_valoracion`)

| valor_numerico | etiqueta | nombre        |
|----------------|----------|---------------|
| 1              | I        | Insuficiente  |
| 2              | B        | Básico        |
| 3              | S        | Satisfactorio |
| 4              | D        | Destacado     |

### Flujo de registro

```
1. Docente selecciona estudiante + período activo
       ↓
2. Se crea evaluaciones (cabecera)
   UNIQUE (estudiante_id, usuario_id, periodo_id) → previene duplicados
       ↓
3. Por cada criterio → se inserta detalle_evaluacion
   criterio_id  → qué se evalúa
   escala_id    → calificación (1–4)
   UNIQUE (evaluacion_id, criterio_id) → un criterio, una vez
       ↓
4. El sistema calcula promedios y dispara alertas si corresponde
```

### Normalización del detalle

La tabla `detalle_evaluacion` solo contiene `criterio_id` y `escala_id`. Los campos `contenido_id`, `dimension_id` y `valor_numerico` del diseño original fueron eliminados porque son derivables sin pérdida de información:

```
detalle_evaluacion.criterio_id
  → criterios_indicadores.contenido_id   (qué contenido)
  → criterios_indicadores.dimension_id   (qué dimensión)
  → escala_id → escalas_valoracion.valor_numerico (valor numérico)
```

### ¿Por qué `escalas_valoracion` en lugar de solo un CHECK?

La tabla de escala permite que la UI muestre etiquetas dinámicas sin hard-codear texto, y permite i18n futura. El `CHECK (valor_numerico BETWEEN 1 AND 4)` garantiza integridad a nivel de base de datos como segunda línea de defensa.

---

## 8. Sistema de alertas

### Motor de reglas: `configuracion_alertas`

Permite a los administradores definir umbrales sin cambios de código:

| Campo             | Ejemplo                              |
|-------------------|--------------------------------------|
| `umbral_minimo`   | 1.00                                 |
| `umbral_maximo`   | 1.49                                 |
| `tipo_alerta`     | MODERADA                             |
| `nivel_resultante`| MEDIO                                |

Si el promedio de un estudiante en un contenido cae dentro del rango → se genera una alerta con ese tipo y nivel.

### Ciclo de vida de una alerta

```
ACTIVA  →  RESUELTA
        →  DESCARTADA
```

- `fecha_resolucion` solo se completa al cerrar la alerta (CHECK constraint garantiza coherencia con `estado`).
- `generada_por` registra quién disparó la alerta (sistema o docente).
- `evaluacion_id` (nullable) permite trazar qué evaluación originó la alerta; NULL para alertas manuales.
- `contenido_id` indica en qué área temática se originó el problema, útil para reportes por materia.

### Tipos y niveles de alerta

| tipo_alerta | nivel_alerta | Acción sugerida |
|-------------|--------------|-----------------|
| PREVENTIVA  | BAJO         | Monitorear; aumentar frecuencia de observación |
| MODERADA    | MEDIO        | Intervención pedagógica diferenciada |
| CRITICA     | ALTO         | Acción inmediata + contacto con el acudiente |

---

## 9. Estructura académica

### Jerarquía temporal

```
ANIO_LECTIVO  (2026: Feb – Nov)
  ├── PERIODO 1  (Feb – Mar)
  ├── PERIODO 2  (Mar – May)  ← activo
  ├── PERIODO 3  (Jun – Jul)
  └── …
```

Un período siempre pertenece a un año lectivo, lo que distingue el "Período 2 de 2024" del "Período 2 de 2026" y facilita comparativas interanuales.

### Jerarquía de ubicación

```
CENTRO_EDUCATIVO  (Escuela Simón Bolívar | Circuito 01 | DREH San José Central)
  └── SECCION  (1°A · Año 2026 · Titular: María García)
        └── ESTUDIANTE  (Luis Hernández · Acudiente: Roberto Hernández)
```

La unicidad de sección se garantiza con `UNIQUE (centro_id, nivel_id, anio_lectivo_id, nombre)`.

### Asignación de docentes

| Mecanismo              | Uso |
|------------------------|-----|
| `secciones.docente_id` | Docente titular (aula integrada, un solo responsable) |
| `usuarios_secciones`   | Cualquier docente que atienda la sección en alguna materia (M:N) |

---

## 10. Decisiones de diseño

| Decisión | Justificación |
|----------|---------------|
| `INT GENERATED BY DEFAULT AS IDENTITY` | Compatible con Spring Data JPA `@GeneratedValue(IDENTITY)` sin configuración extra |
| `DEFERRABLE INITIALLY IMMEDIATE` en FK | Permite inserciones en lote dentro de una transacción sin violar FK momentáneamente |
| `estado VARCHAR + CHECK` (no ENUM) | Más simple de gestionar con JPA/Hibernate; añadir un valor no requiere `ALTER TYPE` |
| `escalas_valoracion` como tabla | Etiquetas dinámicas; soporte de localización; CHECK como segunda barrera |
| `configuracion_alertas` como tabla | Motor de reglas sin cambios de código; configurable en producción por administrador |
| `detalle_evaluacion` sin columnas redundantes | 3NF: `contenido_id`, `dimension_id` y `valor_numerico` se derivan de `criterio_id`; elimina inconsistencias |
| `evaluaciones.seccion_id` desnormalizado | Permite `GROUP BY seccion_id` en reportes sin JOIN adicional por estudiante |
| `UNIQUE (estudiante_id, usuario_id, periodo_id)` | Previene evaluaciones duplicadas a nivel de base de datos, no solo en aplicación |
| `registro_auditoria.valores_*` como JSONB | Captura cualquier tabla sin columnas específicas por entidad |
| `fn_actualizar_updated_at` + triggers | Timestamps correctos aunque la actualización venga de herramientas externas (psql, DBeaver) |
| `anios_lectivos` + `periodos.anio_lectivo_id` | Distingue períodos entre años; historial auto-documentado |
| `niveles` tabla en lugar de `varchar` | Elimina valores arbitrarios como "1ro", "Primero", "1°"; integridad referencial garantizada |
| Nombres en español (snake_case) | Coherencia con el contexto MEP Costa Rica; facilita lectura del equipo local |

---

## 11. Cómo ejecutar el script SQL

### Prerrequisitos

- PostgreSQL 15+
- Base de datos `atara_db` creada:

```sql
CREATE DATABASE atara_db
    ENCODING 'UTF8'
    LC_COLLATE 'es_CR.UTF-8'
    LC_CTYPE   'es_CR.UTF-8';
```

### Estructura de archivos de migración

```
src/main/resources/db/
├── V1__init_schema.sql          ← Flyway ejecuta (20 tablas, 40+ índices, 13 triggers)
├── V2__sample_data.sql          ← Flyway ejecuta (seed — datos de prueba)
├── V3__queries_reference.sql    ← Flyway ejecuta (8 vistas SQL de solo lectura)
└── reference/
    └── queries_reference.sql    ← Flyway IGNORA (consultas :param para @Query JPA)
```

### Ejecución manual (sin Flyway)

```bash
psql -U postgres -d atara_db -f src/main/resources/db/V1__init_schema.sql
psql -U postgres -d atara_db -f src/main/resources/db/V2__sample_data.sql
psql -U postgres -d atara_db -f src/main/resources/db/V3__queries_reference.sql
```

### ⚠️ Passwords en datos de muestra (V2)

Los hashes de V2 son marcadores de posición de formato BCrypt válido (60 caracteres). Spring Security retornará `false` (contraseña incorrecta) sin lanzar excepción. Reemplazar antes de probar autenticación:

```java
String hash = new BCryptPasswordEncoder(12).encode("Password123!");
System.out.println(hash);
```

```sql
UPDATE usuarios SET password = '<hash_generado>'
WHERE correo IN (
    'admin@atara.mep.go.cr',
    'mgarcia@atara.mep.go.cr',
    'jperez@atara.mep.go.cr',
    'avargas@atara.mep.go.cr'
);
```

### Archivos SQL

| Archivo | Tipo | Descripción |
|---------|------|-------------|
| `V1__init_schema.sql` | Migración | 20 tablas, 40+ índices, 13 triggers, `fn_actualizar_updated_at` |
| `V2__sample_data.sql` | Migración | Seed: 4 usuarios, 2 centros, 3 secciones, 3 estudiantes, 1 evaluación, 1 alerta |
| `V3__queries_reference.sql` | Migración | 8 vistas SQL: rendimiento, riesgo, alertas, historial, secciones, docentes |
| `reference/queries_reference.sql` | Referencia | 8 consultas con `:param` para `@Query` JPA — no es migración |

### Vistas SQL (V3)

| Vista | Propósito |
|-------|-----------|
| `vw_criterios_completos` | Jerarquía criterio → contenido → materia → dimensión |
| `vw_secciones_activas` | Secciones del año lectivo en curso |
| `vw_docentes_por_seccion` | Asignaciones docente-sección del año activo |
| `vw_evaluaciones_periodo_activo` | Cabeceras de evaluación del período activo |
| `vw_rendimiento_periodo_activo` | Promedio Likert por estudiante en el período activo |
| `vw_estudiantes_en_riesgo` | Estudiantes con promedio < 2.5 (período activo) |
| `vw_alertas_activas` | Alertas en estado ACTIVA ordenadas por severidad |
| `vw_historial_por_dimension` | Promedio histórico por dimensión y período |

---

## 12. Integración con Spring Boot

### Configuración actual (manual)

`application.properties` usa `ddl-auto=update`. Ejecutar los scripts manualmente contra `atara_db` y cambiar a `validate` en producción para que Hibernate solo valide el esquema existente:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/atara_db
spring.datasource.username=postgres
spring.datasource.password=admin
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=true
```

### Integración con Flyway (recomendado para producción)

Los archivos ya usan la convención `V{n}__{descripcion}.sql` de Flyway.

**Agregar a `pom.xml`:**

```xml
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-core</artifactId>
</dependency>
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-database-postgresql</artifactId>
</dependency>
```

**Agregar a `application.properties`:**

```properties
spring.flyway.enabled=true
spring.flyway.locations=classpath:db
spring.jpa.hibernate.ddl-auto=validate
```

Flyway aplicará V1 → V2 → V3  en orden al iniciar y registrará cada migración en `flyway_schema_history`. Para cambios futuros crear `V5__descripcion.sql`.

### Referencia de mapeo JPA

```java
@Entity
@Table(name = "evaluaciones")
public class Evaluacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estudiante_id", nullable = false)
    private Estudiante estudiante;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "periodo_id", nullable = false)
    private Periodo periodo;

    @OneToMany(mappedBy = "evaluacion", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleEvaluacion> detalles = new ArrayList<>();

    @Column(name = "origen_registro", nullable = false)
    @Enumerated(EnumType.STRING)
    private OrigenRegistro origenRegistro = OrigenRegistro.MANUAL;

    // ...
}
```

---

*ATARA — MEP Costa Rica · 2026*
