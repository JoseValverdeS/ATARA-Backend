package com.atara.deb.ataraapi.dto.periodo;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PeriodoResponseDto {
    private Long id;
    private Long anioLectivoId;
    private Short anioLectivoAnio;
    private String nombre;
    private Short numeroPeriodo;
    private Boolean activo;
}
