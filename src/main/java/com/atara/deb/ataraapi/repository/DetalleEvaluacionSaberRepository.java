package com.atara.deb.ataraapi.repository;

import com.atara.deb.ataraapi.model.DetalleEvaluacionSaber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DetalleEvaluacionSaberRepository extends JpaRepository<DetalleEvaluacionSaber, Long> {

    List<DetalleEvaluacionSaber> findByEvaluacionSaberId(Long evaluacionSaberId);

    @Query("""
        SELECT d FROM DetalleEvaluacionSaber d
        JOIN d.evaluacionSaber es
        WHERE es.estudiante.id = :estudianteId
          AND es.periodo.id = :periodoId
        ORDER BY d.ejeTematico.id
    """)
    List<DetalleEvaluacionSaber> findByEstudianteAndPeriodo(Long estudianteId, Long periodoId);

    @Query("""
        SELECT d FROM DetalleEvaluacionSaber d
        JOIN d.evaluacionSaber es
        WHERE es.estudiante.id = :estudianteId
          AND es.periodo.id = :periodoId
          AND d.ejeTematico.id = :ejeTematicoId
    """)
    List<DetalleEvaluacionSaber> findByEstudiantePeriodoAndEje(
        Long estudianteId, Long periodoId, Integer ejeTematicoId);
}
