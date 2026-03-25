package com.atara.deb.ataraapi.repository;

import com.atara.deb.ataraapi.model.Periodo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PeriodoRepository extends JpaRepository<Periodo, Long> {

    List<Periodo> findByAnioLectivoId(Long anioLectivoId);

    Optional<Periodo> findByAnioLectivoIdAndActivoTrue(Long anioLectivoId);

    boolean existsByAnioLectivoIdAndNumeroPeriodo(Long anioLectivoId, Short numeroPeriodo);

    // Desactiva todos los periodos de un año lectivo (usado antes de activar uno nuevo)
    @Modifying(clearAutomatically = true)
    @Query("UPDATE Periodo p SET p.activo = false WHERE p.anioLectivo.id = :anioLectivoId AND p.activo = true")
    void desactivarPeriodosDeAnio(@Param("anioLectivoId") Long anioLectivoId);
}
