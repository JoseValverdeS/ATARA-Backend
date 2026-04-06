package com.atara.deb.ataraapi.dto.saber;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MateriaResponseDto {
    private Integer id;
    private String clave;
    private String nombre;
}
