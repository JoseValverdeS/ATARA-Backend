package com.atara.deb.ataraapi.repository;

import com.atara.deb.ataraapi.model.entity.Alerta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlertaRepository extends JpaRepository<Alerta, Long> {

    List<Alerta> findByEstudianteIdAndPeriodoId(Long estudianteId, Long periodoId);

    List<Alerta> findByPeriodoId(Long periodoId);
}