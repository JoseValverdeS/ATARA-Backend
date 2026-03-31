package com.atara.deb.ataraapi.dto.piad;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EstudiantePIADDto {
    private int    numero;
    private String cedula;
    private String primerApellido;
    private String segundoApellido;
    private String nombre;
    private String tipoAdecuacion;
    private String nivel;
    private int    grupo;
    private String fechaMatricula;
    private String codigoEstado;
}
