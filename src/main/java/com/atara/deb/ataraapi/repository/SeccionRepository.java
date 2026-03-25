package com.atara.deb.ataraapi.repository;

import com.atara.deb.ataraapi.model.Seccion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SeccionRepository extends JpaRepository<Seccion, Long> {

    List<Seccion> findByAnioLectivoId(Long anioLectivoId);

    List<Seccion> findByDocenteId(Long docenteId);
}