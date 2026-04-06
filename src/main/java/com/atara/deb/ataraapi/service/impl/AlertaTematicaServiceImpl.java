package com.atara.deb.ataraapi.service.impl;

import com.atara.deb.ataraapi.dto.alertatematica.AlertaTematicaResponseDto;
import com.atara.deb.ataraapi.model.AlertaTematica;
import com.atara.deb.ataraapi.model.DetalleEvaluacionSaber;
import com.atara.deb.ataraapi.model.EjeTematico;
import com.atara.deb.ataraapi.model.Estudiante;
import com.atara.deb.ataraapi.model.Materia;
import com.atara.deb.ataraapi.model.Periodo;
import com.atara.deb.ataraapi.model.enums.EstadoAlerta;
import com.atara.deb.ataraapi.model.enums.NivelAlertaTematica;
import com.atara.deb.ataraapi.repository.AlertaTematicaRepository;
import com.atara.deb.ataraapi.repository.DetalleEvaluacionSaberRepository;
import com.atara.deb.ataraapi.repository.EstudianteRepository;
import com.atara.deb.ataraapi.repository.MatriculaRepository;
import com.atara.deb.ataraapi.repository.PeriodoRepository;
import com.atara.deb.ataraapi.service.AlertaTematicaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class AlertaTematicaServiceImpl implements AlertaTematicaService {

    private final AlertaTematicaRepository alertaRepository;
    private final DetalleEvaluacionSaberRepository detalleRepository;
    private final EstudianteRepository estudianteRepository;
    private final PeriodoRepository periodoRepository;
    private final MatriculaRepository matriculaRepository;

    public AlertaTematicaServiceImpl(
            AlertaTematicaRepository alertaRepository,
            DetalleEvaluacionSaberRepository detalleRepository,
            EstudianteRepository estudianteRepository,
            PeriodoRepository periodoRepository,
            MatriculaRepository matriculaRepository) {
        this.alertaRepository = alertaRepository;
        this.detalleRepository = detalleRepository;
        this.estudianteRepository = estudianteRepository;
        this.periodoRepository = periodoRepository;
        this.matriculaRepository = matriculaRepository;
    }

    @Override
    @Transactional
    public List<AlertaTematicaResponseDto> generarAlertasPorEstudiante(Long estudianteId, Long periodoId) {
        Estudiante estudiante = estudianteRepository.findById(estudianteId)
            .orElseThrow(() -> new NoSuchElementException("Estudiante no encontrado con ID: " + estudianteId));
        Periodo periodo = periodoRepository.findById(periodoId)
            .orElseThrow(() -> new NoSuchElementException("Periodo no encontrado con ID: " + periodoId));

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
        periodoRepository.findById(periodoId)
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
            .stream()
            .map(this::toDto)
            .toList();
    }

    @Override
    public List<AlertaTematicaResponseDto> obtenerAlertasPorSeccion(Long seccionId, Long periodoId) {
        List<Long> estudianteIds = matriculaRepository.findBySeccionId(seccionId).stream()
            .map(m -> m.getEstudiante().getId())
            .toList();
        if (estudianteIds.isEmpty()) {
            return List.of();
        }

        return alertaRepository.findByEstudianteIdInAndPeriodoId(estudianteIds, periodoId)
            .stream()
            .map(this::toDto)
            .toList();
    }

    private List<AlertaTematica> calcularAlertas(
            Estudiante estudiante, Periodo periodo, List<DetalleEvaluacionSaber> detalles) {

        Map<String, List<DetalleEvaluacionSaber>> porMateriaYEje = detalles.stream()
            .collect(Collectors.groupingBy(
                d -> claveMateriaYEje(d.getEvaluacionSaber().getMateria().getId(), d.getEjeTematico().getId()),
                LinkedHashMap::new,
                Collectors.toList()));

        List<AlertaTematica> alertas = new ArrayList<>();

        for (List<DetalleEvaluacionSaber> detallesPorEje : porMateriaYEje.values()) {
            DetalleEvaluacionSaber primerDetalle = detallesPorEje.get(0);
            EjeTematico eje = primerDetalle.getEjeTematico();
            Materia materia = primerDetalle.getEvaluacionSaber().getMateria();

            limpiarAlertasExistentes(estudiante.getId(), periodo.getId(), eje.getId(), materia.getId());

            BigDecimal suma = BigDecimal.ZERO;
            for (DetalleEvaluacionSaber detalle : detallesPorEje) {
                suma = suma.add(BigDecimal.valueOf(detalle.getValor()));
            }

            BigDecimal promedio = suma.divide(
                BigDecimal.valueOf(detallesPorEje.size()),
                2,
                RoundingMode.HALF_UP
            );

            NivelAlertaTematica nivel = determinarNivel(promedio);
            if (nivel == NivelAlertaTematica.SIN_ALERTA) {
                continue;
            }

            String nombreEstudiante = estudiante.getNombre() + " " + estudiante.getApellido1();
            String motivo = generarMotivo(nombreEstudiante, materia, eje, promedio, nivel, detallesPorEje.size());

            alertas.add(AlertaTematica.builder()
                .estudiante(estudiante)
                .periodo(periodo)
                .ejeTematico(eje)
                .materia(materia)
                .promedio(promedio)
                .nivelAlerta(nivel)
                .motivo(motivo)
                .estado(EstadoAlerta.ACTIVA)
                .build());
        }

        return alertas;
    }

    private void limpiarAlertasExistentes(Long estudianteId, Long periodoId, Integer ejeId, Integer materiaId) {
        List<AlertaTematica> existentes = alertaRepository
            .findByEstudianteIdAndPeriodoIdAndEjeTematico_IdAndMateriaId(estudianteId, periodoId, ejeId, materiaId);

        if (!existentes.isEmpty()) {
            alertaRepository.deleteAll(existentes);
        }
    }

    private String claveMateriaYEje(Integer materiaId, Integer ejeId) {
        return materiaId + ":" + ejeId;
    }

    private NivelAlertaTematica determinarNivel(BigDecimal promedio) {
        if (promedio.compareTo(new BigDecimal("2.00")) <= 0) {
            return NivelAlertaTematica.ALTA;
        } else if (promedio.compareTo(new BigDecimal("3.00")) <= 0) {
            return NivelAlertaTematica.MEDIA;
        }
        return NivelAlertaTematica.SIN_ALERTA;
    }

    private String generarMotivo(
            String nombreEstudiante,
            Materia materia,
            EjeTematico eje,
            BigDecimal promedio,
            NivelAlertaTematica nivel,
            int totalEvals) {
        String tipoSaber = eje.getTipoSaber().getNombre();
        String nivelTexto = nivel == NivelAlertaTematica.ALTA
            ? "requiere intervención inmediata"
            : "requiere seguimiento activo";

        return String.format(
            "%s obtuvo un promedio de %s/5.00 en %s, eje '%s' (%s) con base en %d evaluación(es) del periodo. %s.",
            nombreEstudiante,
            promedio.toPlainString(),
            materia.getNombre(),
            eje.getNombre(),
            tipoSaber,
            totalEvals,
            nivelTexto.substring(0, 1).toUpperCase() + nivelTexto.substring(1)
        );
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
            .materiaId(alerta.getMateria().getId())
            .materiaNombre(alerta.getMateria().getNombre())
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
