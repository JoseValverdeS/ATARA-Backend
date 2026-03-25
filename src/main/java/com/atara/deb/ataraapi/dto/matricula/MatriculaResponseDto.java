package com.atara.deb.ataraapi.dto.matricula;

import java.time.LocalDate;
import java.time.OffsetDateTime;

public class MatriculaResponseDto {

    private Long id;
    private Long estudianteId;
    private String estudianteNombreCompleto;
    private Long seccionId;
    private String seccionNombre;
    private Long anioLectivoId;
    private Short anioLectivo;
    private String estado;
    private LocalDate fechaMatricula;
    private String observaciones;
    private OffsetDateTime createdAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getEstudianteId() { return estudianteId; }
    public void setEstudianteId(Long estudianteId) { this.estudianteId = estudianteId; }

    public String getEstudianteNombreCompleto() { return estudianteNombreCompleto; }
    public void setEstudianteNombreCompleto(String estudianteNombreCompleto) { this.estudianteNombreCompleto = estudianteNombreCompleto; }

    public Long getSeccionId() { return seccionId; }
    public void setSeccionId(Long seccionId) { this.seccionId = seccionId; }

    public String getSeccionNombre() { return seccionNombre; }
    public void setSeccionNombre(String seccionNombre) { this.seccionNombre = seccionNombre; }

    public Long getAnioLectivoId() { return anioLectivoId; }
    public void setAnioLectivoId(Long anioLectivoId) { this.anioLectivoId = anioLectivoId; }

    public Short getAnioLectivo() { return anioLectivo; }
    public void setAnioLectivo(Short anioLectivo) { this.anioLectivo = anioLectivo; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public LocalDate getFechaMatricula() { return fechaMatricula; }
    public void setFechaMatricula(LocalDate fechaMatricula) { this.fechaMatricula = fechaMatricula; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
}
