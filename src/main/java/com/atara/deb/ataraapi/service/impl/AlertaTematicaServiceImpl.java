package com.atara.deb.ataraapi.service.impl;

import com.atara.deb.ataraapi.dto.alertatematica.AlertaTematicaResponseDto;
import com.atara.deb.ataraapi.model.*;
import com.atara.deb.ataraapi.model.enums.EstadoAlerta;
import com.atara.deb.ataraapi.model.enums.NivelAlertaTematica;
import com.atara.deb.ataraapi.repository.*;
import com.atara.deb.ataraapi.service.AlertaTematicaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class AlertaTematicaServiceImpl implements AlertaTematicaService {

    private final AlertaTematicaRepository alertaRepository;
    private final DetalleEvaluacionSaberRepository detalleRepository;
    private final EstudianteRepository estudianteRepository;
    private final PeriodoRepository periodoRepository;
    private final MatriculaRepository matriculaRepository;
    private final EvaluacionSaberRepository evaluacionSaberRepository;

    public AlertaTematicaServiceImpl(
            AlertaTematicaRepository alertaRepository,
            DetalleEvaluacionSaberRepository detalleRepository,
            EstudianteRepository estudianteRepository,
            PeriodoRepository periodoRepository,
            MatriculaRepository matriculaRepository,
            EvaluacionSaberRepository evaluacionSaberRepository) {
        this.alertaRepository = alertaRepository;
        this.detalleRepository = detalleRepository;
        this.estudianteRepository = estudianteRepository;
        this.periodoRepository = periodoRepository;
        this.matriculaRepository = matriculaRepository;
        this.evaluacionSaberRepository = evaluacionSaberRepository;
    }

    /**
     * Genera alertas para un estudiante en un periodo.
     *
     * Reglas de negocio:
     * - Promedio por eje 1.0 - 2.0 -> ALTA (intervención inmediata)
     * - Promedio por eje 2.1 - 3.0 -> MEDIA (seguimiento activo)
     * - Promedio por eje 3.1 - 5.0 -> SIN_ALERTA (no se persiste)
     *
     * Elimina alertas anteriores del mismo estudiante/periodo antes de recalcular.
     */
    @Override
    @Transactional
    public List<AlertaTematicaResponseDto> generarAlertasPorEstudiante(Long estudianteId, Long periodoId) {
        Estudiante estudiante = estudianteRepository.findById(estudianteId)
            .orElseThrow(() -> new NoSuchElementException("Estudiante no encontrado con ID: " + estudianteId));
        Periodo periodo = periodoRepository.findById(periodoId)
            .orElseThrow(() -> new NoSuchElementException("Periodo no encontrado con ID: " + periodoId));

        // Eliminar alertas previas para recalcular
        alertaRepository.deleteByEstudianteIdAndPeriodoId(estudianteId, periodoId);

        List<DetalleEvaluacionSaber> detalles = detalleRepository.findByEstudianteAndPeriodo(estudianteId, periodoId);
        if (detalles.isEmpty()) {
            return List.of();
        }

        List<AlertaTematica> alertasGeneradas = calcularAlertas(estudiante, periodo, detalles);
        List<AlertaTematica> saved = alertaRepository.saveAll(alertasGeneradas);
        return saved.stream().map(this::toDto).toList();
    }

    @Override
    @Transactional
    public List<AlertaTematicaResponseDto> generarAlertasPorSeccion(Long seccionId, Long periodoId) {
        Periodo periodo = periodoRepository.findById(periodoId)
            .orElseThrow(() -> new NoSuchElementException("Periodo no encontrado con ID: " + periodoId));

        List<Long> estudianteIds = matriculaRepository.findBySeccionId(seccionId).stream()
            .map(m -> m.getEstudiante().getId())
            .toList();

        List<AlertaTematicaResponseDto> todasAlertas = new ArrayList<>();
        for (Long estId : estudianteIds) {
            todasAlertas.addAll(generarAlertasPorEstudiante(estId, periodoId));
        }
        return todasAlertas;
    }

    @Override
    public List<AlertaTematicaResponseDto> obtenerAlertasPorEstudiante(Long estudianteId, Long periodoId) {
        return alertaRepository.findByEstudianteIdAndPeriodoId(estudianteId, periodoId)
            .stream().map(this::toDto).toList();
    }

    @Override
    public List<AlertaTematicaResponseDto> obtenerAlertasPorSeccion(Long seccionId, Long periodoId) {
        List<Long> estudianteIds = matriculaRepository.findBySeccionId(seccionId).stream()
            .map(m -> m.getEstudiante().getId())
            .toList();
        if (estudianteIds.isEmpty()) return List.of();

        return alertaRepository.findByEstudianteIdInAndPeriodoId(estudianteIds, periodoId)
            .stream().map(this::toDto).toList();
    }

    private List<AlertaTematica> calcularAlertas(
            Estudiante estudiante, Periodo periodo, List<DetalleEvaluacionSaber> detalles) {

        // Agrupar por eje temático y calcular promedio
        Map<Integer, List<DetalleEvaluacionSaber>> porEje = detalles.stream()
            .collect(Collectors.groupingBy(d -> d.getEjeTematico().getId()));

        List<AlertaTematica> alertas = new ArrayList<>();

        for (Map.Entry<Integer, List<DetalleEvaluacionSaber>> entry : porEje.entrySet()) {
            List<DetalleEvaluacionSaber> detsEje = entry.getValue();
            EjeTematico eje = detsEje.get(0).getEjeTematico();

            BigDecimal sum = BigDecimal.ZERO;
            for (DetalleEvaluacionSaber d : detsEje) {
                sum = sum.add(BigDecimal.valueOf(d.getValor()));
            }
            BigDecimal promedio = sum.divide(BigDecimal.valueOf(detsEje.size()), 2, RoundingMode.HALF_UP);

            NivelAlertaTematica nivel = determinarNivel(promedio);
            if (nivel == NivelAlertaTematica.SIN_ALERTA) {
                continue;
            }

            String nombreEstudiante = estudiante.getNombre() + " " + estudiante.getApellido1();
            String motivo = generarMotivo(nombreEstudiante, eje, promedio, nivel, detsEje.size());

            AlertaTematica alerta = AlertaTematica.builder()
                .estudiante(estudiante)
                .periodo(periodo)
                .ejeTematico(eje)
                .promedio(promedio)
                .nivelAlerta(nivel)
                .motivo(motivo)
                .estado(EstadoAlerta.ACTIVA)
                .build();

            alertas.add(alerta);
        }

        return alertas;
    }

    private NivelAlertaTematica determinarNivel(BigDecimal promedio) {
        if (promedio.compareTo(new BigDecimal("2.00")) <= 0) {
            return NivelAlertaTematica.ALTA;
        } else if (promedio.compareTo(new BigDecimal("3.00")) <= 0) {
            return NivelAlertaTematica.MEDIA;
        }
        return NivelAlertaTematica.SIN_ALERTA;
    }

    private String generarMotivo(String nombreEstudiante, EjeTematico eje,
                                  BigDecimal promedio, NivelAlertaTematica nivel, int totalEvals) {
        String tipoSaber = eje.getTipoSaber().getNombre();
        String nivelTexto = nivel == NivelAlertaTematica.ALTA
            ? "requiere intervención inmediata"
            : "requiere seguimiento activo";

        return String.format(
            "%s obtuvo un promedio de %s/5.00 en el eje '%s' (%s) "
            + "con base en %d evaluación(es) del periodo. %s.",
            nombreEstudiante, promedio.toPlainString(), eje.getNombre(),
            tipoSaber, totalEvals,
            nivelTexto.substring(0, 1).toUpperCase() + nivelTexto.substring(1));
    }

    private AlertaTematicaResponseDto toDto(AlertaTematica alerta) {
        String nombreCompleto = alerta.getEstudiante().getNombre() + " " + alerta.getEstudiante().getApellido1()
            + (alerta.getEstudiante().getApellido2() != null ? " " + alerta.getEstudiante().getApellido2() : "");

        return AlertaTematicaResponseDto.builder()
            .id(alerta.getId())
            .estudianteId(alerta.getEstudiante().getId())
            .estudianteNombreCompleto(nombreCompleto)
            .periodoId(alerta.getPeriodo().getId())
            .periodoNombre(alerta.getPeriodo().getNombre())
            .ejeTemaaticoId(alerta.getEjeTematico().getId())
            .ejeNombre(alerta.getEjeTematico().getNombre())
            .ejeClave(alerta.getEjeTematico().getClave())
            .tipoSaberId(alerta.getEjeTematico().getTipoSaber().getId())
            .tipoSaberNombre(alerta.getEjeTematico().getTipoSaber().getNombre())
            .promedio(alerta.getPromedio())
            .nivelAlerta(alerta.getNivelAlerta().name())
            .motivo(alerta.getMotivo())
            .estado(alerta.getEstado().name())
            .fechaGeneracion(alerta.getFechaGeneracion() != null
                ? alerta.getFechaGeneracion().toLocalDateTime() : null)
            .build();
    }
}
