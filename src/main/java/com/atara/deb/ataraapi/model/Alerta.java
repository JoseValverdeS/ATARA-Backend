package com.atara.deb.ataraapi.model;

import com.atara.deb.ataraapi.model.enums.EstadoAlerta;
import com.atara.deb.ataraapi.model.enums.NivelAlerta;
import com.atara.deb.ataraapi.model.enums.TipoAlerta;
import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "alertas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Alerta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estudiante_id", nullable = false)
    private Estudiante estudiante;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contenido_id", nullable = false)
    private Contenido contenido;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "periodo_id", nullable = false)
    private Periodo periodo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "config_alerta_id", nullable = false)
    private ConfiguracionAlerta configAlerta;

    // NULL = alerta manual sin evaluación automática
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evaluacion_id")
    private Evaluacion evaluacion;

    // NULL = generada automáticamente por el motor de reglas
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "generada_por")
    private Usuario generadaPor;

    // CHECK (tipo_alerta IN ('PREVENTIVA','MODERADA','CRITICA'))
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_alerta", nullable = false, length = 20)
    private TipoAlerta tipoAlerta;

    // CHECK (nivel_alerta IN ('BAJO','MEDIO','ALTO'))
    @Enumerated(EnumType.STRING)
    @Column(name = "nivel_alerta", nullable = false, length = 10)
    private NivelAlerta nivelAlerta;

    @Column(name = "motivo", nullable = false)
    private String motivo;

    // CHECK (estado IN ('ACTIVA','RESUELTA','DESCARTADA'))
    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 15)
    private EstadoAlerta estado;

    // DB DEFAULT NOW() — no insertar desde Java
    @Column(name = "fecha_generacion", insertable = false, updatable = false)
    private OffsetDateTime fechaGeneracion;

    // NULL cuando estado = ACTIVA; NOT NULL cuando RESUELTA o DESCARTADA
    @Column(name = "fecha_resolucion")
    private OffsetDateTime fechaResolucion;

    @Column(name = "created_at", insertable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private OffsetDateTime updatedAt;
}
