package com.atara.deb.ataraapi.dto.seccion;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeccionResponseDto {
    private Long id;
    private String nombre;
    private Long anioLectivoId;
    private Short anioLectivoAnio;
    private String nivelNombre;
    private Short nivelGrado;
    private String centroNombre;
    private String docenteNombreCompleto;
    private Short capacidad;
}
