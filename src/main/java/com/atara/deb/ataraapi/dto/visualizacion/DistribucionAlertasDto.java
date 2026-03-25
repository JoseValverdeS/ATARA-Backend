package com.atara.deb.ataraapi.dto.visualizacion;

import java.util.List;

public class DistribucionAlertasDto {

    private Long sectionId;
    private List<GraficoValorDto> distribucion;

    public DistribucionAlertasDto() {
    }

    public Long getSectionId() {
        return sectionId;
    }

    public void setSectionId(Long sectionId) {
        this.sectionId = sectionId;
    }

    public List<GraficoValorDto> getDistribucion() {
        return distribucion;
    }

    public void setDistribucion(List<GraficoValorDto> distribucion) {
        this.distribucion = distribucion;
    }
}