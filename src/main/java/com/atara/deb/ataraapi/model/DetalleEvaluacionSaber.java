package com.atara.deb.ataraapi.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Table(
    name = "detalle_evaluacion_saber",
    uniqueConstraints = @UniqueConstraint(
        name = "uq_detalle_saber_eje",
        columnNames = {"evaluacion_saber_id", "eje_tematico_id"}
    )
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DetalleEvaluacionSaber {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evaluacion_saber_id", nullable = false)
    private EvaluacionSaber evaluacionSaber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "eje_tematico_id", nullable = false)
    private EjeTematico ejeTematico;

    @Column(name = "valor", nullable = false)
    private Short valor;

    @Column(name = "observacion")
    private String observacion;

    @Column(name = "created_at", insertable = false, updatable = false)
    private OffsetDateTime createdAt;
}
