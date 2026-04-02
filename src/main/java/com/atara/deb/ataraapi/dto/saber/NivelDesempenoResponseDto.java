package com.atara.deb.ataraapi.dto.saber;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NivelDesempenoResponseDto {
    private Integer id;
    private Short valorNumerico;
    private String nombre;
    private String etiqueta;
    private String descripcion;
}
