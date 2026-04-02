package com.atara.deb.ataraapi.dto.aniolectivo;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class AnioLectivoRequestDto {

    @NotNull
    @Min(2000)
    @Max(2200)
    private Short anio;

    public Short getAnio() { return anio; }
    public void setAnio(Short anio) { this.anio = anio; }
}
