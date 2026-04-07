package com.atara.deb.ataraapi.dto.usuario;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioAdminResponseDto {
    private Long id;
    private String nombre;
    private String apellidos;
    private String correo;
    private String rol;
    private String estado;
}
