package com.atara.deb.ataraapi.service.impl;

import com.atara.deb.ataraapi.dto.catalogo.CentroEducativoResponseDto;
import com.atara.deb.ataraapi.dto.catalogo.NivelResponseDto;
import com.atara.deb.ataraapi.dto.seccion.SeccionRequestDto;
import com.atara.deb.ataraapi.dto.seccion.SeccionResponseDto;
import com.atara.deb.ataraapi.dto.usuario.UsuarioDocenteResponseDto;
import com.atara.deb.ataraapi.model.*;
import com.atara.deb.ataraapi.model.enums.EstadoUsuario;
import com.atara.deb.ataraapi.repository.*;
import com.atara.deb.ataraapi.service.SeccionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional(readOnly = true)
public class SeccionServiceImpl implements SeccionService {

    private final SeccionRepository seccionRepository;
    private final NivelRepository nivelRepository;
    private final CentroEducativoRepository centroRepository;
    private final AnioLectivoRepository anioLectivoRepository;
    private final UsuarioRepository usuarioRepository;
    private final MatriculaRepository matriculaRepository;
    private final EvaluacionRepository evaluacionRepository;
    private final EvaluacionSaberRepository evaluacionSaberRepository;

    public SeccionServiceImpl(SeccionRepository seccionRepository,
                              NivelRepository nivelRepository,
                              CentroEducativoRepository centroRepository,
                              AnioLectivoRepository anioLectivoRepository,
                              UsuarioRepository usuarioRepository,
                              MatriculaRepository matriculaRepository,
                              EvaluacionRepository evaluacionRepository,
                              EvaluacionSaberRepository evaluacionSaberRepository) {
        this.seccionRepository = seccionRepository;
        this.nivelRepository = nivelRepository;
        this.centroRepository = centroRepository;
        this.anioLectivoRepository = anioLectivoRepository;
        this.usuarioRepository = usuarioRepository;
        this.matriculaRepository = matriculaRepository;
        this.evaluacionRepository = evaluacionRepository;
        this.evaluacionSaberRepository = evaluacionSaberRepository;
    }

    @Override
    public List<SeccionResponseDto> listarPorAnioLectivo(Long anioLectivoId) {
        return seccionRepository.findByAnioLectivoId(anioLectivoId)
                .stream()
                .sorted((a, b) -> {
                    int cmp = a.getNivel().getNumeroGrado().compareTo(b.getNivel().getNumeroGrado());
                    return cmp != 0 ? cmp : a.getNombre().compareTo(b.getNombre());
                })
                .map(this::toDto)
                .toList();
    }

