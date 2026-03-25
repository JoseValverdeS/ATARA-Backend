// src/main/java/com/atara/deb/ataraapi/model/entity/Seccion.java
package com.atara.deb.ataraapi.model.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "secciones")
public class Seccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    private String nivel;

    @Column(name = "centro_id", nullable = false)
    private Long centroId;

    @Column(name = "docente_id")
    private Long docenteId;

    @Column(nullable = false)
    private String estado = "ACTIVO";

    public Seccion() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getNivel() { return nivel; }
    public void setNivel(String nivel) { this.nivel = nivel; }

    public Long getCentroId() { return centroId; }
    public void setCentroId(Long centroId) { this.centroId = centroId; }

    public Long getDocenteId() { return docenteId; }
    public void setDocenteId(Long docenteId) { this.docenteId = docenteId; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}