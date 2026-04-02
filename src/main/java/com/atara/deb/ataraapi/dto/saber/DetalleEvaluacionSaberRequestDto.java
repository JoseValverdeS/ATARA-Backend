package com.atara.deb.ataraapi.dto.saber;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DetalleEvaluacionSaberRequestDto {

    @NotNull(message = "El ID del eje temático es obligatorio")
    private Integer ejeTemaaticoId;

    @NotNull(message = "El valor es obligatorio")
    @Min(value = 1, message = "El valor mínimo es 1")
    @Max(value = 5, message = "El valor máximo es 5")
    private Short valor;

    private String observacion;
}
