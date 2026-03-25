package com.atara.deb.ataraapi.repository;

import com.atara.deb.ataraapi.model.Estudiante;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EstudianteRepository extends JpaRepository<Estudiante, Long> {

    //List<Estudiante> findBySeccion_Id(Long seccionId);
}