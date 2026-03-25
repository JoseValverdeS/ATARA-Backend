package com.atara.deb.ataraapi.repository;

import com.atara.deb.ataraapi.model.Alerta;
import com.atara.deb.ataraapi.model.enums.EstadoAlerta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AlertaRepository extends JpaRepository<Alerta, Long> {

    @Query("""
        SELECT a FROM Alerta a
        JOIN FETCH a.estudiante
        JOIN FETCH a.contenido
        WHERE a.estudiante.id = :estudianteId AND a.periodo.id = :periodoId
        """)
    List<Alerta> findByEstudianteIdAndPeriodoId(
        @Param("estudianteId") Long estudianteId,
        @Param("periodoId") Long periodoId
    );

    List<Alerta> findByPeriodoId(Long periodoId);

    List<Alerta> findByEstudianteIdAndEstado(Long estudianteId, EstadoAlerta estado);

    @Query("""
        SELECT a FROM Alerta a
        JOIN FETCH a.estudiante
        JOIN FETCH a.contenido
        WHERE a.estudiante.id IN :ids AND a.periodo.id = :periodoId
        """)
    List<Alerta> findByEstudianteIdInAndPeriodoId(
        @Param("ids") List<Long> estudianteIds,
        @Param("periodoId") Long periodoId
    );
}