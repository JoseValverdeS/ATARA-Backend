package com.atara.deb.ataraapi.dto.saber;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EvaluacionSaberRequestDto {

    @NotNull(message = "El ID del estudiante es obligatorio")
    private Long estudianteId;

    @NotNull(message = "El ID del periodo es obligatorio")
    private Long periodoId;

    @NotNull(message = "El ID del usuario evaluador es obligatorio")
    private Long usuarioId;

    @NotNull(message = "El ID de la sección es obligatorio")
    private Long seccionId;

    @NotNull(message = "El ID de la materia es obligatorio")
    private Integer materiaId;

    @NotNull(message = "El ID del tipo de saber es obligatorio")
    private Integer tipoSaberId;

    private LocalDate fechaEvaluacion;

    private String observacion;

    @NotNull(message = "Los detalles de evaluación son obligatorios")
    @Size(min = 1, message = "Debe incluir al menos un detalle de evaluación")
    @Valid
    private List<DetalleEvaluacionSaberRequestDto> detalles;
}
