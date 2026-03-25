package com.atara.deb.ataraapi.service;

import com.atara.deb.ataraapi.model.DetalleEvaluacion;
import com.atara.deb.ataraapi.model.Evaluacion;

import java.util.List;

public interface EvaluacionService {

    /**
     * Registra una evaluación con sus detalles.
     * Valida unicidad por (estudiante, usuario, periodo).
     */
    Evaluacion registrar(Evaluacion evaluacion);

    /**
     * Agrega o actualiza un detalle (criterio + escala) en una evaluación existente.
     */
    DetalleEvaluacion agregarDetalle(Long evaluacionId, DetalleEvaluacion detalle);

    Evaluacion buscarPorId(Long id);

    List<Evaluacion> listarPorEstudiante(Long estudianteId);

    List<Evaluacion> listarPorEstudianteYPeriodo(Long estudianteId, Long periodoId);

    List<Evaluacion> listarPorPeriodo(Long periodoId);
}
