package com.atara.deb.ataraapi.dto.usuario;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioDocenteResponseDto {
    private Long id;
    private String nombreCompleto;
    private String correo;
}
