package com.atara.deb.ataraapi.repository;

import com.atara.deb.ataraapi.model.Estudiante;
import com.atara.deb.ataraapi.model.enums.EstadoEstudiante;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EstudianteRepository extends JpaRepository<Estudiante, Long> {

    Optional<Estudiante> findByIdentificacion(String identificacion);

    boolean existsByIdentificacion(String identificacion);

    List<Estudiante> findByEstado(EstadoEstudiante estado);
}