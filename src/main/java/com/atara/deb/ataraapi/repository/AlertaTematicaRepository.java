package com.atara.deb.ataraapi.repository;

import com.atara.deb.ataraapi.model.AlertaTematica;
import com.atara.deb.ataraapi.model.enums.NivelAlertaTematica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AlertaTematicaRepository extends JpaRepository<AlertaTematica, Long> {

    @Query("""
        SELECT a FROM AlertaTematica a
        JOIN FETCH a.estudiante
        JOIN FETCH a.periodo
        JOIN FETCH a.materia
        JOIN FETCH a.ejeTematico eje
        JOIN FETCH eje.tipoSaber
        WHERE a.estudiante.id = :estudianteId
          AND a.periodo.id = :periodoId
        ORDER BY a.materia.id, eje.tipoSaber.id, eje.orden, a.promedio ASC
    """)
    List<AlertaTematica> findByEstudianteIdAndPeriodoId(Long estudianteId, Long periodoId);

    List<AlertaTematica> findByPeriodoId(Long periodoId);

    @Query("""
        SELECT a FROM AlertaTematica a
        JOIN FETCH a.estudiante
        JOIN FETCH a.periodo
        JOIN FETCH a.materia
        JOIN FETCH a.ejeTematico eje
        JOIN FETCH eje.tipoSaber
        WHERE a.estudiante.id IN :estudianteIds AND a.periodo.id = :periodoId
        ORDER BY a.materia.id, eje.tipoSaber.id, eje.orden, a.nivelAlerta ASC, a.promedio ASC
    """)
    List<AlertaTematica> findByEstudianteIdInAndPeriodoId(List<Long> estudianteIds, Long periodoId);

    List<AlertaTematica> findByEstudianteIdAndPeriodoIdAndEjeTematico_IdAndMateriaId(
        Long estudianteId, Long periodoId, Integer ejeTemaaticoId, Integer materiaId);

    Optional<AlertaTematica> findByEstudianteIdAndPeriodoIdAndEjeTematico_IdAndMateriaIdAndNivelAlerta(
        Long estudianteId, Long periodoId, Integer ejeTemaaticoId, Integer materiaId, NivelAlertaTematica nivelAlerta);

    void deleteByEstudianteIdAndPeriodoId(Long estudianteId, Long periodoId);

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM AlertaTematica a WHERE a.estudiante.id = :estudianteId")
    void deleteAllByEstudianteId(@Param("estudianteId") Long estudianteId);

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM AlertaTematica a WHERE a.periodo.id = :periodoId")
    void deleteAllByPeriodoId(@Param("periodoId") Long periodoId);
}
