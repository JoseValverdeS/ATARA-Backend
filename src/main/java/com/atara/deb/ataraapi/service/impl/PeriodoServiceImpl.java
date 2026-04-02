package com.atara.deb.ataraapi.service.impl;

import com.atara.deb.ataraapi.dto.periodo.PeriodoRequestDto;
import com.atara.deb.ataraapi.dto.periodo.PeriodoResponseDto;
import com.atara.deb.ataraapi.model.AnioLectivo;
import com.atara.deb.ataraapi.model.Periodo;
import com.atara.deb.ataraapi.repository.*;
import com.atara.deb.ataraapi.service.PeriodoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class PeriodoServiceImpl implements PeriodoService {

    private final PeriodoRepository periodoRepository;
    private final AnioLectivoRepository anioLectivoRepository;
    private final EvaluacionRepository evaluacionRepository;
    private final EvaluacionSaberRepository evaluacionSaberRepository;
    private final AlertaRepository alertaRepository;
    private final AlertaTematicaRepository alertaTematicaRepository;

    public PeriodoServiceImpl(PeriodoRepository periodoRepository,
                              AnioLectivoRepository anioLectivoRepository,
                              EvaluacionRepository evaluacionRepository,
                              EvaluacionSaberRepository evaluacionSaberRepository,
                              AlertaRepository alertaRepository,
                              AlertaTematicaRepository alertaTematicaRepository) {
        this.periodoRepository = periodoRepository;
        this.anioLectivoRepository = anioLectivoRepository;
        this.evaluacionRepository = evaluacionRepository;
        this.evaluacionSaberRepository = evaluacionSaberRepository;
        this.alertaRepository = alertaRepository;
        this.alertaTematicaRepository = alertaTematicaRepository;
    }

    @Override
    public List<PeriodoResponseDto> listarPorAnioLectivo(Long anioLectivoId) {
        return periodoRepository.findByAnioLectivoId(anioLectivoId)
                .stream()
                .sorted((a, b) -> a.getNumeroPeriodo().compareTo(b.getNumeroPeriodo()))
                .map(this::toDto)
                .toList();
    }

    @Override
    public Optional<PeriodoResponseDto> obtenerActivo(Long anioLectivoId) {
        return periodoRepository.findByAnioLectivoIdAndActivoTrue(anioLectivoId)
                .map(this::toDto);
    }

    @Override
    @Transactional
    public PeriodoResponseDto activar(Long periodoId) {
        Periodo periodo = periodoRepository.findById(periodoId)
                .orElseThrow(() -> new NoSuchElementException("Periodo no encontrado: " + periodoId));
        periodoRepository.desactivarPeriodosDeAnio(periodo.getAnioLectivo().getId());
        periodo.setActivo(true);
        return toDto(periodoRepository.save(periodo));
    }

    @Override
    @Transactional
    public PeriodoResponseDto crear(PeriodoRequestDto dto) {
        if (dto.getAnioLectivoId() == null) {
            throw new IllegalArgumentException("anioLectivoId es obligatorio para crear un periodo.");
        }
        AnioLectivo anio = anioLectivoRepository.findById(dto.getAnioLectivoId())
                .orElseThrow(() -> new NoSuchElementException("Año lectivo no encontrado: " + dto.getAnioLectivoId()));
        if (dto.getFechaInicio().isAfter(dto.getFechaFin())) {
            throw new IllegalArgumentException("La fecha de inicio debe ser anterior a la fecha de fin.");
        }

        // numeroPeriodo = máx existente + 1
        List<Periodo> existentes = periodoRepository.findByAnioLectivoId(dto.getAnioLectivoId());
        short numero = (short) (existentes.stream()
                .mapToInt(p -> p.getNumeroPeriodo())
                .max().orElse(0) + 1);

        Periodo periodo = Periodo.builder()
                .anioLectivo(anio)
                .nombre(dto.getNombre())
                .numeroPeriodo(numero)
                .fechaInicio(dto.getFechaInicio())
                .fechaFin(dto.getFechaFin())
                .activo(false)
                .build();

        return toDto(periodoRepository.save(periodo));
    }

    @Override
    @Transactional
    public PeriodoResponseDto actualizar(Long id, PeriodoRequestDto dto) {
        Periodo periodo = periodoRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Periodo no encontrado: " + id));
        if (dto.getFechaInicio().isAfter(dto.getFechaFin())) {
            throw new IllegalArgumentException("La fecha de inicio debe ser anterior a la fecha de fin.");
        }
        periodo.setNombre(dto.getNombre());
        periodo.setFechaInicio(dto.getFechaInicio());
        periodo.setFechaFin(dto.getFechaFin());
        return toDto(periodoRepository.save(periodo));
    }

    @Override
    @Transactional
    public void eliminar(Long periodoId) {
        Periodo periodo = periodoRepository.findById(periodoId)
                .orElseThrow(() -> new NoSuchElementException("Periodo no encontrado: " + periodoId));
        if (Boolean.TRUE.equals(periodo.getActivo())) {
            throw new IllegalArgumentException(
                "No se puede eliminar el periodo activo. " +
                "Activa otro periodo antes de eliminar éste.");
        }
        // Orden: alertas temáticas → alertas → evaluaciones saber → evaluaciones → periodo
        alertaTematicaRepository.deleteAllByPeriodoId(periodoId);
        alertaRepository.deleteAllByPeriodoId(periodoId);
        evaluacionSaberRepository.deleteAllByPeriodoId(periodoId); // cascada a detalles
        evaluacionRepository.deleteAllByPeriodoId(periodoId);      // cascada a detalles
        periodoRepository.deleteById(periodoId);
    }

    private PeriodoResponseDto toDto(Periodo p) {
        return PeriodoResponseDto.builder()
                .id(p.getId())
                .anioLectivoId(p.getAnioLectivo().getId())
                .anioLectivoAnio(p.getAnioLectivo().getAnio())
                .nombre(p.getNombre())
                .numeroPeriodo(p.getNumeroPeriodo())
                .fechaInicio(p.getFechaInicio())
                .fechaFin(p.getFechaFin())
                .activo(p.getActivo())
                .build();
    }
}
