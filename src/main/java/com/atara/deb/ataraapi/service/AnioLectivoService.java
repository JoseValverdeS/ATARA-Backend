package com.atara.deb.ataraapi.service;

import com.atara.deb.ataraapi.model.AnioLectivo;

import java.util.List;
import java.util.Optional;

public interface AnioLectivoService {

    AnioLectivo crear(AnioLectivo anioLectivo);

    List<AnioLectivo> listarTodos();

    Optional<AnioLectivo> obtenerActivo();

    AnioLectivo buscarPorId(Long id);

    /**
     * Activa el año lectivo indicado y desactiva el que estaba activo.
     */
    AnioLectivo activar(Long id);
}
