package com.atara.deb.ataraapi.service.impl;

import com.atara.deb.ataraapi.model.AnioLectivo;
import com.atara.deb.ataraapi.repository.AnioLectivoRepository;
import com.atara.deb.ataraapi.service.AnioLectivoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@Transactional
public class AnioLectivoServiceImpl implements AnioLectivoService {

    private final AnioLectivoRepository anioLectivoRepository;

    public AnioLectivoServiceImpl(AnioLectivoRepository anioLectivoRepository) {
        this.anioLectivoRepository = anioLectivoRepository;
    }

    @Override
    public AnioLectivo crear(AnioLectivo anioLectivo) {
        if (anioLectivoRepository.existsByAnio(anioLectivo.getAnio())) {
            throw new IllegalArgumentException(
                "Ya existe un año lectivo para el año: " + anioLectivo.getAnio()
            );
        }
        if (anioLectivo.getFechaInicio().isAfter(anioLectivo.getFechaFin())) {
            throw new IllegalArgumentException("La fecha de inicio debe ser anterior a la fecha de fin.");
        }
        // Nuevo año se crea inactivo por defecto; se activa explícitamente
        if (anioLectivo.getActivo() == null) {
            anioLectivo.setActivo(false);
        }
        return anioLectivoRepository.save(anioLectivo);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AnioLectivo> listarTodos() {
        return anioLectivoRepository.findAllByOrderByAnioDesc();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<AnioLectivo> obtenerActivo() {
        return anioLectivoRepository.findByActivoTrue();
    }

    @Override
    @Transactional(readOnly = true)
    public AnioLectivo buscarPorId(Long id) {
        return anioLectivoRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("Año lectivo no encontrado con id: " + id));
    }

    @Override
    public AnioLectivo activar(Long id) {
        AnioLectivo anioLectivo = buscarPorId(id);
        // Desactiva el año activo actual antes de activar el nuevo
        anioLectivoRepository.desactivarTodos();
        anioLectivo.setActivo(true);
        return anioLectivoRepository.save(anioLectivo);
    }
}
