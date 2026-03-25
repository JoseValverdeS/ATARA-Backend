package com.atara.deb.ataraapi.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

/**
 * Tabla intermedia M:N entre usuarios y secciones.
 * Permite asignar más de un docente o coordinador a una misma sección,
 * complementando el docente titular registrado en secciones.docente_id.
 */
@Entity
@Table(
    name = "usuarios_secciones",
    uniqueConstraints = @UniqueConstraint(
        name = "uq_usuario_seccion",
        columnNames = {"usuario_id", "seccion_id"}
    )
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioSeccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seccion_id", nullable = false)
    private Seccion seccion;

    @Column(name = "created_at", insertable = false, updatable = false)
    private OffsetDateTime createdAt;
}
