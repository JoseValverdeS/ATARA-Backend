package com.atara.deb.ataraapi.service;

import com.atara.deb.ataraapi.model.Matricula;

import java.util.List;
import java.util.Optional;

public interface MatriculaService {

    /**
     * Matricula un estudiante en una sección para el año lectivo indicado.
     * Valida que no exista matrícula activa previa en el mismo año.
     */
    Matricula matricular(Long estudianteId, Long seccionId, Long anioLectivoId);

    List<Matricula> listarPorEstudiante(Long estudianteId);

    List<Matricula> listarPorSeccion(Long seccionId);

    Optional<Matricula> obtenerMatriculaEnAnio(Long estudianteId, Long anioLectivoId);
}
