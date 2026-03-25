package com.atara.deb.ataraapi.repository;

import com.atara.deb.ataraapi.model.Matricula;
import com.atara.deb.ataraapi.model.enums.EstadoMatricula;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MatriculaRepository extends JpaRepository<Matricula, Long> {

    List<Matricula> findByEstudianteId(Long estudianteId);

    @Query("SELECT m FROM Matricula m JOIN FETCH m.estudiante WHERE m.seccion.id = :seccionId")
    List<Matricula> findBySeccionId(@Param("seccionId") Long seccionId);

    List<Matricula> findByAnioLectivoId(Long anioLectivoId);

    Optional<Matricula> findByEstudianteIdAndAnioLectivoId(Long estudianteId, Long anioLectivoId);

    boolean existsByEstudianteIdAndAnioLectivoId(Long estudianteId, Long anioLectivoId);

    List<Matricula> findByEstudianteIdAndEstado(Long estudianteId, EstadoMatricula estado);
}
