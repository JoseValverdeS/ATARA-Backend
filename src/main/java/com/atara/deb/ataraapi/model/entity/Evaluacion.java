// src/main/java/com/atara/deb/ataraapi/model/entity/Evaluacion.java
package com.atara.deb.ataraapi.model.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "evaluaciones")
public class Evaluacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "estudiante_id", nullable = false)
    private Long estudianteId;

    @Column(name = "periodo_id", nullable = false)
    private Long periodoId;

    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @Column(name = "fecha_registro", nullable = false)
    private LocalDateTime fechaRegistro;

    @Column(name = "observacion_general")
    private String observacionGeneral;

    @Column(name = "origen_registro", nullable = false)
    private String origenRegistro = "MANUAL";

    public Evaluacion() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getEstudianteId() { return estudianteId; }
    public void setEstudianteId(Long estudianteId) { this.estudianteId = estudianteId; }

    public Long getPeriodoId() { return periodoId; }
    public void setPeriodoId(Long periodoId) { this.periodoId = periodoId; }

    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }

    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDateTime fechaRegistro) { this.fechaRegistro = fechaRegistro; }

    public String getObservacionGeneral() { return observacionGeneral; }
    public void setObservacionGeneral(String observacionGeneral) { this.observacionGeneral = observacionGeneral; }

    public String getOrigenRegistro() { return origenRegistro; }
    public void setOrigenRegistro(String origenRegistro) { this.origenRegistro = origenRegistro; }
}