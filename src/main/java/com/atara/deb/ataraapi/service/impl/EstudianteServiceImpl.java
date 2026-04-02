package com.atara.deb.ataraapi.service.impl;

import com.atara.deb.ataraapi.model.Estudiante;
import com.atara.deb.ataraapi.model.enums.EstadoEstudiante;
import com.atara.deb.ataraapi.repository.*;
import com.atara.deb.ataraapi.service.EstudianteService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@Transactional
public class EstudianteServiceImpl implements EstudianteService {

    private final EstudianteRepository estudianteRepository;
    private final MatriculaRepository matriculaRepository;
    private final EvaluacionRepository evaluacionRepository;
    private final EvaluacionSaberRepository evaluacionSaberRepository;
    private final AlertaRepository alertaRepository;
    private final AlertaTematicaRepository alertaTematicaRepository;

    public EstudianteServiceImpl(EstudianteRepository estudianteRepository,
                                 MatriculaRepository matriculaRepository,
                                 EvaluacionRepository evaluacionRepository,
                                 EvaluacionSaberRepository evaluacionSaberRepository,
                                 AlertaRepository alertaRepository,
                                 AlertaTematicaRepository alertaTematicaRepository) {
        this.estudianteRepository = estudianteRepository;
        this.matriculaRepository = matriculaRepository;
        this.evaluacionRepository = evaluacionRepository;
        this.evaluacionSaberRepository = evaluacionSaberRepository;
        this.alertaRepository = alertaRepository;
        this.alertaTematicaRepository = alertaTematicaRepository;
    }

    @Override
    public Estudiante registrar(Estudiante estudiante) {
        if (estudianteRepository.existsByIdentificacion(estudiante.getIdentificacion())) {
            throw new IllegalArgumentException(
                "Ya existe un estudiante con identificación: " + estudiante.getIdentificacion()
            );
        }
        if (estudiante.getEstado() == null) {
            estudiante.setEstado(EstadoEstudiante.ACTIVO);
        }
        return estudianteRepository.save(estudiante);
    }

    @Override
    @Transactional(readOnly = true)
    public Estudiante buscarPorId(Long id) {
        return estudianteRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("Estudiante no encontrado con id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Estudiante> buscarPorIdentificacion(String identificacion) {
        return estudianteRepository.findByIdentificacion(identificacion);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Estudiante> listarTodos() {
        return estudianteRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Estudiante> listarPorEstado(EstadoEstudiante estado) {
        return estudianteRepository.findByEstado(estado);
    }

    @Override
    public Estudiante actualizar(Long id, Estudiante datosActualizados) {
        Estudiante existente = buscarPorId(id);

        // Verificar unicidad de identificación si cambió
        if (!existente.getIdentificacion().equals(datosActualizados.getIdentificacion())
                && estudianteRepository.existsByIdentificacion(datosActualizados.getIdentificacion())) {
            throw new IllegalArgumentException(
                "Ya existe un estudiante con identificación: " + datosActualizados.getIdentificacion()
            );
        }

        existente.setIdentificacion(datosActualizados.getIdentificacion());
        existente.setNombre(datosActualizados.getNombre());
        existente.setApellido1(datosActualizados.getApellido1());
        existente.setApellido2(datosActualizados.getApellido2());
        existente.setFechaNacimiento(datosActualizados.getFechaNacimiento());
        existente.setGenero(datosActualizados.getGenero());
        existente.setNombreAcudiente(datosActualizados.getNombreAcudiente());
        existente.setTelefonoAcudiente(datosActualizados.getTelefonoAcudiente());
        existente.setCorreoAcudiente(datosActualizados.getCorreoAcudiente());
        existente.setEstado(datosActualizados.getEstado());

        return estudianteRepository.save(existente);
    }

    @Override
    public void eliminar(Long id) {
        buscarPorId(id); // lanza NoSuchElementException si no existe
        // Eliminar en orden correcto respetando FK RESTRICT del esquema:
        // alertas temáticas → alertas → evaluaciones saber → evaluaciones → matrículas → estudiante
        // Las tablas detalle_evaluacion y detalle_evaluacion_saber tienen ON DELETE CASCADE.
        alertaTematicaRepository.deleteAllByEstudianteId(id);
        alertaRepository.deleteAllByEstudianteId(id);
        evaluacionSaberRepository.deleteAllByEstudianteId(id);  // cascada a detalle_evaluacion_saber
        evaluacionRepository.deleteAllByEstudianteId(id);       // cascada a detalle_evaluacion
        matriculaRepository.deleteAllByEstudianteId(id);
        estudianteRepository.deleteById(id);
    }
}
