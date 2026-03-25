package com.atara.deb.ataraapi.repository;

import com.atara.deb.ataraapi.model.entity.DetalleEvaluacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DetalleEvaluacionRepository extends JpaRepository<DetalleEvaluacion, Long> {

    List<DetalleEvaluacion> findByEvaluacionId(Long evaluacionId);
}