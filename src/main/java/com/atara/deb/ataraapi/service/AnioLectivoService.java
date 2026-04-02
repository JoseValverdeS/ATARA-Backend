package com.atara.deb.ataraapi.service;

import com.atara.deb.ataraapi.dto.aniolectivo.AnioLectivoRequestDto;
import com.atara.deb.ataraapi.model.AnioLectivo;

import java.util.List;
import java.util.Optional;

public interface AnioLectivoService {

    AnioLectivo crear(AnioLectivo anioLectivo);

    AnioLectivo actualizar(Long id, AnioLectivoRequestDto dto);

    List<AnioLectivo> listarTodos();

    Optional<AnioLectivo> obtenerActivo();

    AnioLectivo buscarPorId(Long id);

    /**
     * Activa el año lectivo indicado y desactiva el que estaba activo.
     */
    AnioLectivo activar(Long id);

    /**
     * Elimina el año lectivo y todos sus datos dependientes (periodos, secciones,
     * matrículas, evaluaciones, alertas). Lanza excepción si el año está activo.
     */
    void eliminar(Long id);
}
