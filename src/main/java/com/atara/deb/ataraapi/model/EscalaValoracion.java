package com.atara.deb.ataraapi.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "escalas_valoracion")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EscalaValoracion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "nombre", nullable = false, length = 50, unique = true)
    private String nombre;

    // CHAR(1): I, B, S, D
    @Column(name = "etiqueta", nullable = false, length = 1, unique = true)
    private String etiqueta;

    // SMALLINT — valor entre 1 y 4
    @Column(name = "valor_numerico", nullable = false, unique = true)
    private Short valorNumerico;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "created_at", insertable = false, updatable = false)
    private OffsetDateTime createdAt;
}
