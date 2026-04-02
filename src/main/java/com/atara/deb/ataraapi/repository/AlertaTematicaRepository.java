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

    List<AlertaTematica> findByEstudianteIdAndPeriodoId(Long estudianteId, Long periodoId);

    List<AlertaTematica> findByPeriodoId(Long periodoId);

    @Query("""
        SELECT a FROM AlertaTematica a
        WHERE a.estudiante.id IN :estudianteIds AND a.periodo.id = :periodoId
        ORDER BY a.nivelAlerta ASC, a.promedio ASC
    """)
    List<AlertaTematica> findByEstudianteIdInAndPeriodoId(List<Long> estudianteIds, Long periodoId);

    List<AlertaTematica> findByEstudianteIdAndPeriodoIdAndEjeTematico_Id(
        Long estudianteId, Long periodoId, Integer ejeTemaaticoId);

    Optional<AlertaTematica> findByEstudianteIdAndPeriodoIdAndEjeTematico_IdAndNivelAlerta(
        Long estudianteId, Long periodoId, Integer ejeTemaaticoId, NivelAlertaTematica nivelAlerta);

    void deleteByEstudianteIdAndPeriodoId(Long estudianteId, Long periodoId);

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM AlertaTematica a WHERE a.estudiante.id = :estudianteId")
    void deleteAllByEstudianteId(@Param("estudianteId") Long estudianteId);

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM AlertaTematica a WHERE a.periodo.id = :periodoId")
    void deleteAllByPeriodoId(@Param("periodoId") Long periodoId);
}
