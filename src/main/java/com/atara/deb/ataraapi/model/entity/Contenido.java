// src/main/java/com/atara/deb/ataraapi/model/entity/Contenido.java
package com.atara.deb.ataraapi.model.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "contenidos")
public class Contenido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    private String descripcion;

    @Column(name = "materia_id", nullable = false)
    private Long materiaId;

    @Column(nullable = false)
    private String estado = "ACTIVO";

    public Contenido() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public Long getMateriaId() { return materiaId; }
    public void setMateriaId(Long materiaId) { this.materiaId = materiaId; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}