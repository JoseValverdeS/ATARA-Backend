package com.atara.deb.ataraapi.dto.visualizacion;

import java.util.List;

public class ProgresoSeccionDto {

    private Long sectionId;
    private String sectionName;
    private List<GraficoValorDto> valores;

    public ProgresoSeccionDto() {
    }

    public Long getSectionId() {
        return sectionId;
    }

    public void setSectionId(Long sectionId) {
        this.sectionId = sectionId;
    }

    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }

    public List<GraficoValorDto> getValores() {
        return valores;
    }

    public void setValores(List<GraficoValorDto> valores) {
        this.valores = valores;
    }
}