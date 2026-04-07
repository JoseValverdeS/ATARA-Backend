package com.atara.deb.ataraapi.dto.usuario;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioAdminRequestDto {

    @NotBlank
    @Size(max = 100)
    private String nombre;

    @NotBlank
    @Size(max = 150)
    private String apellidos;

    @NotBlank
    @Email
    @Size(max = 150)
    private String correo;

    /** Requerido al crear. Si se omite en PUT, la contraseña no cambia. */
    @Size(min = 8, max = 100)
    private String password;

    /** Nombre del rol: ADMIN, DOCENTE o COORDINADOR. */
    @NotBlank
    private String rol;

    /** Solo aplica en PUT. En POST el usuario siempre se crea como ACTIVO. */
    private String estado;
}
