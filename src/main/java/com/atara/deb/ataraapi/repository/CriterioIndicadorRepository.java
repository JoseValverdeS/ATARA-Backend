package com.atara.deb.ataraapi.repository;

import com.atara.deb.ataraapi.model.CriterioIndicador;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CriterioIndicadorRepository extends JpaRepository<CriterioIndicador, Long> {

    List<CriterioIndicador> findByContenidoId(Long contenidoId);

    List<CriterioIndicador> findByDimensionId(Long dimensionId);
}
