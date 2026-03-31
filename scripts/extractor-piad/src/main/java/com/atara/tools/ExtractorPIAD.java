package com.atara.tools;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;

import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.regex.*;

/**
 * ExtractorPIAD — versión OCR con reconstrucción de filas
 * =========================================================
 * Extrae la tabla de estudiantes del PDF "Lista PIAD" del sistema SIGCE (MEP Costa Rica).
 *
 * <p>El PDF de SIGCE tiene el texto convertido a <b>trazados vectoriales</b> (paths),
 * no texto embebido. Además, la estructura de tabla hace que Tesseract lea columna a columna
 * si se procesa la página completa. Para garantizar precisión, el extractor:</p>
 * <ol>
 *   <li>Renderiza el PDF a imagen de alta resolución (300 DPI)</li>
 *   <li>Detecta automáticamente las filas de la tabla analizando los patrones de píxeles horizontales</li>
 *   <li>Recorta cada fila individualmente y aplica OCR con PSM 7 (línea única)</li>
 *   <li>Parsea cada línea con anclas regex (cédula, tipo adecuación, nivel)</li>
 * </ol>
 *
 * <h3>Salidas:</h3>
 * <ul>
 *   <li>{@code output/estudiantes.json}  — array JSON estructurado</li>
 *   <li>{@code output/estudiantes.csv}   — CSV para importación / verificación</li>
 *   <li>{@code output/estudiantes.xlsx}  — Excel formateado</li>
 *   <li>{@code output/raw_ocr.txt}       — texto OCR crudo de cada fila (debug)</li>
 * </ul>
 *
 * <h3>Requisitos de Tesseract:</h3>
 * <ul>
 *   <li><b>Windows</b>: tess4j incluye los DLL automáticamente.
 *       Descargar {@code spa.traineddata} en {@code scripts/extractor-piad/tessdata/}.
 *       Ver {@code tessdata/INSTRUCCIONES.md}.</li>
 *   <li><b>Docker / Linux</b>: {@code apt-get install -y tesseract-ocr tesseract-ocr-spa}</li>
 * </ul>
 *
 * <h3>Futura integración con ATARA-Backend:</h3>
 * <ul>
 *   <li>{@code extraerTabla(String)} → {@code extraerTabla(InputStream)} en un {@code @Service}</li>
 *   <li>{@code EstudiantePIAD} → {@code EstudiantePIADRequestDTO} con anotaciones Bean Validation</li>
 *   <li>Controlador: recibe {@code MultipartFile}, delega al servicio, persiste en DB</li>
 * </ul>
 */
public class ExtractorPIAD {

    // ─── Configuración OCR ─────────────────────────────────────────────────────

    /**
     * DPI para renderizado. 400 es mejor que 300 para letras delgadas como la 'i' de "Primero"
     * en fuentes pequeñas, sin ser tan grande como para ralentizar el OCR.
     */
    private static final int RENDER_DPI = 400;

    /**
     * Umbral de brillo (0-255) para detectar líneas en blanco entre filas de la tabla.
     * Una fila de píxeles se considera "espacio vacío" si su brillo promedio supera este valor.
     */
    private static final int BRIGHTNESS_THRESHOLD = 245;

    /**
     * Mínimo de filas de píxeles "vacías" consecutivas para considerar una separación entre filas.
     * A 400 DPI los bordes de celda suelen ser ≥2 px.
     */
    private static final int MIN_GAP_HEIGHT = 2;

    /** Padding en píxeles añadido arriba y abajo de cada banda al recortarla para OCR. */
    private static final int BAND_PADDING = 4;

    // ─── Patrones regex ───────────────────────────────────────────────────────

    /**
     * Cédulas costarricenses con tolerancia a errores OCR:
     *   - Estándar CR/DIMEX: 2-1062-0937 (puede tener 3-5 dígitos en segmento medio por OCR)
     *   - YREX (provisional): YR2024-35171
     * El último segmento acepta U (OCR frecuente de 0) y dígitos extra.
     */
    private static final Pattern CEDULA = Pattern.compile(
        "(\\d-\\d{3,5}-[\\dU]{3,5}|[A-Z]{1,3}\\d{4,}-[\\d/|]+)"
    );

    /**
     * Tipo de adecuación con tolerancia OCR:
     *   "Sin" → "Sn" (la 'i' delgada se pierde), "sin" (minúscula por OCR).
     */
    private static final Pattern TIPO_ADECUACION = Pattern.compile(
        "(Si?n adecuaci[oó]n|Con adecuaci[oó]n|"
        + "Adecuaci[oó]n significativa|Adecuaci[oó]n no significativa|"
        + "Adecuaci[oó]n de acceso)",
        Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE
    );

