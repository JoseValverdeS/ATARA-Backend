package com.atara.deb.ataraapi.service;

import com.atara.deb.ataraapi.dto.saber.*;

import java.util.List;

public interface EvaluacionSaberService {

    EvaluacionSaberResponseDto registrar(EvaluacionSaberRequestDto request);

    EvaluacionSaberResponseDto actualizar(Long id, EvaluacionSaberRequestDto request);

    EvaluacionSaberResponseDto buscarPorId(Long id);

    List<EvaluacionSaberResponseDto> listarPorEstudianteYPeriodo(Long estudianteId, Long periodoId);

    List<EvaluacionSaberResponseDto> listarPorSeccionYPeriodo(Long seccionId, Long periodoId);

    ResumenPromediosEstudianteDto obtenerPromedios(Long estudianteId, Long periodoId);

    List<ResumenPromediosEstudianteDto> obtenerPromediosSeccion(Long seccionId, Long periodoId);
}
