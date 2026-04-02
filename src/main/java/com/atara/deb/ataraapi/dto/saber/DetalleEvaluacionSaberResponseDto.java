package com.atara.deb.ataraapi.dto.saber;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DetalleEvaluacionSaberResponseDto {
    private Long id;
    private Integer ejeTemaaticoId;
    private String ejeNombre;
    private String ejeClave;
    private Short valor;
    private String nivelDesempenoNombre;
    private String observacion;
}
