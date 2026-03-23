package com.atara.deb.ataraapi.model;

import com.atara.deb.ataraapi.model.enums.OrigenRegistro;
import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
    name = "evaluaciones",
    uniqueConstraints = @UniqueConstraint(
        name = "uq_evaluacion",
        columnNames = {"estudiante_id", "usuario_id", "periodo_id"}
    )
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Evaluacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estudiante_id", nullable = false)
    private Estudiante estudiante;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "periodo_id", nullable = false)
    private Periodo periodo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    // Denormalizado desde matriculas para preservar contexto histórico por sección/nivel
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seccion_id", nullable = false)
    private Seccion seccion;

    @Column(name = "observacion_general")
    private String observacionGeneral;

    // CHECK (origen_registro IN ('MANUAL','AUTOMATICO'))
    @Enumerated(EnumType.STRING)
    @Column(name = "origen_registro", nullable = false, length = 20)
    private OrigenRegistro origenRegistro;

    // Relación bidireccional con DetalleEvaluacion — el agregado se gestiona desde aquí
    @OneToMany(mappedBy = "evaluacion", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<DetalleEvaluacion> detalles = new ArrayList<>();

    @Column(name = "created_at", insertable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private OffsetDateTime updatedAt;
}
