package com.atara.deb.ataraapi.dto.saber;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResumenPromediosEstudianteDto {
    private Long estudianteId;
    private String estudianteNombreCompleto;
    private Long periodoId;
    private String periodoNombre;
    private BigDecimal promedioGlobal;
    private List<PromedioTipoSaberResponseDto> promediosPorTipoSaber;
    private Integer totalAlertasAltas;
    private Integer totalAlertasMedias;
}