    @Override
    public List<SeccionResponseDto> listarPorDocente(Long docenteId) {
        return seccionRepository.findByDocenteId(docenteId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public SeccionResponseDto buscarPorId(Long id) {
        Seccion seccion = seccionRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Sección no encontrada con id: " + id));
        return toDto(seccion);
    }

    @Override
    @Transactional
    public SeccionResponseDto crearSeccion(SeccionRequestDto dto) {
        Nivel nivel = nivelRepository.findById(dto.getNivelId())
                .orElseThrow(() -> new NoSuchElementException("Nivel no encontrado: " + dto.getNivelId()));
        CentroEducativo centro = centroRepository.findById(dto.getCentroId())
                .orElseThrow(() -> new NoSuchElementException("Centro educativo no encontrado: " + dto.getCentroId()));
        AnioLectivo anioLectivo = anioLectivoRepository.findById(dto.getAnioLectivoId())
                .orElseThrow(() -> new NoSuchElementException("Año lectivo no encontrado: " + dto.getAnioLectivoId()));

        Usuario docente = null;
        if (dto.getDocenteId() != null) {
            docente = usuarioRepository.findById(dto.getDocenteId())
                    .orElseThrow(() -> new NoSuchElementException("Docente no encontrado: " + dto.getDocenteId()));
        }

        Seccion seccion = Seccion.builder()
                .nombre(dto.getNombre())
                .nivel(nivel)
                .centro(centro)
                .anioLectivo(anioLectivo)
                .docente(docente)
                .capacidad(dto.getCapacidad())
                .build();

        return toDto(seccionRepository.save(seccion));
    }

    @Override
    public List<NivelResponseDto> listarNiveles() {
        return nivelRepository.findAll()
                .stream()
                .sorted((a, b) -> a.getNumeroGrado().compareTo(b.getNumeroGrado()))
                .map(n -> NivelResponseDto.builder()
                        .id(n.getId())
                        .numeroGrado(n.getNumeroGrado())
                        .nombre(n.getNombre())
                        .build())
                .toList();
    }

    @Override
    public List<CentroEducativoResponseDto> listarCentros() {
        return centroRepository.findAll()
                .stream()
                .sorted((a, b) -> a.getNombre().compareTo(b.getNombre()))
                .map(c -> CentroEducativoResponseDto.builder()
                        .id(c.getId())
                        .nombre(c.getNombre())
                        .circuito(c.getCircuito())
                        .direccionRegional(c.getDireccionRegional())
                        .build())
                .toList();
    }

    @Override
    public List<UsuarioDocenteResponseDto> listarDocentes() {
        return usuarioRepository.findByEstado(EstadoUsuario.ACTIVO)
                .stream()
                .filter(u -> "DOCENTE".equalsIgnoreCase(u.getRol().getNombre()))
                .sorted((a, b) -> a.getApellidos().compareTo(b.getApellidos()))
                .map(u -> UsuarioDocenteResponseDto.builder()
                        .id(u.getId())
                        .nombreCompleto(u.getNombre() + " " + u.getApellidos())
                        .correo(u.getCorreo())
                        .build())
                .toList();
    }

    @Override
    @Transactional
    public SeccionResponseDto actualizarSeccion(Long id, SeccionRequestDto dto) {
        Seccion seccion = seccionRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Sección no encontrada con id: " + id));
        Nivel nivel = nivelRepository.findById(dto.getNivelId())
                .orElseThrow(() -> new NoSuchElementException("Nivel no encontrado: " + dto.getNivelId()));
        CentroEducativo centro = centroRepository.findById(dto.getCentroId())
                .orElseThrow(() -> new NoSuchElementException("Centro educativo no encontrado: " + dto.getCentroId()));

        Usuario docente = null;
        if (dto.getDocenteId() != null) {
            docente = usuarioRepository.findById(dto.getDocenteId())
                    .orElseThrow(() -> new NoSuchElementException("Docente no encontrado: " + dto.getDocenteId()));
        }

        seccion.setNombre(dto.getNombre());
        seccion.setNivel(nivel);
        seccion.setCentro(centro);
        seccion.setDocente(docente);
        seccion.setCapacidad(dto.getCapacidad());

        return toDto(seccionRepository.save(seccion));
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        if (!seccionRepository.existsById(id)) {
            throw new NoSuchElementException("Sección no encontrada con id: " + id);
        }
        // Orden: evaluaciones saber → evaluaciones → matrículas → sección
        // (FK RESTRICT en evaluaciones.seccion_id, evaluaciones_saber.seccion_id, matriculas.seccion_id)
        evaluacionSaberRepository.deleteAllBySeccionId(id);  // cascada a detalle_evaluacion_saber
        evaluacionRepository.deleteAllBySeccionId(id);       // cascada a detalle_evaluacion
        matriculaRepository.deleteAllBySeccionId(id);
        seccionRepository.deleteById(id);
    }

    private SeccionResponseDto toDto(Seccion s) {
        Usuario docente = s.getDocente();
        String docenteNombre = docente != null
                ? docente.getNombre() + " " + docente.getApellidos()
                : null;

        return SeccionResponseDto.builder()
                .id(s.getId())
                .nombre(s.getNombre())
                .anioLectivoId(s.getAnioLectivo().getId())
                .anioLectivoAnio(s.getAnioLectivo().getAnio())
                .nivelNombre(s.getNivel().getNombre())
                .nivelGrado(s.getNivel().getNumeroGrado())
                .centroNombre(s.getCentro().getNombre())
                .docenteNombreCompleto(docenteNombre)
                .capacidad(s.getCapacidad())
                .build();
    }
}
