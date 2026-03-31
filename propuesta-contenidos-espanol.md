# Propuesta: Uso de la Estructura Curricular de Español para Detección de Debilidades y Regresión en ATARA

**Sistema:** ATARA — Sistema de Alerta Temprana y Atención al Rendimiento Académico
**Fuente analizada:** `Estructura de los contenidos Español.xlsx`
**Fecha:** 26 de marzo de 2026
**Contexto:** Ministerio de Educación Pública de Costa Rica (MEP)

---

## 1. Estructura del Archivo

El archivo contiene una sola hoja con **58 filas de datos** distribuidas en **31 columnas**. Es una tabla de doble entrada: cada fila representa un contenido curricular y las columnas representan los grados escolares desde 1.° hasta 6.°.

### 1.1 Jerarquía de encabezados

```
Fila 1 │ I CICLO  (etiqueta de ciclo — abarca toda la hoja)
Fila 2 │ Primero/Segundo │ Tercero │ Cuarto │ Quinto │ Sexto
Fila 3 │ Saberes conceptuales │ Habilidades y actitudes  (por grado)
```

### 1.2 Mapeo de columnas

| Columnas | Grado           | Componentes                             |
|----------|-----------------|-----------------------------------------|
| 0 – 4    | Primero y Segundo | ID · Conceptual · Habilidades/Actitudes |
| 5 – 7    | Tercero           | ID · Conceptual · Habilidades/Actitudes |
| 8 – 10   | Cuarto            | ID · Conceptual · Habilidades/Actitudes |
| 11 – 13  | Quinto            | ID · Conceptual · Habilidades/Actitudes |
| 14 – 16  | Sexto             | ID · Conceptual · Habilidades/Actitudes |

> **Nota técnica:** Las IDs de los contenidos (`1.1`, `1.2`, etc.) fueron almacenadas en Excel con formato de fecha. openpyxl las lee como `datetime(2025, N, M)` donde `N` = grado y `M` = número del tema.

---

## 2. Los Tres Componentes del Contenido

El archivo fusiona lo procedimental y lo actitudinal en una sola columna: **"Habilidades y actitudes"**. El análisis permite distinguir los tres componentes del modelo MEP:

### 2.1 Componente Conceptual — *Saberes conceptuales*

El **qué saber**: definiciones, reglas, estructuras, conceptos lingüísticos.

| Grado     | Ejemplo de contenido conceptual                                               |
|-----------|-------------------------------------------------------------------------------|
| 1.° – 2.° | Conciencia fonológica; correspondencia letra-grafía; trazado de letras        |
| 3.°       | Producción de texto; tipología textual; predictores de lectura                |
| 4.°       | Semejanzas y diferencias entre tipos de texto; familia léxica; prefijos/sufijos |
| 5.°       | Estructura del texto literario; géneros literarios; propósito comunicativo    |
| 6.°       | Sinonimia, antonimia, polisemia; intertextualidad; tipos de diccionario       |

### 2.2 Componente Procedimental — *dentro de "Habilidades y actitudes"*

El **cómo hacer**: estrategias, procesos, técnicas aplicadas.

| Grado     | Ejemplo de componente procedimental                                           |
|-----------|-------------------------------------------------------------------------------|
| 3.°       | Habilidades de lectoescritura y comprensión de textos                         |
| 4.°       | Estrategias de comprensión del significado del vocabulario nuevo              |
| 4.°       | Técnicas de comprensión oral: dramatizaciones, audios, debates                |
| 5.°       | Utilización de diversas fuentes informativas — proceso de investigación       |
| 6.°       | Exposiciones orales; debates; foros; mesas redondas; dramatizaciones          |

### 2.3 Componente Actitudinal — *dentro de "Habilidades y actitudes"*

El **querer valorar**: disposiciones, valores, identidad lectora y ciudadana.

