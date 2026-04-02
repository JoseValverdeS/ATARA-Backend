package com.atara.deb.ataraapi.dto.saber;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EvaluacionSaberResponseDto {
    private Long id;
    private Long estudianteId;
    private String estudianteNombreCompleto;
    private Long periodoId;
    private String periodoNombre;
    private Long usuarioId;
    private Long seccionId;
    private String seccionNombre;
    private Integer tipoSaberId;
    private String tipoSaberNombre;
    private LocalDate fechaEvaluacion;
    private String observacion;
    private List<DetalleEvaluacionSaberResponseDto> detalles;
    private LocalDateTime createdAt;
}
