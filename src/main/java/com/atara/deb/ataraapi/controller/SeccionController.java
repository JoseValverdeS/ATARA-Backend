package com.atara.deb.ataraapi.controller;

import com.atara.deb.ataraapi.dto.catalogo.CentroEducativoResponseDto;
import com.atara.deb.ataraapi.dto.catalogo.NivelResponseDto;
import com.atara.deb.ataraapi.dto.seccion.SeccionRequestDto;
import com.atara.deb.ataraapi.dto.seccion.SeccionResponseDto;
import com.atara.deb.ataraapi.dto.usuario.UsuarioDocenteResponseDto;
import com.atara.deb.ataraapi.service.SeccionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

import java.util.List;

@RestController
@RequestMapping("/api/secciones")
public class SeccionController {

    private final SeccionService seccionService;

    public SeccionController(SeccionService seccionService) {
        this.seccionService = seccionService;
    }

    /** GET /api/secciones?anioLectivoId=1 — todas las secciones de un año lectivo. */
    @GetMapping
    public ResponseEntity<List<SeccionResponseDto>> listar(
            @RequestParam Long anioLectivoId) {
        return ResponseEntity.ok(seccionService.listarPorAnioLectivo(anioLectivoId));
    }

    /** GET /api/secciones/{id} — obtiene una sección por id. */
    @GetMapping("/{id}")
    public ResponseEntity<SeccionResponseDto> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(seccionService.buscarPorId(id));
    }

    /** GET /api/secciones/docente/{docenteId} — secciones asignadas a un docente. */
    @GetMapping("/docente/{docenteId}")
    public ResponseEntity<List<SeccionResponseDto>> listarPorDocente(
            @PathVariable Long docenteId) {
        return ResponseEntity.ok(seccionService.listarPorDocente(docenteId));
    }

    /** POST /api/secciones — crear una nueva sección. */
    @PostMapping
    public ResponseEntity<SeccionResponseDto> crear(@Valid @RequestBody SeccionRequestDto dto) {
        SeccionResponseDto creada = seccionService.crearSeccion(dto);
        return ResponseEntity
                .created(URI.create("/api/secciones/" + creada.getId()))
                .body(creada);
    }

    /** GET /api/secciones/catalogos/niveles — niveles educativos disponibles. */
    @GetMapping("/catalogos/niveles")
    public ResponseEntity<List<NivelResponseDto>> listarNiveles() {
        return ResponseEntity.ok(seccionService.listarNiveles());
    }

    /** GET /api/secciones/catalogos/centros — centros educativos disponibles. */
    @GetMapping("/catalogos/centros")
    public ResponseEntity<List<CentroEducativoResponseDto>> listarCentros() {
        return ResponseEntity.ok(seccionService.listarCentros());
    }

    /** GET /api/secciones/catalogos/docentes — docentes activos disponibles. */
    @GetMapping("/catalogos/docentes")
    public ResponseEntity<List<UsuarioDocenteResponseDto>> listarDocentes() {
        return ResponseEntity.ok(seccionService.listarDocentes());
    }

    /** PUT /api/secciones/{id} — actualiza los datos de una sección. */
    @PutMapping("/{id}")
    public ResponseEntity<SeccionResponseDto> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody SeccionRequestDto dto) {
        return ResponseEntity.ok(seccionService.actualizarSeccion(id, dto));
    }

    /** DELETE /api/secciones/{id} — elimina la sección y sus matrículas y evaluaciones. Solo ADMIN. */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        seccionService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
