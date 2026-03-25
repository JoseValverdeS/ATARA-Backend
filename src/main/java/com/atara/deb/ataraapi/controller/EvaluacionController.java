package com.atara.deb.ataraapi.controller;

import com.atara.deb.ataraapi.dto.evaluacion.DetalleEvaluacionRequestDto;
import com.atara.deb.ataraapi.dto.evaluacion.EvaluacionRequestDto;
import com.atara.deb.ataraapi.dto.evaluacion.EvaluacionResponseDto;
import com.atara.deb.ataraapi.model.*;
import com.atara.deb.ataraapi.service.EvaluacionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/evaluaciones")
public class EvaluacionController {

    private final EvaluacionService evaluacionService;

    public EvaluacionController(EvaluacionService evaluacionService) {
        this.evaluacionService = evaluacionService;
    }

    @PostMapping
    public ResponseEntity<EvaluacionResponseDto> registrar(@Valid @RequestBody EvaluacionRequestDto request) {
        Evaluacion evaluacion = toEntity(request);
        Evaluacion creada = evaluacionService.registrar(evaluacion);
        return ResponseEntity
                .created(URI.create("/api/evaluaciones/" + creada.getId()))
                .body(toResponse(creada));
    }

    @PostMapping("/{id}/detalles")
    public ResponseEntity<EvaluacionResponseDto> agregarDetalle(
            @PathVariable Long id,
            @Valid @RequestBody DetalleEvaluacionRequestDto request) {
        DetalleEvaluacion detalle = toDetalleEntity(request);
        evaluacionService.agregarDetalle(id, detalle);
        return ResponseEntity.ok(toResponse(evaluacionService.buscarPorId(id)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EvaluacionResponseDto> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(toResponse(evaluacionService.buscarPorId(id)));
    }

    @GetMapping("/estudiante/{estudianteId}")
    public ResponseEntity<List<EvaluacionResponseDto>> listarPorEstudiante(@PathVariable Long estudianteId) {
        List<EvaluacionResponseDto> lista = evaluacionService.listarPorEstudiante(estudianteId)
                .stream().map(this::toResponse).collect(Collectors.toList());
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/estudiante/{estudianteId}/periodo/{periodoId}")
    public ResponseEntity<List<EvaluacionResponseDto>> listarPorEstudianteYPeriodo(
            @PathVariable Long estudianteId,
            @PathVariable Long periodoId) {
        List<EvaluacionResponseDto> lista = evaluacionService.listarPorEstudianteYPeriodo(estudianteId, periodoId)
                .stream().map(this::toResponse).collect(Collectors.toList());
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/periodo/{periodoId}")
    public ResponseEntity<List<EvaluacionResponseDto>> listarPorPeriodo(@PathVariable Long periodoId) {
        List<EvaluacionResponseDto> lista = evaluacionService.listarPorPeriodo(periodoId)
                .stream().map(this::toResponse).collect(Collectors.toList());
        return ResponseEntity.ok(lista);
    }

    // --- Mapeo ---

    private Evaluacion toEntity(EvaluacionRequestDto dto) {
        Evaluacion e = new Evaluacion();

        Estudiante estudiante = new Estudiante();
        estudiante.setId(dto.getEstudianteId());
        e.setEstudiante(estudiante);

        Periodo periodo = new Periodo();
        periodo.setId(dto.getPeriodoId());
        e.setPeriodo(periodo);

        Usuario usuario = new Usuario();
        usuario.setId(dto.getUsuarioId());
        e.setUsuario(usuario);

        Seccion seccion = new Seccion();
        seccion.setId(dto.getSeccionId());
        e.setSeccion(seccion);

        e.setObservacionGeneral(dto.getObservacionGeneral());
        e.setOrigenRegistro(dto.getOrigenRegistro());
        return e;
    }

    private DetalleEvaluacion toDetalleEntity(DetalleEvaluacionRequestDto dto) {
        DetalleEvaluacion d = new DetalleEvaluacion();

        CriterioIndicador criterio = new CriterioIndicador();
        criterio.setId(dto.getCriterioId());
        d.setCriterio(criterio);

        EscalaValoracion escala = new EscalaValoracion();
        escala.setId(dto.getEscalaId());
        d.setEscala(escala);

        d.setObservacion(dto.getObservacion());
        return d;
    }

    private EvaluacionResponseDto toResponse(Evaluacion e) {
        EvaluacionResponseDto dto = new EvaluacionResponseDto();
        dto.setId(e.getId());

        if (e.getEstudiante() != null) {
            dto.setEstudianteId(e.getEstudiante().getId());
            String nombre = e.getEstudiante().getNombre() + " " + e.getEstudiante().getApellido1();
            if (e.getEstudiante().getApellido2() != null) {
                nombre += " " + e.getEstudiante().getApellido2();
            }
            dto.setEstudianteNombreCompleto(nombre);
        }

        if (e.getPeriodo() != null) {
            dto.setPeriodoId(e.getPeriodo().getId());
            dto.setPeriodoNombre(e.getPeriodo().getNombre());
        }

        if (e.getUsuario() != null) {
            dto.setUsuarioId(e.getUsuario().getId());
        }

        if (e.getSeccion() != null) {
            dto.setSeccionId(e.getSeccion().getId());
            dto.setSeccionNombre(e.getSeccion().getNombre());
        }

        dto.setObservacionGeneral(e.getObservacionGeneral());
        dto.setOrigenRegistro(e.getOrigenRegistro() != null ? e.getOrigenRegistro().name() : null);
        dto.setCreatedAt(e.getCreatedAt());
        return dto;
    }
}
