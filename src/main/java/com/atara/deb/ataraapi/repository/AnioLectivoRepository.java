package com.atara.deb.ataraapi.repository;

import com.atara.deb.ataraapi.model.AnioLectivo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AnioLectivoRepository extends JpaRepository<AnioLectivo, Long> {

    Optional<AnioLectivo> findByActivoTrue();

    Optional<AnioLectivo> findByAnio(Short anio);

    boolean existsByAnio(Short anio);

    List<AnioLectivo> findAllByOrderByAnioDesc();

    // Desactiva todos los años lectivos (usado antes de activar uno nuevo)
    @Modifying(clearAutomatically = true)
    @Query("UPDATE AnioLectivo a SET a.activo = false WHERE a.activo = true")
    void desactivarTodos();
}