    /**
     * Niveles de enseñanza del MEP con tolerancia a errores OCR:
     * - "Primero" → "Prmero", "Prnmero", "Frmero" (F/P confundidas, 'i' delgada omitida).
     * - Pattern [PF]r + 2-7 letras + "ro" captura todas las variantes conocidas.
     */
    private static final Pattern NIVEL = Pattern.compile(
        "([PF]r[a-záéíóúñ]{2,7}ro|Primero|Segundo|Tercero|Cuarto|Quinto|Sexto"
        + "|S[eé]timo|Octavo|Noveno|D[eé]cimo|Und[eé]cimo)",
        Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE
    );

    /**
     * Fecha de matrícula. Acepta años de 1-4 dígitos para manejar truncamiento OCR.
     * Ejemplo: "1/1/1" (OCR truncado de "1/1/2026") → se extrae y el usuario verifica.
     */
    private static final Pattern FECHA = Pattern.compile("(\\d{1,2}/\\d{1,2}/\\d{1,4})");

    // ─── Modelo de datos ──────────────────────────────────────────────────────

    /**
     * Representa un registro de la tabla "Lista PIAD".
     *
     * <p>Al integrar en ATARA-Backend, este record se convierte en un DTO con
     * anotaciones de Bean Validation (@NotBlank, @Pattern, etc.).</p>
     */
    public record EstudiantePIAD(
        int    numero,
        String cedula,
        String primerApellido,
        String segundoApellido,
        String nombre,
        String tipoAdecuacion,
        String nivel,
        int    grupo,
        String fechaMatricula,
        String codigoEstado         // "Pp1", "sm", etc. — puede ser null
    ) {}

    // ─── Punto de entrada ─────────────────────────────────────────────────────

    public static void main(String[] args) throws Exception {
        String pdfPath = (args.length > 0) ? args[0] : "Lista PIAD.pdf";

        System.out.println("╔══════════════════════════════════════╗");
        System.out.println("║   Extractor PIAD — ATARA Backend     ║");
        System.out.println("╚══════════════════════════════════════╝");
        System.out.println();
        System.out.println("Archivo  : " + pdfPath);
        System.out.println("Tessdata : " + System.getProperty("tessdata.dir", "(auto-detect)"));
        System.out.println();

        List<EstudiantePIAD> estudiantes = extraerTabla(pdfPath);

        if (estudiantes.isEmpty()) {
            System.out.println();
            System.out.println("⚠  No se extrajo ningún registro.");
            System.out.println("   Revisar output/raw_ocr.txt para inspeccionar el texto reconocido por OCR.");
            System.out.println("   Si las líneas están vacías, verificar la instalación de tessdata/spa.traineddata.");
            return;
        }

        System.out.printf("%n✓  %d estudiante(s) extraídos%n%n", estudiantes.size());

        imprimirTabla(estudiantes);
        guardarJSON(  estudiantes, "output/estudiantes.json");
        guardarCSV(   estudiantes, "output/estudiantes.csv");
        guardarExcel( estudiantes, "output/estudiantes.xlsx");

        System.out.println();
        System.out.println("Archivos generados:");
        System.out.println("  output/estudiantes.json    ← integración futura ATARA-Backend");
        System.out.println("  output/estudiantes.csv     ← importación / verificación manual");
        System.out.println("  output/estudiantes.xlsx    ← Excel formateado");
        System.out.println("  output/raw_ocr.txt         ← texto OCR de cada fila (debug)");
    }

    // ─── Extracción principal ─────────────────────────────────────────────────

