package com.atara.deb.ataraapi.dto.evaluacion;

import java.time.OffsetDateTime;

public class EvaluacionResponseDto {

    private Long id;
    private Long estudianteId;
    private String estudianteNombreCompleto;
    private Long periodoId;
    private String periodoNombre;
    private Long usuarioId;
    private Long seccionId;
    private String seccionNombre;
    private String observacionGeneral;
    private String origenRegistro;
    private OffsetDateTime createdAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getEstudianteId() { return estudianteId; }
    public void setEstudianteId(Long estudianteId) { this.estudianteId = estudianteId; }

    public String getEstudianteNombreCompleto() { return estudianteNombreCompleto; }
    public void setEstudianteNombreCompleto(String estudianteNombreCompleto) { this.estudianteNombreCompleto = estudianteNombreCompleto; }

    public Long getPeriodoId() { return periodoId; }
    public void setPeriodoId(Long periodoId) { this.periodoId = periodoId; }

    public String getPeriodoNombre() { return periodoNombre; }
    public void setPeriodoNombre(String periodoNombre) { this.periodoNombre = periodoNombre; }

    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }

    public Long getSeccionId() { return seccionId; }
    public void setSeccionId(Long seccionId) { this.seccionId = seccionId; }

    public String getSeccionNombre() { return seccionNombre; }
    public void setSeccionNombre(String seccionNombre) { this.seccionNombre = seccionNombre; }

    public String getObservacionGeneral() { return observacionGeneral; }
    public void setObservacionGeneral(String observacionGeneral) { this.observacionGeneral = observacionGeneral; }

    public String getOrigenRegistro() { return origenRegistro; }
    public void setOrigenRegistro(String origenRegistro) { this.origenRegistro = origenRegistro; }

    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
}
