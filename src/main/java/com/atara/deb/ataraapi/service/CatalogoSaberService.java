package com.atara.deb.ataraapi.service;

import com.atara.deb.ataraapi.dto.saber.EjeTemaaticoResponseDto;
import com.atara.deb.ataraapi.dto.saber.MateriaResponseDto;
import com.atara.deb.ataraapi.dto.saber.NivelDesempenoResponseDto;
import com.atara.deb.ataraapi.dto.saber.TipoSaberResponseDto;

import java.util.List;

public interface CatalogoSaberService {

    List<TipoSaberResponseDto> listarTiposSaber();

    List<MateriaResponseDto> listarMaterias();

    List<EjeTemaaticoResponseDto> listarEjesTematicos();

    List<EjeTemaaticoResponseDto> listarEjesPorTipoSaber(Integer tipoSaberId);

    List<EjeTemaaticoResponseDto> listarEjesPorMateriaYTipoSaber(Integer materiaId, Integer tipoSaberId);

    List<NivelDesempenoResponseDto> listarNivelesDesempeno();
}