    /**
     * Extrae la tabla de estudiantes del PDF indicado.
     *
     * <p>Al integrar en Spring Boot, cambiar la firma a
     * {@code extraerTabla(InputStream pdfStream)} para recibir un MultipartFile.</p>
     *
     * @param pdfPath ruta al PDF (absoluta o relativa al CWD)
     * @return lista de estudiantes ordenada por número de fila
     */
    public static List<EstudiantePIAD> extraerTabla(String pdfPath) throws Exception {
        // Paso 1 — Renderizar PDF a imagen de alta resolución
        List<BufferedImage> paginas = renderizarPDF(pdfPath);

        // Paso 2 — Detectar filas y aplicar OCR fila por fila
        Tesseract tesseract = crearTesseract();
        List<EstudiantePIAD> resultado = new ArrayList<>();
        StringBuilder rawOcr = new StringBuilder();

        Files.createDirectories(Path.of("output"));

        for (int p = 0; p < paginas.size(); p++) {
            BufferedImage pagina = paginas.get(p);
            System.out.printf("Procesando página %d/%d (%dx%d px)...%n",
                p + 1, paginas.size(), pagina.getWidth(), pagina.getHeight());

            // Paso 1: detectar bandas crudas y agrupar las adyacentes
            // (celdas cuyo contenido ocupa 2 líneas aparecen como 2 bandas muy próximas)
            List<int[]> bandasCrudas = detectarBandas(pagina);
            List<int[]> filas = agruparBandas(bandasCrudas);
            System.out.printf("  %d bandas detectadas → %d filas agrupadas%n",
                bandasCrudas.size(), filas.size());

            for (int[] fila : filas) {
                int yStart = fila[0];
                int yEnd   = fila[1];
                int alto   = yEnd - yStart;

                // Ignorar grupos demasiado pequeños (ruido puro) o gigantes (encabezado de página)
                if (alto < 8 || alto > 150) continue;

                // Recortar con padding vertical para capturar ascendentes/descendentes
                int yStartPad = Math.max(0, yStart - BAND_PADDING);
                int yEndPad   = Math.min(pagina.getHeight(), yEnd + BAND_PADDING);
                BufferedImage recorte = pagina.getSubimage(
                    0, yStartPad, pagina.getWidth(), yEndPad - yStartPad);

                // OCR — PSM 6 para grupos multi-línea, PSM 7 para línea única
                int psm = (alto > 30) ? 6 : 7;
                tesseract.setPageSegMode(psm);

                String textoFila;
                try {
                    textoFila = tesseract.doOCR(recorte).trim();
                } catch (TesseractException e) {
                    System.err.printf("  ⚠ OCR falló en y=%d-%d: %s%n", yStart, yEnd, e.getMessage());
                    continue;
                }

                textoFila = normalizarLineaOCR(textoFila);

                rawOcr.append(String.format("[p%d y=%d-%d h=%d psm=%d] %s%n",
                    p + 1, yStart, yEnd, alto, psm, textoFila));

                if (textoFila.isEmpty()) continue;

                // Solo procesar si hay una cédula reconocible
                Matcher cedulaMatcher = CEDULA.matcher(textoFila);
                if (!cedulaMatcher.find()) continue;

                EstudiantePIAD est = parsearLinea(textoFila, cedulaMatcher);
                if (est != null) {
                    resultado.add(est);
                    System.out.printf("  ✓ Fila %d — %s %s %s%n",
                        est.numero(), est.cedula(), est.primerApellido(), est.nombre());
                }
            }
        }

        Files.writeString(Path.of("output/raw_ocr.txt"), rawOcr.toString(), StandardCharsets.UTF_8);

        resultado.sort(Comparator.comparingInt(EstudiantePIAD::numero));
        return resultado;
    }

    // ─── Renderizado PDF → imagen ─────────────────────────────────────────────

    private static List<BufferedImage> renderizarPDF(String pdfPath) throws IOException {
        List<BufferedImage> paginas = new ArrayList<>();
        try (PDDocument doc = Loader.loadPDF(new File(pdfPath))) {
            PDFRenderer renderer = new PDFRenderer(doc);
            int total = doc.getNumberOfPages();
            System.out.printf("PDF cargado: %d página(s). Renderizando a %d DPI...%n", total, RENDER_DPI);
            for (int i = 0; i < total; i++) {
                BufferedImage img = renderer.renderImageWithDPI(i, RENDER_DPI, ImageType.RGB);
                paginas.add(img);
                System.out.printf("  Página %d/%d → %dx%d px%n", i + 1, total, img.getWidth(), img.getHeight());
            }
        }
        return paginas;
    }

    // ─── Detección de bandas horizontales (filas de la tabla) ────────────────

