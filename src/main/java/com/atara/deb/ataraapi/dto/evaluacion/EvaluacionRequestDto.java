package com.atara.deb.ataraapi.dto.evaluacion;

import com.atara.deb.ataraapi.model.enums.OrigenRegistro;
import jakarta.validation.constraints.NotNull;

public class EvaluacionRequestDto {

    @NotNull
    private Long estudianteId;

    @NotNull
    private Long periodoId;

    @NotNull
    private Long usuarioId;

    @NotNull
    private Long seccionId;

    private String observacionGeneral;

    @NotNull
    private OrigenRegistro origenRegistro;

    public Long getEstudianteId() { return estudianteId; }
    public void setEstudianteId(Long estudianteId) { this.estudianteId = estudianteId; }

    public Long getPeriodoId() { return periodoId; }
    public void setPeriodoId(Long periodoId) { this.periodoId = periodoId; }

    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }

    public Long getSeccionId() { return seccionId; }
    public void setSeccionId(Long seccionId) { this.seccionId = seccionId; }

    public String getObservacionGeneral() { return observacionGeneral; }
    public void setObservacionGeneral(String observacionGeneral) { this.observacionGeneral = observacionGeneral; }

    public OrigenRegistro getOrigenRegistro() { return origenRegistro; }
    public void setOrigenRegistro(OrigenRegistro origenRegistro) { this.origenRegistro = origenRegistro; }
}
