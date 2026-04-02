package com.atara.deb.ataraapi.service.impl;

import com.atara.deb.ataraapi.dto.aniolectivo.AnioLectivoRequestDto;
import com.atara.deb.ataraapi.model.AnioLectivo;
import com.atara.deb.ataraapi.model.Periodo;
import com.atara.deb.ataraapi.repository.*;
import com.atara.deb.ataraapi.service.AnioLectivoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@Transactional
public class AnioLectivoServiceImpl implements AnioLectivoService {

    private final AnioLectivoRepository anioLectivoRepository;
    private final PeriodoRepository periodoRepository;
    private final SeccionRepository seccionRepository;
    private final MatriculaRepository matriculaRepository;
    private final EvaluacionRepository evaluacionRepository;
    private final EvaluacionSaberRepository evaluacionSaberRepository;
    private final AlertaRepository alertaRepository;
    private final AlertaTematicaRepository alertaTematicaRepository;

    private static final String[] NOMBRES_TRIMESTRE = {
        "I Trimestre", "II Trimestre", "III Trimestre"
    };

    public AnioLectivoServiceImpl(AnioLectivoRepository anioLectivoRepository,
                                   PeriodoRepository periodoRepository,
                                   SeccionRepository seccionRepository,
                                   MatriculaRepository matriculaRepository,
                                   EvaluacionRepository evaluacionRepository,
                                   EvaluacionSaberRepository evaluacionSaberRepository,
                                   AlertaRepository alertaRepository,
                                   AlertaTematicaRepository alertaTematicaRepository) {
        this.anioLectivoRepository = anioLectivoRepository;
        this.periodoRepository = periodoRepository;
        this.seccionRepository = seccionRepository;
        this.matriculaRepository = matriculaRepository;
        this.evaluacionRepository = evaluacionRepository;
        this.evaluacionSaberRepository = evaluacionSaberRepository;
        this.alertaRepository = alertaRepository;
        this.alertaTematicaRepository = alertaTematicaRepository;
    }

    @Override
    public AnioLectivo crear(AnioLectivo anioLectivo) {
        if (anioLectivoRepository.existsByAnio(anioLectivo.getAnio())) {
            throw new IllegalArgumentException(
                "Ya existe un año lectivo para el año: " + anioLectivo.getAnio()
            );
        }
        if (anioLectivo.getActivo() == null) {
            anioLectivo.setActivo(false);
        }

        AnioLectivo guardado = anioLectivoRepository.save(anioLectivo);

        // Generar automáticamente los 3 trimestres
        crearTrimestres(guardado);

        return guardado;
    }

    private void crearTrimestres(AnioLectivo anio) {
        for (int i = 0; i < 3; i++) {
            periodoRepository.save(Periodo.builder()
                    .anioLectivo(anio)
                    .nombre(NOMBRES_TRIMESTRE[i])
                    .numeroPeriodo((short) (i + 1))
                    .activo(i == 0)   // el primer trimestre queda activo
                    .build());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<AnioLectivo> listarTodos() {
        return anioLectivoRepository.findAllByOrderByAnioDesc();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<AnioLectivo> obtenerActivo() {
        return anioLectivoRepository.findByActivoTrue();
    }

    @Override
    @Transactional(readOnly = true)
    public AnioLectivo buscarPorId(Long id) {
        return anioLectivoRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("Año lectivo no encontrado con id: " + id));
    }

    @Override
    public AnioLectivo actualizar(Long id, AnioLectivoRequestDto dto) {
        AnioLectivo anio = buscarPorId(id);
        if (!anio.getAnio().equals(dto.getAnio()) && anioLectivoRepository.existsByAnio(dto.getAnio())) {
            throw new IllegalArgumentException("Ya existe un año lectivo para el año: " + dto.getAnio());
        }
        anio.setAnio(dto.getAnio());
        return anioLectivoRepository.save(anio);
    }

    @Override
    public AnioLectivo activar(Long id) {
        AnioLectivo anioLectivo = buscarPorId(id);
        anioLectivoRepository.desactivarTodos();
        anioLectivo.setActivo(true);
        return anioLectivoRepository.save(anioLectivo);
    }

    @Override
    public void eliminar(Long id) {
        AnioLectivo anio = buscarPorId(id);
        if (Boolean.TRUE.equals(anio.getActivo())) {
            throw new IllegalArgumentException(
                "No se puede eliminar el año lectivo activo. " +
                "Activa otro año lectivo antes de eliminar éste.");
        }

        // Eliminar datos de cada periodo (alertas → evaluaciones → periodos)
        List<Periodo> periodos = periodoRepository.findByAnioLectivoId(id);
        for (Periodo p : periodos) {
            alertaTematicaRepository.deleteAllByPeriodoId(p.getId());
            alertaRepository.deleteAllByPeriodoId(p.getId());
            evaluacionSaberRepository.deleteAllByPeriodoId(p.getId()); // cascada a detalles
            evaluacionRepository.deleteAllByPeriodoId(p.getId());      // cascada a detalles
        }
        periodoRepository.deleteAll(periodos);

        // Eliminar matrículas y secciones del año
        matriculaRepository.deleteAllByAnioLectivoId(id);
        seccionRepository.deleteAll(seccionRepository.findByAnioLectivoId(id));

        anioLectivoRepository.deleteById(id);
    }
}
