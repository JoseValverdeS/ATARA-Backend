# Tessdata — Modelos de idioma para Tesseract OCR

Esta carpeta contiene los modelos de idioma (`.traineddata`) necesarios para el OCR.
Los archivos `.traineddata` **no se incluyen en el repositorio** (son binarios de 4-15 MB cada uno).

---

## Windows (desarrollo local)

1. Descargar `spa.traineddata` (español):

   ```
   https://github.com/tesseract-ocr/tessdata/raw/main/spa.traineddata
   ```

2. Colocarlo en esta carpeta:

   ```
   scripts/extractor-piad/tessdata/spa.traineddata
   ```

3. Ejecutar el extractor normalmente:

   ```bat
   ejecutar.bat
   ```

> **Nota:** tess4j incluye automáticamente los DLL de Tesseract para Windows en el JAR de Maven.
> Solo se necesita el archivo `.traineddata` — no hay que instalar nada más.

---

## Docker / Linux (producción)

En el `Dockerfile` del proyecto ATARA-Backend agregar:

```dockerfile
RUN apt-get update && apt-get install -y \
    tesseract-ocr \
    tesseract-ocr-spa \
    && rm -rf /var/lib/apt/lists/*
```

El extractor detectará automáticamente los modelos instalados por el sistema.

---

## Verificar instalación

```bash
# Linux / Docker
tesseract --list-langs
# Debe incluir "spa"

# Windows (si se instaló tesseract por separado)
"C:\Program Files\Tesseract-OCR\tesseract.exe" --list-langs
```
