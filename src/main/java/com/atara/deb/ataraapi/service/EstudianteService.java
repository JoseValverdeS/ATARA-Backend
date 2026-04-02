package com.atara.deb.ataraapi.service;

import com.atara.deb.ataraapi.model.Estudiante;
import com.atara.deb.ataraapi.model.enums.EstadoEstudiante;

import java.util.List;
import java.util.Optional;

public interface EstudianteService {

    Estudiante registrar(Estudiante estudiante);

    Estudiante buscarPorId(Long id);

    Optional<Estudiante> buscarPorIdentificacion(String identificacion);

    List<Estudiante> listarTodos();

    List<Estudiante> listarPorEstado(EstadoEstudiante estado);

    Estudiante actualizar(Long id, Estudiante datosActualizados);

    void eliminar(Long id);
}
