package com.atara.deb.ataraapi.security;

import com.atara.deb.ataraapi.exception.AccesoDenegadoException;
import com.atara.deb.ataraapi.model.enums.RolNombre;

import java.util.Set;

/**
 * Representa el alcance del usuario autenticado en la sesión actual.
 * Inmutable — se construye una vez por request en {@link ContextoUsuarioService}.
 *
 * <p>Cuando {@code esAdmin} es {@code true} (roles ADMIN / COORDINADOR),
 * los conjuntos de IDs están vacíos y todos los métodos de validación permiten el acceso.
 */
public record ContextoUsuario(
        Long usuarioId,
        RolNombre rol,
        boolean esAdmin,
        Set<Long> seccionIds,
        Set<Integer> materiaIds
) {

    /** Lanza {@link AccesoDenegadoException} si el usuario no tiene acceso a la sección. */
    public void verificarSeccion(Long seccionId) {
        if (!esAdmin && !seccionIds.contains(seccionId)) {
            throw new AccesoDenegadoException(
                    "No tiene permiso para operar en la sección ID: " + seccionId);
        }
    }

    /** Lanza {@link AccesoDenegadoException} si el usuario no tiene acceso a la materia. */
    public void verificarMateria(Integer materiaId) {
        if (!esAdmin && !materiaIds.contains(materiaId)) {
            throw new AccesoDenegadoException(
                    "No tiene permiso para operar con la materia ID: " + materiaId);
        }
    }

    /** {@code true} si la sección pertenece al alcance del usuario, o si es admin. */
    public boolean tieneSeccion(Long seccionId) {
        return esAdmin || seccionIds.contains(seccionId);
    }

    /** {@code true} si la materia pertenece al alcance del usuario, o si es admin. */
    public boolean tieneMateria(Integer materiaId) {
        return esAdmin || materiaIds.contains(materiaId);
    }
}