| Grado     | Ejemplo de componente actitudinal                                             |
|-----------|-------------------------------------------------------------------------------|
| 1.° – 2.° | Respeto por las normas básicas de interacción oral; confianza lectora         |
| 3.°       | Pensamiento crítico incipiente; responsabilidad tecnológica                   |
| 4.°       | Escritura precisa; creatividad responsable; análisis artístico                |
| 5.°       | Trabajo colaborativo; motivación lectora; comprensión inferencial             |
| 6.°       | Conciencia escolar crítica; placer literario; crítica literaria               |

> La evolución actitudinal — de "respeto" (1.°) → "apreciación" (4.°) → "crítica" (6.°) — es un diseño intencional del MEP: la identidad lectora se construye gradualmente y es prerequisito implícito del logro conceptual avanzado.

---

## 3. Agrupación Temática de los Contenidos

A lo largo de los 5 grados se identifican **7 ejes temáticos transversales** que se repiten y profundizan:

| Eje | Núcleo temático                                     | Cobertura principal   |
|-----|-----------------------------------------------------|-----------------------|
| 1   | Conciencia fonológica / Lectoescritura              | Intensivo en 1.° – 2.° |
| 2   | Producción textual (oral y escrita)                 | Todos los grados      |
| 3   | Comprensión lectora                                 | Desde 2.°, crítico en 4.° – 6.° |
| 4   | Vocabulario y ortografía                            | Desde 3.°, morfología avanzada en 6.° |
| 5   | Gramática                                           | Fuerte en 4.° – 5.°   |
| 6   | Literatura                                          | 4.° – 6.°             |
| 7   | Expresión oral                                      | Todos los grados      |

### Volumen de contenidos por grado

| Grado         | Cantidad de contenidos | Observación                              |
|---------------|------------------------|------------------------------------------|
| 1.° y 2.°     | ~38                    | Fase fundacional — mayor densidad        |
| 3.°           | ~16                    | Consolidación y aplicación               |
| 4.°           | ~15                    | Análisis formal                          |
| 5.°           | ~14                    | Evaluación crítica                       |
| 6.°           | ~12                    | Síntesis — mayor integración conceptual  |

El número decreciente no implica menor exigencia: los contenidos de niveles superiores integran y combinan los anteriores.

---

## 4. Progresión Entre Niveles

La progresión sigue una escalada cognitiva alineada con la taxonomía de Bloom:

### 1.° – 2.° → Reconocimiento y familiarización

- Conciencia fonológica, correspondencia letra–sonido–grafía
- Lectura aproximada y experimental
- Producción oral básica (instrucciones, relatos, anécdotas)
- *Actitud:* confianza, imaginación, curiosidad por el lenguaje

### 3.° → Aplicación

- Producción textual con intención comunicativa clara
- Primeros predictores de lectura (hipótesis textuales)
- Vocabulario ortográfico; introducción a la tipología textual
- *Actitud:* pensamiento crítico incipiente; habilidades tecnológicas responsables

### 4.° → Análisis

- Comparación de tipos de texto (narrativo, expositivo, descriptivo)
- Reglas ortográficas complejas (diptongos, hiatos, familias léxicas)
- Gramática formal: sustantivo, adjetivo, verbo, concordancia, oración
- *Actitud:* escritura precisa; análisis artístico; enriquecimiento lingüístico

### 5.° → Evaluación

- Análisis literario (género, destinatario, propósito, posición ideológica)
- Comprensión inferencial profunda
- Conexión intertextual; validación de fuentes
- *Actitud:* trabajo colaborativo; motivación lectora; crítica literaria

### 6.° → Síntesis y crítica

- Lectura crítica (ideología, posición del lector frente a los personajes)
- Validación de múltiples fuentes de información
- Expresión oral compleja: debate, foro, mesa redonda, cine-foro
- *Actitud:* conciencia escolar crítica; placer literario; valoración de fuentes

---

## 5. Propuesta: Aplicación en ATARA

### 5.1 Cadena de prerequisitos — Detección de debilidades latentes

