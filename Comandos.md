# Comandos — ATARA

> **Shell:** PowerShell (Windows). Usar `;` para encadenar comandos, no `&&`.

---

## Arranque completo

Abrir **dos terminales** en la raíz del proyecto:

**Terminal 1 — DB + Backend:**
```powershell
docker-compose up -d ; .\mvnw spring-boot:run
```

**Terminal 2 — Frontend:**
```powershell
cd temp-frontend ; npm run dev
```

| Servicio   | URL                       |
|------------|---------------------------|
| Backend    | http://localhost:8081     |
| Frontend   | http://localhost:3000     |
| PostgreSQL | localhost:5433 / atara_db |

---

## Base de datos (Docker)

```powershell
docker-compose up -d          # Iniciar PostgreSQL
docker-compose down           # Detener PostgreSQL
docker-compose down -v        # Detener Y borrar datos (reset total)
docker-compose ps             # Ver estado
docker-compose logs postgres  # Ver logs de la DB
```

```powershell
# Conectarse a psql
docker exec -it atara-postgres psql -U atara_user -d atara_db
```

Comandos útiles dentro de psql:
```sql
\dt                          -- listar tablas
\d nombre_tabla              -- ver columnas de una tabla
SELECT version, description, installed_on, success FROM flyway_schema_history ORDER BY installed_rank;
SELECT * FROM registro_auditoria ORDER BY created_at DESC LIMIT 10;
\q                           -- salir
```

---

## Backend (Maven)

```powershell
.\mvnw spring-boot:run                         # Ejecutar en dev
.\mvnw clean package                           # Compilar JAR
.\mvnw clean package -DskipTests              # Compilar sin tests
java -jar target/atara-api-0.0.1-SNAPSHOT.jar # Ejecutar el JAR

.\mvnw test                                    # Todos los tests
.\mvnw test -Dtest=NombreClase                # Una clase
.\mvnw test "-Dtest=NombreClase#nombreMetodo" # Un método (comillas necesarias en PS)
```

---

## Frontend (Vite)

```powershell
cd temp-frontend
npm run dev    # Puerto 3000, proxea /api → http://localhost:8081
npm run build  # Build de producción (genera dist/)
```

---

## Reset completo (DB + migraciones)

Borra todos los datos y re-aplica las migraciones desde cero:

```powershell
docker-compose down -v ; docker-compose up -d ; .\mvnw spring-boot:run
```

---

## Extractor PIAD (OCR)

Lee `Lista PIAD.pdf` desde la raíz y genera archivos en `scripts/extractor-piad/output/`.

```powershell
# Desde la raíz del proyecto
scripts\extractor-piad\ejecutar.bat
```

Salida:
- `scripts/extractor-piad/output/estudiantes.json`
- `scripts/extractor-piad/output/estudiantes.csv`
- `scripts/extractor-piad/output/estudiantes.xlsx`

> Requiere `spa.traineddata` en `scripts/extractor-piad/tessdata/`. Ver `INSTRUCCIONES.md` dentro de esa carpeta.

---

## Pruebas rápidas de endpoints

```powershell
# Listar estudiantes
curl http://localhost:8081/api/estudiantes

# Estudiante por ID
curl http://localhost:8081/api/estudiantes/1

# Año lectivo activo
curl http://localhost:8081/api/anios-lectivos/activo

# Estado de la app (actuator)
curl http://localhost:8081/actuator/health

# Crear estudiante (PowerShell necesita Invoke-RestMethod o comillas escapadas)
Invoke-RestMethod -Method Post -Uri http://localhost:8081/api/estudiantes `
  -ContentType "application/json" `
  -Body '{"nombre":"Juan","apellido1":"Perez","apellido2":"Garcia","cedula":"123456789","fechaNacimiento":"2010-03-15","genero":"M"}'

# Alertas de un estudiante en un periodo
curl "http://localhost:8081/api/alertas/estudiante/1?periodoId=1"
```
