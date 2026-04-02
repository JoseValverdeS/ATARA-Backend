package com.atara.deb.ataraapi.service;

import com.atara.deb.ataraapi.dto.periodo.PeriodoRequestDto;
import com.atara.deb.ataraapi.dto.periodo.PeriodoResponseDto;

import java.util.List;
import java.util.Optional;

public interface PeriodoService {
    List<PeriodoResponseDto> listarPorAnioLectivo(Long anioLectivoId);
    Optional<PeriodoResponseDto> obtenerActivo(Long anioLectivoId);
    PeriodoResponseDto activar(Long periodoId);
    PeriodoResponseDto crear(PeriodoRequestDto dto);
    PeriodoResponseDto actualizar(Long id, PeriodoRequestDto dto);

    /**
     * Elimina el periodo y todas sus evaluaciones y alertas.
     * Lanza excepción si el periodo está activo.
     */
    void eliminar(Long periodoId);
}
