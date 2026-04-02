package com.atara.deb.ataraapi.repository;

import com.atara.deb.ataraapi.model.EjeTematico;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EjeTemaaticoRepository extends JpaRepository<EjeTematico, Integer> {
    List<EjeTematico> findByTipoSaberIdOrderByOrden(Integer tipoSaberId);
    List<EjeTematico> findAllByOrderByTipoSaberIdAscOrdenAsc();
}
