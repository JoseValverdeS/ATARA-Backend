package com.atara.deb.ataraapi.controller;

import com.atara.deb.ataraapi.dto.matricula.MatriculaRequestDto;
import com.atara.deb.ataraapi.dto.matricula.MatriculaResponseDto;
import com.atara.deb.ataraapi.model.Matricula;
import com.atara.deb.ataraapi.service.MatriculaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/matriculas")
public class MatriculaController {

    private final MatriculaService matriculaService;

    public MatriculaController(MatriculaService matriculaService) {
        this.matriculaService = matriculaService;
    }

    @PostMapping
    public ResponseEntity<MatriculaResponseDto> matricular(@Valid @RequestBody MatriculaRequestDto request) {
        Matricula creada = matriculaService.matricular(
                request.getEstudianteId(),
                request.getSeccionId(),
                request.getAnioLectivoId());
        return ResponseEntity
                .created(URI.create("/api/matriculas/" + creada.getId()))
                .body(toResponse(creada));
    }

    @GetMapping("/estudiante/{estudianteId}")
    public ResponseEntity<List<MatriculaResponseDto>> listarPorEstudiante(@PathVariable Long estudianteId) {
        List<MatriculaResponseDto> lista = matriculaService.listarPorEstudiante(estudianteId)
                .stream().map(this::toResponse).collect(Collectors.toList());
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/seccion/{seccionId}")
    public ResponseEntity<List<MatriculaResponseDto>> listarPorSeccion(@PathVariable Long seccionId) {
        List<MatriculaResponseDto> lista = matriculaService.listarPorSeccion(seccionId)
                .stream().map(this::toResponse).collect(Collectors.toList());
        return ResponseEntity.ok(lista);
    }

    // --- Mapeo ---

    private MatriculaResponseDto toResponse(Matricula m) {
        MatriculaResponseDto dto = new MatriculaResponseDto();
        dto.setId(m.getId());

        if (m.getEstudiante() != null) {
            dto.setEstudianteId(m.getEstudiante().getId());
            String nombreCompleto = m.getEstudiante().getNombre() + " " + m.getEstudiante().getApellido1();
            if (m.getEstudiante().getApellido2() != null) {
                nombreCompleto += " " + m.getEstudiante().getApellido2();
            }
            dto.setEstudianteNombreCompleto(nombreCompleto);
        }

        if (m.getSeccion() != null) {
            dto.setSeccionId(m.getSeccion().getId());
            dto.setSeccionNombre(m.getSeccion().getNombre());
        }

        if (m.getAnioLectivo() != null) {
            dto.setAnioLectivoId(m.getAnioLectivo().getId());
            dto.setAnioLectivo(m.getAnioLectivo().getAnio());
        }

        dto.setEstado(m.getEstado() != null ? m.getEstado().name() : null);
        dto.setFechaMatricula(m.getFechaMatricula());
        dto.setObservaciones(m.getObservaciones());
        dto.setCreatedAt(m.getCreatedAt());
        return dto;
    }
}
