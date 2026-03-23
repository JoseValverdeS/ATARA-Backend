package com.atara.deb.ataraapi.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Table(name = "anios_lectivos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnioLectivo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // SMALLINT — entre 2000 y 2200; UNIQUE garantizado por uq_anio_lectivo
    @Column(name = "anio", nullable = false, unique = true)
    private Short anio;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin", nullable = false)
    private LocalDate fechaFin;

    // Solo uno puede ser TRUE (índice parcial uq_un_anio_activo)
    @Column(name = "activo", nullable = false)
    private Boolean activo;

    @Column(name = "created_at", insertable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private OffsetDateTime updatedAt;
}