    /**
     * Detecta las bandas horizontales de texto en la imagen analizando el brillo
     * promedio de cada fila de píxeles.
     *
     * <p>Una "banda" es un bloque continuo de filas de píxeles no vacías (oscuras).
     * Las separaciones entre celdas de tabla aparecen como filas casi blancas.</p>
     *
     * @param imagen imagen completa de la página
     * @return lista de [yStart, yEnd] para cada banda detectada
     */
    private static List<int[]> detectarBandas(BufferedImage imagen) {
        int ancho  = imagen.getWidth();
        int alto   = imagen.getHeight();
        List<int[]> bandas = new ArrayList<>();

        boolean enBanda     = false;
        int     bandaStart  = 0;
        int     gapCount    = 0;

        for (int y = 0; y < alto; y++) {
            // Calcular brillo promedio de la fila (muestreo cada 4 píxeles para velocidad)
            long sumaRGB = 0;
            int  muestras = 0;
            for (int x = 0; x < ancho; x += 4) {
                int rgb = imagen.getRGB(x, y);
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8)  & 0xFF;
                int b = rgb & 0xFF;
                sumaRGB += (r + g + b) / 3;
                muestras++;
            }
            double brillo = (double) sumaRGB / muestras;
            boolean filaVacia = (brillo >= BRIGHTNESS_THRESHOLD);

            if (!enBanda && !filaVacia) {
                // Inicio de una nueva banda de texto
                enBanda    = true;
                bandaStart = y;
                gapCount   = 0;
            } else if (enBanda && filaVacia) {
                gapCount++;
                if (gapCount >= MIN_GAP_HEIGHT) {
                    // Fin de la banda
                    enBanda = false;
                    bandas.add(new int[]{bandaStart, y - gapCount + 1});
                    gapCount = 0;
                }
            } else if (enBanda && !filaVacia) {
                gapCount = 0; // Resetear contador de gap dentro de una banda
            }
        }

        // Cerrar la última banda si la página termina con texto
        if (enBanda) {
            bandas.add(new int[]{bandaStart, alto});
        }

