package com.atara.deb.ataraapi.dto.aniolectivo;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class AnioLectivoRequestDto {

    @NotNull
    private Short anio;

    @NotNull
    private LocalDate fechaInicio;

    @NotNull
    private LocalDate fechaFin;

    public Short getAnio() { return anio; }
    public void setAnio(Short anio) { this.anio = anio; }

    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }

    public LocalDate getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDate fechaFin) { this.fechaFin = fechaFin; }
}
