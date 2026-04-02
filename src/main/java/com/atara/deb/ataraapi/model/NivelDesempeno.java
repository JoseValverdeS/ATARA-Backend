package com.atara.deb.ataraapi.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "niveles_desempeno")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NivelDesempeno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "valor_numerico", nullable = false, unique = true)
    private Short valorNumerico;

    @Column(name = "nombre", nullable = false, length = 50, unique = true)
    private String nombre;

    @Column(name = "etiqueta", nullable = false, length = 5, unique = true)
    private String etiqueta;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "created_at", insertable = false, updatable = false)
    private OffsetDateTime createdAt;
}
