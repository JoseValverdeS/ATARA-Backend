package com.atara.deb.ataraapi.controller;

import com.atara.deb.ataraapi.dto.saber.EvaluacionSaberRequestDto;
import com.atara.deb.ataraapi.dto.saber.EvaluacionSaberResponseDto;
import com.atara.deb.ataraapi.dto.saber.ResumenPromediosEstudianteDto;
import com.atara.deb.ataraapi.service.EvaluacionSaberService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/evaluaciones-saber")
public class EvaluacionSaberController {

    private final EvaluacionSaberService evaluacionSaberService;

    public EvaluacionSaberController(EvaluacionSaberService evaluacionSaberService) {
        this.evaluacionSaberService = evaluacionSaberService;
    }

    @PostMapping
    public ResponseEntity<EvaluacionSaberResponseDto> registrar(
            @Valid @RequestBody EvaluacionSaberRequestDto request) {
        EvaluacionSaberResponseDto response = evaluacionSaberService.registrar(request);
        return ResponseEntity
            .created(URI.create("/api/evaluaciones-saber/" + response.getId()))
            .body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EvaluacionSaberResponseDto> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(evaluacionSaberService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EvaluacionSaberResponseDto> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody EvaluacionSaberRequestDto request) {
        return ResponseEntity.ok(evaluacionSaberService.actualizar(id, request));
    }

    @GetMapping("/estudiante/{estudianteId}/periodo/{periodoId}")
    public ResponseEntity<List<EvaluacionSaberResponseDto>> listarPorEstudianteYPeriodo(
            @PathVariable Long estudianteId, @PathVariable Long periodoId) {
        return ResponseEntity.ok(evaluacionSaberService.listarPorEstudianteYPeriodo(estudianteId, periodoId));
    }

    @GetMapping("/seccion/{seccionId}/periodo/{periodoId}")
    public ResponseEntity<List<EvaluacionSaberResponseDto>> listarPorSeccionYPeriodo(
            @PathVariable Long seccionId, @PathVariable Long periodoId) {
        return ResponseEntity.ok(evaluacionSaberService.listarPorSeccionYPeriodo(seccionId, periodoId));
    }

    @GetMapping("/promedios/estudiante/{estudianteId}/periodo/{periodoId}")
    public ResponseEntity<ResumenPromediosEstudianteDto> obtenerPromedios(
            @PathVariable Long estudianteId, @PathVariable Long periodoId) {
        return ResponseEntity.ok(evaluacionSaberService.obtenerPromedios(estudianteId, periodoId));
    }

    @GetMapping("/promedios/seccion/{seccionId}/periodo/{periodoId}")
    public ResponseEntity<List<ResumenPromediosEstudianteDto>> obtenerPromediosSeccion(
            @PathVariable Long seccionId, @PathVariable Long periodoId) {
        return ResponseEntity.ok(evaluacionSaberService.obtenerPromediosSeccion(seccionId, periodoId));
    }
}