        return bandas;
    }

    /**
     * Agrupa bandas adyacentes cuyo gap entre sí sea menor a {@code MAX_INTRA_ROW_GAP} píxeles.
     *
     * <p>En el SIGCE, algunas celdas tienen contenido en dos líneas (por ejemplo, nombres largos).
     * Esas dos sub-bandas deben procesarse como una sola fila de la tabla para que el OCR
     * capture todo el contenido en un único recorte.</p>
     *
     * <p>Un gap pequeño (≤ umbral) indica que las bandas pertenecen a la misma celda.
     * Un gap grande indica separación entre filas distintas de la tabla.</p>
     */
    private static List<int[]> agruparBandas(List<int[]> bandas) {
        // A 400 DPI, el espaciado entre filas de la tabla es ~40-50 px.
        // Dentro de una misma celda de dos líneas, el gap suele ser ≤ 8 px.
        final int MAX_INTRA_ROW_GAP = 12;

        List<int[]> grupos = new ArrayList<>();
        if (bandas.isEmpty()) return grupos;

        int gStart = bandas.get(0)[0];
        int gEnd   = bandas.get(0)[1];

        for (int i = 1; i < bandas.size(); i++) {
            int bStart = bandas.get(i)[0];
            int bEnd   = bandas.get(i)[1];
            int gap    = bStart - gEnd;

            if (gap <= MAX_INTRA_ROW_GAP) {
                // Misma fila → extender el grupo actual
                gEnd = bEnd;
            } else {
                // Nueva fila → guardar grupo anterior y empezar uno nuevo
                grupos.add(new int[]{gStart, gEnd});
                gStart = bStart;
                gEnd   = bEnd;
            }
        }
        grupos.add(new int[]{gStart, gEnd}); // último grupo
        return grupos;
    }

    // ─── OCR ──────────────────────────────────────────────────────────────────

    private static Tesseract crearTesseract() {
        Tesseract tesseract = new Tesseract();
        String tessdata = resolverRutaTessdata();
        tesseract.setDatapath(tessdata);
        tesseract.setLanguage("spa");
        // PSM 7: tratar cada recorte como una única línea de texto
        // (ideal para OCR fila por fila de una tabla)
        tesseract.setPageSegMode(7);
        // OEM 1: LSTM únicamente (más preciso que el engine legacy)
        tesseract.setOcrEngineMode(1);
        System.out.println("OCR configurado — tessdata: " + tessdata + " | PSM 7 | OEM 1 (LSTM)");
        return tesseract;
    }

    /**
     * Determina la ruta de la carpeta que contiene directamente los {@code .traineddata}.
     *
     * <p>Orden de búsqueda:</p>
     * <ol>
     *   <li>Propiedad de sistema {@code tessdata.dir} (inyectada por el exec-maven-plugin)</li>
     *   <li>Variable de entorno {@code TESSDATA_PREFIX}</li>
     *   <li>Rutas estándar de instalación en Linux / Docker</li>
     *   <li>Directorio actual (fallback con advertencia)</li>
     * </ol>
     */
    private static String resolverRutaTessdata() {
        // 1. Propiedad Maven: scripts/extractor-piad/tessdata/
        String prop = System.getProperty("tessdata.dir");
        if (prop != null && new File(prop).isDirectory()) return prop;

        // 2. Variable de entorno TESSDATA_PREFIX
        String env = System.getenv("TESSDATA_PREFIX");
        if (env != null && new File(env).isDirectory()) return env;

        // 3. Rutas estándar Linux / Docker con apt-get install
        for (String path : List.of(
                "/usr/share/tesseract-ocr/5/tessdata",
                "/usr/share/tesseract-ocr/4/tessdata",
                "/usr/local/share/tessdata")) {
            if (new File(path).isDirectory()) return path;
        }

        // 4. Fallback con instrucciones
        System.err.println();
        System.err.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.err.println("⚠  No se encontró la carpeta tessdata con spa.traineddata");
        System.err.println();
        System.err.println("   Windows: coloque spa.traineddata en:");
        System.err.println("   scripts/extractor-piad/tessdata/spa.traineddata");
        System.err.println("   (ver tessdata/INSTRUCCIONES.md)");
        System.err.println();
        System.err.println("   Docker/Linux: agregue al Dockerfile:");
        System.err.println("   RUN apt-get install -y tesseract-ocr tesseract-ocr-spa");
        System.err.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.err.println();
        return ".";
    }

    // ─── Parseo de línea ──────────────────────────────────────────────────────

    /**
     * Parsea una línea OCR que contiene una cédula y extrae todos los campos.
     *
     * <p>Estrategia:</p>
     * <ol>
     *   <li>La <b>cédula</b> actúa como ancla principal.</li>
     *   <li>Prefijo (antes de la cédula): número de fila + código de estado opcional ("Pp1", "sm").</li>
     *   <li>Sufijo (después de la cédula):
     *       {@code PRIMER_AP  SEGUNDO_AP  NOMBRE  TIPO_ADECUACIÓN  NIVEL  GRUPO  FECHA}</li>
     *   <li><b>Tipo adecuación</b> y <b>Nivel</b> son valores fijos del SIGCE,
     *       usados como anclas secundarias para delimitar el nombre.</li>
     * </ol>
     */
    private static EstudiantePIAD parsearLinea(String linea, Matcher cedulaMatcher) {
        try {
            String cedula      = normalizarCedula(cedulaMatcher.group(1));
            int    cedulaStart = cedulaMatcher.start();
            int    cedulaEnd   = cedulaMatcher.end();

            // ── Prefijo ───────────────────────────────────────────────────────
            String prefijo      = linea.substring(0, cedulaStart).trim();
            int    numero       = extraerNumeroFila(prefijo);
            String codigoEstado = extraerCodigoEstado(prefijo);

            if (numero <= 0) return null;

            // ── Sufijo ────────────────────────────────────────────────────────
            String sufijo = linea.substring(cedulaEnd).trim();

            Matcher tipoMatcher = TIPO_ADECUACION.matcher(sufijo);
            if (!tipoMatcher.find()) {
                System.err.printf("  ⚠ Fila %d: 'Tipo adecuación' no encontrado en: %s%n", numero, sufijo);
                return null;
            }

            String antesDelTipo   = sufijo.substring(0, tipoMatcher.start()).trim();
            String tipoAdecuacion = normalizar(tipoMatcher.group(1));
            String despuesDelTipo = sufijo.substring(tipoMatcher.end()).trim();

            // ── Apellidos + Nombre ────────────────────────────────────────────
            // Filtrar tokens vacíos o de ruido antes de asignar apellidos/nombre
            List<String> tokens = new ArrayList<>();
            for (String t : antesDelTipo.split("\\s+")) {
                String limpio = limpiarToken(t);
                if (!limpio.isEmpty()) tokens.add(limpio);
            }
            if (tokens.size() < 3) {
                System.err.printf("  ⚠ Fila %d: tokens insuficientes para nombre (%d): '%s'%n",
                    numero, tokens.size(), antesDelTipo);
                return null;
            }
            String primerApellido  = tokens.get(0);
            String segundoApellido = tokens.get(1);
            String nombre = String.join(" ", tokens.subList(2, tokens.size()));

            // ── Nivel ─────────────────────────────────────────────────────────
            Matcher nivelMatcher = NIVEL.matcher(despuesDelTipo);
            if (!nivelMatcher.find()) {
                System.err.printf("  ⚠ Fila %d: 'Nivel' no encontrado en: %s%n", numero, despuesDelTipo);
                return null;
            }
            String nivel           = nivelMatcher.group(1);
            String despuesDelNivel = despuesDelTipo.substring(nivelMatcher.end()).trim();

            // ── Grupo y Fecha ─────────────────────────────────────────────────
            String[] resto = despuesDelNivel.split("\\s+", 3);
            if (resto.length < 2) {
                System.err.printf("  ⚠ Fila %d: grupo/fecha no encontrados en: '%s'%n",
                    numero, despuesDelNivel);
                return null;
            }

            int grupo;
            try {
                grupo = Integer.parseInt(resto[0]);
            } catch (NumberFormatException e) {
                System.err.printf("  ⚠ Fila %d: grupo no numérico '%s'%n", numero, resto[0]);
                return null;
            }

            String textoResto = String.join(" ", Arrays.copyOfRange(resto, 1, resto.length));
            Matcher fechaMatcher = FECHA.matcher(textoResto);
            if (!fechaMatcher.find()) {
                System.err.printf("  ⚠ Fila %d: fecha no encontrada en: '%s'%n", numero, textoResto);
                return null;
            }
            String fecha = fechaMatcher.group(1);

            return new EstudiantePIAD(
                numero, cedula,
                primerApellido, segundoApellido, nombre,
                tipoAdecuacion, nivel, grupo, fecha,
                codigoEstado
            );

        } catch (Exception e) {
            System.err.println("  ⚠ Error parseando: " + linea + " | " + e.getMessage());
            return null;
        }
    }

    // ─── Utilidades de parseo ─────────────────────────────────────────────────

    /**
     * Extrae el número de fila del prefijo, con tolerancia a errores OCR comunes:
     *   "7 Pp1" → 7, "19 sm" → 19, "1" → 1
     *   "|" (OCR de 1) → 1, "||" (OCR de 11) → 11
     *   "ro" (ruido) → -1 (ignorar)
     */
    private static int extraerNumeroFila(String prefijo) {
        if (prefijo.isEmpty()) return -1;

        // Normalizar sustituciones OCR de dígitos en el prefijo
        String p = prefijo.trim()
            .replace("|", "1")  // | → 1 (borde de tabla confundido con dígito)
            .replace("]", "1")  // ] → 1 (corchete confundido con 1)
            .replace("l", "1")  // l minúscula → 1
            .replace("Z", "2")  // Z mayúscula → 2 (confusión OCR frecuente)
            .replaceAll("[^0-9a-zA-Z\\s]", " ") // limpiar ruido restante
            .trim();

        Matcher m = Pattern.compile("^(\\d{1,3})").matcher(p);
        if (m.find()) {
            try { return Integer.parseInt(m.group(1)); }
            catch (NumberFormatException ignored) { }
        }
        return -1;
    }

    /**
     * Extrae el código de estado del prefijo si existe.
     * "7 Pp1" → "Pp1", "19 sm" → "sm", "1" → null
     */
    private static String extraerCodigoEstado(String prefijo) {
        Matcher m = Pattern.compile("^\\d+\\s+([A-Za-z][A-Za-z0-9]*)").matcher(prefijo.trim());
        return m.find() ? m.group(1) : null;
    }

    /**
     * Normaliza una línea completa de texto OCR antes de parsearla.
     * Aplica sustituciones seguras que no afectan la semántica del contenido.
     */
    private static String normalizarLineaOCR(String s) {
        return s.replaceAll("[\\r\\n]+", " ")  // quitar saltos de línea internos
                .replaceAll("\\s{2,}", " ")    // comprimir espacios múltiples
                .trim();
    }

    /**
     * Elimina artefactos OCR de un token de nombre/apellido.
     * Los bordes de tabla del SIGCE producen caracteres como •, €, —, |, Ñ aislado.
     * Se conservan letras, tildes, espacios internos y guiones de nombre compuesto.
     */
    private static String limpiarToken(String s) {
        if (s == null) return "";
        // Quitar cualquier carácter que no sea letra, tilde, guión interno o espacio
        String limpio = s.replaceAll("[^\\p{L}\\p{M}'\\- ]", "").trim();
        // Quitar guiones solos o al inicio/fin
        limpio = limpio.replaceAll("^[-\\s]+|[-\\s]+$", "").trim();
        return limpio;
    }

    /** Normaliza variantes de tildes perdidas en OCR */
    private static String normalizar(String s) {
        return s.replace("adecuacion", "adecuación")
                .replace("Adecuacion", "Adecuación");
    }

    /**
     * Normaliza una cédula extraída por OCR reemplazando artefactos visuales comunes:
     *   U → 0 (OCR frecuentemente confunde el dígito cero con mayúscula U)
     *   Recorta dígitos extra en el segmento medio si hay más de 4.
     */
    private static String normalizarCedula(String raw) {
        // Sustituir artefactos OCR comunes en cédulas estándar CR (X-XXXX-XXXX)
        // No aplica al primer segmento de YREX (YR2024-...) que son letras intencionales
        String clean = raw.replace("U", "0").replace("|", "1").replace("Z", "2");

        // Si el segmento del medio tiene 5+ dígitos por inserción OCR, truncar a 4
        // Ejemplo: "4-03372-0186" → "4-0332-0186"
        Matcher m = Pattern.compile("^(\\d)-(\\d{5,})-(\\d{3,5})$").matcher(clean);
        if (m.matches()) {
            // Truncar el segmento medio a 4 dígitos (tomar los últimos 4)
            String medio = m.group(2);
            medio = medio.substring(medio.length() - 4);
            clean = m.group(1) + "-" + medio + "-" + m.group(3);
        }

        // Truncar último segmento a 4 dígitos si tiene más por inserción OCR
        Matcher m2 = Pattern.compile("^(\\d-\\d{3,4})-(\\d{5,})$").matcher(clean);
        if (m2.matches()) {
            String ultimo = m2.group(2);
            ultimo = ultimo.substring(ultimo.length() - 4);
            clean = m2.group(1) + "-" + ultimo;
        }

        return clean;
    }

    // ─── Salidas ──────────────────────────────────────────────────────────────

    private static void imprimirTabla(List<EstudiantePIAD> lista) {
        String sep = "─".repeat(118);
        System.out.println(sep);
        System.out.printf("%-4s %-16s %-15s %-15s %-22s %-22s %-9s %-6s %-12s %-6s%n",
            "#", "Cédula", "Primer ap.", "Segundo ap.", "Nombre",
            "Tipo adecuación", "Nivel", "Grupo", "Matrícula", "Cód.");
        System.out.println(sep);
        for (EstudiantePIAD e : lista) {
            System.out.printf("%-4d %-16s %-15s %-15s %-22s %-22s %-9s %-6d %-12s %-6s%n",
                e.numero(), e.cedula(),
                truncar(e.primerApellido(), 15),
                truncar(e.segundoApellido(), 15),
                truncar(e.nombre(), 22),
                truncar(e.tipoAdecuacion(), 22),
                truncar(e.nivel(), 9),
                e.grupo(),
                e.fechaMatricula(),
                e.codigoEstado() != null ? e.codigoEstado() : "");
        }
        System.out.println(sep);
    }

    private static void guardarJSON(List<EstudiantePIAD> lista, String path) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        Files.createDirectories(Path.of(path).getParent());
        mapper.writeValue(new File(path), lista);
        System.out.printf("✓ JSON  → %s  (%d registros)%n", path, lista.size());
    }

    private static void guardarCSV(List<EstudiantePIAD> lista, String path) throws IOException {
        Files.createDirectories(Path.of(path).getParent());
        try (PrintWriter pw = new PrintWriter(
                new OutputStreamWriter(new FileOutputStream(path), StandardCharsets.UTF_8))) {

            pw.println("numero,cedula,primer_apellido,segundo_apellido,nombre," +
                       "tipo_adecuacion,nivel,grupo,fecha_matricula,codigo_estado");

            for (EstudiantePIAD e : lista) {
                pw.printf("%d,%s,%s,%s,\"%s\",%s,%s,%d,%s,%s%n",
                    e.numero(),
                    e.cedula(),
                    e.primerApellido(),
                    e.segundoApellido(),
                    e.nombre().replace("\"", "\"\""),
                    e.tipoAdecuacion(),
                    e.nivel(),
                    e.grupo(),
                    e.fechaMatricula(),
                    e.codigoEstado() != null ? e.codigoEstado() : "");
            }
        }
        System.out.printf("✓ CSV   → %s  (%d registros)%n", path, lista.size());
    }

    /**
     * Genera un archivo Excel (.xlsx) formateado.
     *
     * <p>Al integrar en Spring Boot, este método puede devolver {@code byte[]} o
     * escribir directamente en {@code HttpServletResponse} para descarga.</p>
     */
    private static void guardarExcel(List<EstudiantePIAD> lista, String path) throws IOException {
        try (XSSFWorkbook wb = new XSSFWorkbook()) {
            XSSFSheet sheet = wb.createSheet("Lista PIAD");

            // ── Estilos ───────────────────────────────────────────────────────

            XSSFCellStyle headerStyle = wb.createCellStyle();
            headerStyle.setFillForegroundColor(new XSSFColor(new byte[]{30, 37, 53}, null));
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            XSSFFont headerFont = wb.createFont();
            headerFont.setBold(true);
            headerFont.setColor(new XSSFColor(new byte[]{(byte) 255, (byte) 255, (byte) 255}, null));
            headerFont.setFontHeightInPoints((short) 10);
            headerStyle.setFont(headerFont);

            XSSFCellStyle parStyle = wb.createCellStyle();
            parStyle.setFillForegroundColor(new XSSFColor(new byte[]{(byte) 255, (byte) 255, (byte) 255}, null));
            parStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            parStyle.setBorderBottom(BorderStyle.HAIR);
            parStyle.setBorderRight(BorderStyle.HAIR);

            XSSFCellStyle imparStyle = wb.createCellStyle();
            imparStyle.setFillForegroundColor(new XSSFColor(new byte[]{(byte) 244, (byte) 246, (byte) 249}, null));
            imparStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            imparStyle.setBorderBottom(BorderStyle.HAIR);
            imparStyle.setBorderRight(BorderStyle.HAIR);

            XSSFCellStyle numParStyle = wb.createCellStyle();
            numParStyle.cloneStyleFrom(parStyle);
            numParStyle.setAlignment(HorizontalAlignment.RIGHT);

            XSSFCellStyle numImparStyle = wb.createCellStyle();
            numImparStyle.cloneStyleFrom(imparStyle);
            numImparStyle.setAlignment(HorizontalAlignment.RIGHT);

            // ── Encabezado ────────────────────────────────────────────────────

            String[] columnas   = {"#", "Cédula", "Primer Apellido", "Segundo Apellido",
                                   "Nombre", "Tipo Adecuación", "Nivel", "Grupo",
                                   "Fecha Matrícula", "Cód. Estado"};
            int[]    anchos     = {5, 18, 20, 20, 28, 26, 13, 8, 16, 12};

            Row headerRow = sheet.createRow(0);
            headerRow.setHeightInPoints(18);
            for (int i = 0; i < columnas.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columnas[i]);
                cell.setCellStyle(headerStyle);
                sheet.setColumnWidth(i, anchos[i] * 256);
            }
            sheet.createFreezePane(0, 1);

            // ── Filas de datos ────────────────────────────────────────────────

            for (int i = 0; i < lista.size(); i++) {
                EstudiantePIAD e     = lista.get(i);
                boolean        impar = (i % 2 == 1);
                CellStyle      base  = impar ? imparStyle   : parStyle;
                CellStyle      num   = impar ? numImparStyle : numParStyle;

                Row row = sheet.createRow(i + 1);
                row.setHeightInPoints(15);

                crearCelda(row, 0, e.numero(),                                  num);
                crearCelda(row, 1, e.cedula(),                                  base);
                crearCelda(row, 2, e.primerApellido(),                          base);
                crearCelda(row, 3, e.segundoApellido(),                         base);
                crearCelda(row, 4, e.nombre(),                                  base);
                crearCelda(row, 5, e.tipoAdecuacion(),                          base);
                crearCelda(row, 6, e.nivel(),                                   base);
                crearCelda(row, 7, e.grupo(),                                   num);
                crearCelda(row, 8, e.fechaMatricula(),                          base);
                crearCelda(row, 9, e.codigoEstado() != null ? e.codigoEstado() : "", base);
            }

            // ── Escribir archivo ──────────────────────────────────────────────

            Files.createDirectories(Path.of(path).getParent());
            try (FileOutputStream fos = new FileOutputStream(path)) {
                wb.write(fos);
            }
        }
        System.out.printf("✓ Excel → %s  (%d registros)%n", path, lista.size());
    }

    private static void crearCelda(Row row, int col, String valor, CellStyle style) {
        Cell cell = row.createCell(col);
        cell.setCellValue(valor != null ? valor : "");
        cell.setCellStyle(style);
    }

    private static void crearCelda(Row row, int col, int valor, CellStyle style) {
        Cell cell = row.createCell(col);
        cell.setCellValue(valor);
        cell.setCellStyle(style);
    }

    private static String truncar(String s, int max) {
        if (s == null) return "";
        return s.length() > max ? s.substring(0, max - 1) + "…" : s;
    }
}
