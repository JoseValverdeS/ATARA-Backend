package com.atara.deb.ataraapi.dto.saber;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PromedioTipoSaberResponseDto {
    private Integer materiaId;
    private String materiaNombre;
    private Integer tipoSaberId;
    private String tipoSaberNombre;
    private BigDecimal promedioGeneral;
    private Integer ejesEvaluados;
    private List<PromedioEjeResponseDto> promediosPorEje;
}
