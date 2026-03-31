@echo off
REM ============================================================
REM  Extractor PIAD — ATARA Backend
REM  Ejecutar desde la raiz del proyecto o desde esta carpeta.
REM ============================================================

echo.
echo  Extractor PIAD - ATARA
echo  ======================

REM Detectar desde donde se llama el script
SET SCRIPT_DIR=%~dp0

REM Si existe "Lista PIAD.pdf" en el directorio actual, usarlo
IF EXIST "Lista PIAD.pdf" (
    SET PDF_PATH=Lista PIAD.pdf
) ELSE (
    SET PDF_PATH=%SCRIPT_DIR%..\..\Lista PIAD.pdf
)

echo  PDF: %PDF_PATH%
echo.

cd /D "%SCRIPT_DIR%"
call mvn compile exec:java -Dexec.args="%PDF_PATH%" -q

echo.
echo  Listo. Revisa la carpeta output\ en la raiz del proyecto.
echo.
pause
