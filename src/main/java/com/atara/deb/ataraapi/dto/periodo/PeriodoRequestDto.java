package com.atara.deb.ataraapi.dto.periodo;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PeriodoRequestDto {

    @NotBlank
    private String nombre;

    /** Requerido sólo al crear; ignorado en actualizar. */
    private Long anioLectivoId;
}
