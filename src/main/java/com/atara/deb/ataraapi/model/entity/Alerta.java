// src/main/java/com/atara/deb/ataraapi/model/entity/Alerta.java
package com.atara.deb.ataraapi.model.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "alertas")
public class Alerta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "estudiante_id", nullable = false)
    private Long estudianteId;

    @Column(name = "contenido_id", nullable = false)
    private Long contenidoId;

    @Column(name = "periodo_id", nullable = false)
    private Long periodoId;

    @Column(name = "config_alerta_id")
    private Long configAlertaId;

    @Column(name = "tipo_alerta", nullable = false)
    private String tipoAlerta;

    @Column(name = "nivel_alerta", nullable = false)
    private String nivelAlerta;

    @Column(nullable = false)
    private String motivo;

    @Column(nullable = false)
    private String estado = "ACTIVA";

    @Column(name = "fecha_generacion", nullable = false)
    private LocalDateTime fechaGeneracion;

    public Alerta() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getEstudianteId() { return estudianteId; }
    public void setEstudianteId(Long estudianteId) { this.estudianteId = estudianteId; }

    public Long getContenidoId() { return contenidoId; }
    public void setContenidoId(Long contenidoId) { this.contenidoId = contenidoId; }

    public Long getPeriodoId() { return periodoId; }
    public void setPeriodoId(Long periodoId) { this.periodoId = periodoId; }

    public Long getConfigAlertaId() { return configAlertaId; }
    public void setConfigAlertaId(Long configAlertaId) { this.configAlertaId = configAlertaId; }

    public String getTipoAlerta() { return tipoAlerta; }
    public void setTipoAlerta(String tipoAlerta) { this.tipoAlerta = tipoAlerta; }

    public String getNivelAlerta() { return nivelAlerta; }
    public void setNivelAlerta(String nivelAlerta) { this.nivelAlerta = nivelAlerta; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public LocalDateTime getFechaGeneracion() { return fechaGeneracion; }
    public void setFechaGeneracion(LocalDateTime fechaGeneracion) { this.fechaGeneracion = fechaGeneracion; }
}