package com.atara.deb.ataraapi.dto.saber;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TipoSaberResponseDto {
    private Integer id;
    private String clave;
    private String nombre;
    private String descripcion;
}
