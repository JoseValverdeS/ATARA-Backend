package com.atara.deb.ataraapi.service.impl;

import com.atara.deb.ataraapi.dto.saber.EjeTemaaticoResponseDto;
import com.atara.deb.ataraapi.dto.saber.NivelDesempenoResponseDto;
import com.atara.deb.ataraapi.dto.saber.TipoSaberResponseDto;
import com.atara.deb.ataraapi.repository.EjeTemaaticoRepository;
import com.atara.deb.ataraapi.repository.NivelDesempenoRepository;
import com.atara.deb.ataraapi.repository.TipoSaberRepository;
import com.atara.deb.ataraapi.service.CatalogoSaberService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class CatalogoSaberServiceImpl implements CatalogoSaberService {

    private final TipoSaberRepository tipoSaberRepository;
    private final EjeTemaaticoRepository ejeTemaaticoRepository;
    private final NivelDesempenoRepository nivelDesempenoRepository;

    public CatalogoSaberServiceImpl(TipoSaberRepository tipoSaberRepository,
                                     EjeTemaaticoRepository ejeTemaaticoRepository,
                                     NivelDesempenoRepository nivelDesempenoRepository) {
        this.tipoSaberRepository = tipoSaberRepository;
        this.ejeTemaaticoRepository = ejeTemaaticoRepository;
        this.nivelDesempenoRepository = nivelDesempenoRepository;
    }

    @Override
    public List<TipoSaberResponseDto> listarTiposSaber() {
        return tipoSaberRepository.findAll().stream()
            .map(ts -> TipoSaberResponseDto.builder()
                .id(ts.getId())
                .clave(ts.getClave())
                .nombre(ts.getNombre())
                .descripcion(ts.getDescripcion())
                .build())
            .toList();
    }

    @Override
    public List<EjeTemaaticoResponseDto> listarEjesTematicos() {
        return ejeTemaaticoRepository.findAllByOrderByTipoSaberIdAscOrdenAsc().stream()
            .map(ej -> EjeTemaaticoResponseDto.builder()
                .id(ej.getId())
                .clave(ej.getClave())
                .nombre(ej.getNombre())
                .descripcion(ej.getDescripcion())
                .orden(ej.getOrden())
                .tipoSaberId(ej.getTipoSaber().getId())
                .tipoSaberNombre(ej.getTipoSaber().getNombre())
                .build())
            .toList();
    }

    @Override
    public List<EjeTemaaticoResponseDto> listarEjesPorTipoSaber(Integer tipoSaberId) {
        return ejeTemaaticoRepository.findByTipoSaberIdOrderByOrden(tipoSaberId).stream()
            .map(ej -> EjeTemaaticoResponseDto.builder()
                .id(ej.getId())
                .clave(ej.getClave())
                .nombre(ej.getNombre())
                .descripcion(ej.getDescripcion())
                .orden(ej.getOrden())
                .tipoSaberId(ej.getTipoSaber().getId())
                .tipoSaberNombre(ej.getTipoSaber().getNombre())
                .build())
            .toList();
    }

    @Override
    public List<NivelDesempenoResponseDto> listarNivelesDesempeno() {
        return nivelDesempenoRepository.findAllByOrderByValorNumericoAsc().stream()
            .map(nd -> NivelDesempenoResponseDto.builder()
                .id(nd.getId())
                .valorNumerico(nd.getValorNumerico())
                .nombre(nd.getNombre())
                .etiqueta(nd.getEtiqueta())
                .descripcion(nd.getDescripcion())
                .build())
            .toList();
    }
}
