package com.atara.deb.ataraapi.model;

import com.atara.deb.ataraapi.model.enums.NivelAlerta;
import com.atara.deb.ataraapi.model.enums.TipoAlerta;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "configuracion_alertas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConfiguracionAlerta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre", nullable = false, length = 200)
    private String nombre;

    @Column(name = "descripcion")
    private String descripcion;

    // NUMERIC(5,2) — nullable: ausencia de límite inferior
    @Column(name = "umbral_minimo", precision = 5, scale = 2)
    private BigDecimal umbralMinimo;

    // NUMERIC(5,2) — nullable: ausencia de límite superior
    @Column(name = "umbral_maximo", precision = 5, scale = 2)
    private BigDecimal umbralMaximo;

    // CHECK (tipo_alerta IN ('PREVENTIVA','MODERADA','CRITICA'))
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_alerta", nullable = false, length = 20)
    private TipoAlerta tipoAlerta;

    // CHECK (nivel_resultante IN ('BAJO','MEDIO','ALTO'))
    @Enumerated(EnumType.STRING)
    @Column(name = "nivel_resultante", nullable = false, length = 10)
    private NivelAlerta nivelResultante;

    // NULL = regla aplica al promedio global del estudiante
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dimension_id")
    private DimensionEvaluacion dimension;

    @Column(name = "created_at", insertable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private OffsetDateTime updatedAt;
}
