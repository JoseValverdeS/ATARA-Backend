package com.atara.deb.ataraapi.dto.saber;

import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PromedioEjeResponseDto {
    private Integer ejeTemaaticoId;
    private String ejeNombre;
    private String ejeClave;
    private Integer materiaId;
    private String materiaNombre;
    private Integer tipoSaberId;
    private String tipoSaberNombre;
    private BigDecimal promedio;
    private Integer totalEvaluaciones;
    private Short valorMinimo;
    private Short valorMaximo;
    private String nivelAlerta;
}
