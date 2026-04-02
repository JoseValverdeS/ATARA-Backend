package com.atara.deb.ataraapi.service;

import com.atara.deb.ataraapi.dto.catalogo.CentroEducativoResponseDto;
import com.atara.deb.ataraapi.dto.catalogo.NivelResponseDto;
import com.atara.deb.ataraapi.dto.seccion.SeccionRequestDto;
import com.atara.deb.ataraapi.dto.seccion.SeccionResponseDto;
import com.atara.deb.ataraapi.dto.usuario.UsuarioDocenteResponseDto;

import java.util.List;

public interface SeccionService {
    List<SeccionResponseDto> listarPorAnioLectivo(Long anioLectivoId);
    List<SeccionResponseDto> listarPorDocente(Long docenteId);
    SeccionResponseDto crearSeccion(SeccionRequestDto dto);

    SeccionResponseDto actualizarSeccion(Long id, SeccionRequestDto dto);

    List<NivelResponseDto> listarNiveles();
    List<CentroEducativoResponseDto> listarCentros();
    List<UsuarioDocenteResponseDto> listarDocentes();

    void eliminar(Long id);
}
