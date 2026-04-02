package com.atara.deb.ataraapi.dto.saber;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EjeTemaaticoResponseDto {
    private Integer id;
    private String clave;
    private String nombre;
    private String descripcion;
    private Short orden;
    private Integer tipoSaberId;
    private String tipoSaberNombre;
}
