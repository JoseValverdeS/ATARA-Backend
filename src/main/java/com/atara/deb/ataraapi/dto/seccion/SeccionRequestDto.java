package com.atara.deb.ataraapi.dto.seccion;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeccionRequestDto {

    @NotBlank
    @Size(max = 10)
    private String nombre;          // 'A', 'B', 'C', …

    @NotNull
    private Long nivelId;

    @NotNull
    private Long centroId;

    @NotNull
    private Long anioLectivoId;

    private Long docenteId;         // nullable

    private Short capacidad;        // nullable
}
