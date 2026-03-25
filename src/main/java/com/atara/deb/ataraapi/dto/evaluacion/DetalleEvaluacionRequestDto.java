package com.atara.deb.ataraapi.dto.evaluacion;

import jakarta.validation.constraints.NotNull;

public class DetalleEvaluacionRequestDto {

    @NotNull
    private Long criterioId;

    @NotNull
    private Long escalaId;

    private String observacion;

    public Long getCriterioId() { return criterioId; }
    public void setCriterioId(Long criterioId) { this.criterioId = criterioId; }

    public Long getEscalaId() { return escalaId; }
    public void setEscalaId(Long escalaId) { this.escalaId = escalaId; }

    public String getObservacion() { return observacion; }
    public void setObservacion(String observacion) { this.observacion = observacion; }
}
