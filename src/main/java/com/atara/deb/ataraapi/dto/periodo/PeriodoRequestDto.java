package com.atara.deb.ataraapi.dto.periodo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PeriodoRequestDto {

    @NotBlank
    private String nombre;

    @NotNull
    private LocalDate fechaInicio;

    @NotNull
    private LocalDate fechaFin;

    /** Requerido sólo al crear; ignorado en actualizar. */
    private Long anioLectivoId;
}
