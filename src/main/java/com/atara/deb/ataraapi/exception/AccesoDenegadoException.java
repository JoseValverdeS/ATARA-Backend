package com.atara.deb.ataraapi.exception;

/**
 * Lanzada cuando el usuario autenticado intenta acceder a un recurso
 * que no pertenece a su alcance asignado (sección, materia, estudiante).
 * Mapeada a HTTP 403 en {@link GlobalExceptionHandler}.
 */
public class AccesoDenegadoException extends RuntimeException {

    public AccesoDenegadoException(String message) {
        super(message);
    }
}