Cada eje temático forma una cadena donde el dominio de un nivel es prerequisito del siguiente:

```
Conciencia fonológica (1.°)
  └─► Decodificación y lectura aproximada (1.° – 2.°)
        └─► Comprensión lectora básica (3.°)
              └─► Inferencia y análisis textual (4.° – 5.°)
                    └─► Crítica literaria e intertextualidad (6.°)

Producción oral básica (1.° – 2.°)
  └─► Producción textual con intención (3.°)
        └─► Coherencia y cohesión formal (4.°)
              └─► Argumentación y exposición (5.° – 6.°)
```

**Aplicación en ATARA:** si un estudiante de 4.° tiene puntuación 1 ó 2 en comprensión lectora, el sistema debe verificar sus evaluaciones previas en contenidos de la misma cadena en 3.° Si la debilidad es antigua y nunca fue atendida, la alerta debe tener urgencia **ALTA** en lugar de **MEDIA**.

**Implementación sugerida:**
Agregar al modelo `Alerta` un campo `contenidoPrerequisito` que vincule el contenido evaluado con su antecesor en la cadena. El `GlobalExceptionHandler` ya existe — solo se necesita una nueva regla de negocio en el `AlertaServiceImpl`.

---

### 5.2 Regresión real vs. mayor exigencia del nivel

Un riesgo en sistemas de alerta automática es confundir **regresión** (el estudiante domina menos que antes) con **nueva exigencia** (el estudiante enfrenta conceptos genuinamente más complejos).

La estructura del archivo permite distinguirlos mediante el eje temático y el nivel cognitivo:

| Situación                                                             | Interpretación            | Nivel de alerta |
|-----------------------------------------------------------------------|---------------------------|-----------------|
| Puntuación baja en contenido de un eje que antes dominaba **en el mismo grado** | Regresión real            | ALTA            |
| Puntuación baja en contenido nuevo de un eje que dominó en grado anterior | Nueva exigencia — monitorear | MEDIA           |
| Puntuación baja en contenido de un eje que **nunca dominó** en ningún grado | Debilidad acumulada       | ALTA            |
| Caída actitudinal (motivación, participación) sin caída conceptual aún | Señal precursora          | MEDIA — intervenir pronto |

**Implementación sugerida:**
Agregar a cada `Contenido` los campos `ejeTemático` (enum con los 7 ejes) y `nivelCognitivo` (enum: RECONOCIMIENTO / APLICACIÓN / ANÁLISIS / EVALUACIÓN / SÍNTESIS). La comparación de regresión debe hacerse dentro del mismo eje y nivel, no solo por puntaje absoluto.

---

### 5.3 El componente actitudinal como señal de alerta temprana

El componente actitudinal es el **indicador más temprano** de riesgo académico futuro. En el curriculum, actitudes como "gusto por la lectura" (4.°), "motivación lectora" (5.°) y "placer literario" (6.°) son prerequisitos implícitos de los logros conceptuales avanzados.

**Patrón de riesgo identificado:**

```
Baja en Participación o Factores Socioemocionales
  → (2–4 semanas después) Baja en componente actitudinal del contenido
      → (4–6 semanas después) Baja en Rendimiento Académico
```

Si ATARA detecta la primera caída (dimensión de evaluación) y la segunda (componente actitudinal del contenido), puede generar una alerta **antes** de que el rendimiento caiga — que es el objetivo central del sistema.

**Implementación sugerida:**
Añadir lógica en `AlertaServiceImpl` que correlacione las dimensiones de evaluación de ATARA con el componente del contenido curricular:

| Dimensión ATARA                 | Componente curricular relacionado     |
|---------------------------------|---------------------------------------|
| Factores Socioemocionales ↓     | Actitudinal de cualquier eje          |
| Participación ↓                 | Expresión oral + actitudinal          |
| Hábitos de Estudio ↓            | Procedimental (producción, estrategias)|
| Rendimiento Académico ↓         | Conceptual de los ejes 1–5            |

