package com.atara.deb.ataraapi.dto.periodo;

import lombok.*;

import java.time.LocalDate;

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
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private Boolean activo;
}
