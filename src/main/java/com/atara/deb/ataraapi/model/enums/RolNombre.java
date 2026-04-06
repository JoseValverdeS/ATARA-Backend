package com.atara.deb.ataraapi.model.enums;

/**
 * Nombres canónicos de los roles del sistema.
 * Usar siempre esta constante en lugar de literales String para evitar errores tipográficos.
 */
public enum RolNombre {
    ADMIN,
    DOCENTE,
    COORDINADOR;

    /** Convierte un String (valor guardado en BD) al enum. Lanza IllegalArgumentException si no coincide. */
    public static RolNombre fromString(String nombre) {
        return RolNombre.valueOf(nombre.toUpperCase());
    }
}
