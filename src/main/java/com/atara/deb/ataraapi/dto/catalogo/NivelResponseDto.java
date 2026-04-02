package com.atara.deb.ataraapi.dto.catalogo;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NivelResponseDto {
    private Long id;
    private Short numeroGrado;
    private String nombre;
}
