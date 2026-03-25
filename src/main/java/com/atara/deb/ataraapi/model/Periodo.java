package com.atara.deb.ataraapi.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Table(
    name = "periodos",
    uniqueConstraints = @UniqueConstraint(
        name = "uq_periodo_por_anio",
        columnNames = {"anio_lectivo_id", "numero_periodo"}
    )
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Periodo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "anio_lectivo_id", nullable = false)
    private AnioLectivo anioLectivo;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    // SMALLINT — entre 1 y 6; UNIQUE por año (uq_periodo_por_anio)
    @Column(name = "numero_periodo", nullable = false)
    private Short numeroPeriodo;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin", nullable = false)
    private LocalDate fechaFin;

    // Solo uno por año puede ser TRUE (índice parcial uq_periodo_activo_por_anio)
    @Column(name = "activo", nullable = false)
    private Boolean activo;

    @Column(name = "created_at", insertable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private OffsetDateTime updatedAt;
}
