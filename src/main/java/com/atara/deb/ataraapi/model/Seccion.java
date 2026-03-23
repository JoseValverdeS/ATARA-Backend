package com.atara.deb.ataraapi.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Table(
    name = "secciones",
    uniqueConstraints = @UniqueConstraint(
        name = "uq_seccion",
        columnNames = {"nivel_id", "centro_id", "anio_lectivo_id", "nombre"}
    )
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Seccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // 'A', 'B', 'C', … — parte del UNIQUE junto con nivel, centro y año
    @Column(name = "nombre", nullable = false, length = 10)
    private String nombre;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nivel_id", nullable = false)
    private Nivel nivel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "centro_id", nullable = false)
    private CentroEducativo centro;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "anio_lectivo_id", nullable = false)
    private AnioLectivo anioLectivo;

    // Docente titular — nullable (SET NULL en ON DELETE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "docente_id")
    private Usuario docente;

    // SMALLINT — nullable; CHECK (capacidad > 0) en DB
    @Column(name = "capacidad")
    private Short capacidad;

    @Column(name = "created_at", insertable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private OffsetDateTime updatedAt;
}
