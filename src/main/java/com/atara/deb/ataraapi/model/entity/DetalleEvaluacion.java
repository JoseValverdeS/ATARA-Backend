// src/main/java/com/atara/deb/ataraapi/model/entity/DetalleEvaluacion.java
package com.atara.deb.ataraapi.model.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "detalle_evaluacion")
public class DetalleEvaluacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "evaluacion_id", nullable = false)
    private Long evaluacionId;

    @Column(name = "contenido_id", nullable = false)
    private Long contenidoId;

    @Column(name = "criterio_id", nullable = false)
    private Long criterioId;

    @Column(name = "dimension_id")
    private Long dimensionId;

    @Column(name = "escala_id", nullable = false)
    private Long escalaId;

    @Column(name = "valor_numerico", nullable = false)
    private Double valorNumerico;

    private String observacion;

    public DetalleEvaluacion() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getEvaluacionId() { return evaluacionId; }
    public void setEvaluacionId(Long evaluacionId) { this.evaluacionId = evaluacionId; }

    public Long getContenidoId() { return contenidoId; }
    public void setContenidoId(Long contenidoId) { this.contenidoId = contenidoId; }

    public Long getCriterioId() { return criterioId; }
    public void setCriterioId(Long criterioId) { this.criterioId = criterioId; }

    public Long getDimensionId() { return dimensionId; }
    public void setDimensionId(Long dimensionId) { this.dimensionId = dimensionId; }

    public Long getEscalaId() { return escalaId; }
    public void setEscalaId(Long escalaId) { this.escalaId = escalaId; }

    public Double getValorNumerico() { return valorNumerico; }
    public void setValorNumerico(Double valorNumerico) { this.valorNumerico = valorNumerico; }

    public String getObservacion() { return observacion; }
    public void setObservacion(String observacion) { this.observacion = observacion; }
}