package com.atara.deb.ataraapi.dto.reporte;

public class DetalleContenidoReporteDto {

    private Long contentId;
    private String contentName;
    private String achievementLevel;
    private Double numericValue;

    public DetalleContenidoReporteDto() {
    }

    public Long getContentId() {
        return contentId;
    }

    public void setContentId(Long contentId) {
        this.contentId = contentId;
    }

    public String getContentName() {
        return contentName;
    }

    public void setContentName(String contentName) {
        this.contentName = contentName;
    }

    public String getAchievementLevel() {
        return achievementLevel;
    }

    public void setAchievementLevel(String achievementLevel) {
        this.achievementLevel = achievementLevel;
    }

    public Double getNumericValue() {
        return numericValue;
    }

    public void setNumericValue(Double numericValue) {
        this.numericValue = numericValue;
    }
}