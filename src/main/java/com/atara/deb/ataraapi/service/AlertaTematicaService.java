package com.atara.deb.ataraapi.service;

import com.atara.deb.ataraapi.dto.alertatematica.AlertaTematicaResponseDto;

import java.util.List;

public interface AlertaTematicaService {

    List<AlertaTematicaResponseDto> generarAlertasPorEstudiante(Long estudianteId, Long periodoId);

    List<AlertaTematicaResponseDto> generarAlertasPorSeccion(Long seccionId, Long periodoId);

    List<AlertaTematicaResponseDto> obtenerAlertasPorEstudiante(Long estudianteId, Long periodoId);

    List<AlertaTematicaResponseDto> obtenerAlertasPorSeccion(Long seccionId, Long periodoId);
}
