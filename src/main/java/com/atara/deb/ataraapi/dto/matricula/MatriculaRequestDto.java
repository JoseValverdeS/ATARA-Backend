package com.atara.deb.ataraapi.dto.matricula;

import jakarta.validation.constraints.NotNull;

public class MatriculaRequestDto {

    @NotNull
    private Long estudianteId;

    @NotNull
    private Long seccionId;

    @NotNull
    private Long anioLectivoId;

    public Long getEstudianteId() { return estudianteId; }
    public void setEstudianteId(Long estudianteId) { this.estudianteId = estudianteId; }

    public Long getSeccionId() { return seccionId; }
    public void setSeccionId(Long seccionId) { this.seccionId = seccionId; }

    public Long getAnioLectivoId() { return anioLectivoId; }
    public void setAnioLectivoId(Long anioLectivoId) { this.anioLectivoId = anioLectivoId; }
}
