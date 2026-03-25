package com.atara.deb.ataraapi.dto.alerta;

public class ResumenAlertaDto {

    private Long total;
    private Long altas;
    private Long medias;
    private Long bajas;

    public ResumenAlertaDto() {
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Long getAltas() {
        return altas;
    }

    public void setAltas(Long altas) {
        this.altas = altas;
    }

    public Long getMedias() {
        return medias;
    }

    public void setMedias(Long medias) {
        this.medias = medias;
    }

    public Long getBajas() {
        return bajas;
    }

    public void setBajas(Long bajas) {
        this.bajas = bajas;
    }
}