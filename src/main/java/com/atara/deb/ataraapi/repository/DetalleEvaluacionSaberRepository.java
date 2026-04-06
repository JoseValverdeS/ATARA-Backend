package com.atara.deb.ataraapi.repository;

import com.atara.deb.ataraapi.model.DetalleEvaluacionSaber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DetalleEvaluacionSaberRepository extends JpaRepository<DetalleEvaluacionSaber, Long> {

    List<DetalleEvaluacionSaber> findByEvaluacionSaberId(Long evaluacionSaberId);

    @Query("""
        SELECT d FROM DetalleEvaluacionSaber d
        JOIN FETCH d.evaluacionSaber es
        JOIN FETCH es.materia
        JOIN FETCH d.ejeTematico eje
        JOIN FETCH eje.materia
        JOIN FETCH eje.tipoSaber
        WHERE es.estudiante.id = :estudianteId
          AND es.periodo.id = :periodoId
          AND es.materia.id = eje.materia.id
        ORDER BY es.materia.id, eje.tipoSaber.id, eje.orden
    """)
    List<DetalleEvaluacionSaber> findByEstudianteAndPeriodo(Long estudianteId, Long periodoId);

    @Query("""
        SELECT d FROM DetalleEvaluacionSaber d
        JOIN FETCH d.evaluacionSaber es
        JOIN FETCH es.materia
        JOIN FETCH d.ejeTematico eje
        JOIN FETCH eje.materia
        JOIN FETCH eje.tipoSaber
        WHERE es.estudiante.id = :estudianteId
          AND es.periodo.id = :periodoId
          AND eje.id = :ejeTematicoId
          AND es.materia.id = eje.materia.id
    """)
    List<DetalleEvaluacionSaber> findByEstudiantePeriodoAndEje(
        Long estudianteId, Long periodoId, Integer ejeTematicoId);
}
