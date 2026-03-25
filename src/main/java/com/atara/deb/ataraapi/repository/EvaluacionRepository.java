package com.atara.deb.ataraapi.repository;

import com.atara.deb.ataraapi.model.Evaluacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EvaluacionRepository extends JpaRepository<Evaluacion, Long> {

    List<Evaluacion> findByEstudianteIdAndPeriodoId(Long estudianteId, Long periodoId);
}