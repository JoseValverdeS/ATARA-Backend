package com.atara.deb.ataraapi.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Table(
    name = "detalle_evaluacion",
    uniqueConstraints = @UniqueConstraint(
        name = "uq_detalle_criterio",
        columnNames = {"evaluacion_id", "criterio_id"}
    )
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DetalleEvaluacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // CASCADE ALL: si se borra la evaluación, se borran los detalles
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evaluacion_id", nullable = false)
    private Evaluacion evaluacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "criterio_id", nullable = false)
    private CriterioIndicador criterio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "escala_id", nullable = false)
    private EscalaValoracion escala;

    @Column(name = "observacion")
    private String observacion;

    // Sin updated_at — este registro es inmutable después de crearse
    @Column(name = "created_at", insertable = false, updatable = false)
    private OffsetDateTime createdAt;
}