---

### 5.4 Ejes temáticos como grupos de alerta accionables

En lugar de alertar contenido por contenido, agrupar por eje temático produce señales más robustas y con mayor valor para la intervención:

| Si el estudiante tiene ≤2 en 3+ contenidos del eje... | Tipo de alerta recomendada                            |
|-------------------------------------------------------|-------------------------------------------------------|
| Conciencia fonológica / Lectoescritura                | Posible dislexia o retraso lector — derivar a orientación |
| Producción textual                                    | Dificultad expresiva — refuerzo en escritura          |
| Comprensión lectora                                   | Debilidad en estrategias — apoyo metodológico         |
| Vocabulario y gramática                               | Debilidad estructural — refuerzo en reglas lingüísticas |
| Literatura + componente actitudinal                   | Desconexión con el aprendizaje — riesgo de abandono   |
| Expresión oral + baja Participación (ATARA)           | Barrera socioemocional — intervención con orientador  |

---

### 5.5 Detección de desfase de ciclo

El curriculum tiene expectativas claras por ciclo. Si los contenidos en ATARA se registran con el ID del archivo (`1.x`, `2.x`, etc.), es posible detectar si un estudiante de 5.° está siendo evaluado en contenidos de nivel 3.° o 4.° — lo que indica un **desfase de ciclo acumulado**, la forma más seria de debilidad no atendida.

**Regla de negocio propuesta:**

```
SI  el grado actual del estudiante ≥ 4
Y   su puntuación en un contenido de ID 1.x ó 2.x = 1
ENTONCES  generar alerta nivel ALTA
          motivo = "Contenido prerequisito de ciclo anterior no dominado"
          acción sugerida = "Evaluación diagnóstica y plan de nivelación"
```

---

## 6. Resumen de Cambios Propuestos en ATARA

| Elemento           | Cambio propuesto                                                                    |
|--------------------|-------------------------------------------------------------------------------------|
| Modelo `Contenido` | Agregar campos: `ejeTemático` (enum 7 valores), `nivelCognitivo` (enum 5 niveles), `componenteTipo` (CONCEPTUAL / PROCEDIMENTAL / ACTITUDINAL) |
| Modelo `Alerta`    | Agregar campo: `contenidoPrerequisito` (FK a otro `Contenido`)                      |
| `AlertaServiceImpl`| Lógica de correlación: dimensión ATARA ↔ componente curricular                     |
| `AlertaServiceImpl`| Regla de desfase de ciclo: comparar ID del contenido vs. grado del estudiante       |
| Migración Flyway   | `V4__contenido_eje_nivel.sql` — nuevas columnas sin romper esquema existente       |
| Vistas (`V3`)      | Agregar vista `vw_debilidades_por_eje` agrupando por eje temático y nivel cognitivo |

---

## 7. Conclusión

El archivo `Estructura de los contenidos Español.xlsx` define una **gramática curricular completa**: contenidos identificados, agrupados en 7 ejes temáticos, organizados por ciclo escolar y distribuidos en tres componentes (conceptual, procedimental, actitudinal).

Para ATARA, el mayor valor está en usar esa estructura como un **grafo de dependencias**, donde:

1. Las debilidades en niveles superiores se rastrean hacia sus raíces en niveles anteriores.
2. La caída en el componente actitudinal se usa como **señal precursora** antes de que el rendimiento caiga.
3. Los ejes temáticos permiten agrupar alertas en intervenciones concretas y accionables.
4. El desfase entre el grado del estudiante y el nivel de los contenidos que domina revela el riesgo más severo: la debilidad acumulada de ciclo.

Este enfoque convierte a ATARA de un sistema reactivo (alerta cuando ya hay bajo rendimiento) a un sistema verdaderamente **predictivo** (alerta cuando el patrón indica que el bajo rendimiento es inminente).

---

*Documento generado con base en el análisis de `Estructura de los contenidos Español.xlsx` — Proyecto ATARA Backend*
