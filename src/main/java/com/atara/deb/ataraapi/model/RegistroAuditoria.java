package com.atara.deb.ataraapi.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

/**
 * Log inmutable de acciones de usuario.
 * No tiene updated_at — los registros de auditoría no se modifican.
 * usuario_id puede ser NULL si la acción fue del sistema (no de un usuario).
 */
@Entity
@Table(name = "registro_auditoria")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegistroAuditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // NULL si la acción fue del sistema; SET NULL si el usuario es eliminado
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    // INSERT, UPDATE, DELETE, LOGIN, LOGOUT, VIEW, EXPORT
    @Column(name = "accion", nullable = false, length = 50)
    private String accion;

    @Column(name = "tabla_nombre", length = 100)
    private String tablaNombre;

    // BIGINT — soporta PKs grandes sin migración futura
    @Column(name = "registro_id")
    private Long registroId;

    // JSONB — estado anterior del registro; NULL en INSERT
    @Column(name = "valores_anteriores", columnDefinition = "jsonb")
    private String valoresAnteriores;

    // JSONB — estado nuevo del registro; NULL en DELETE
    @Column(name = "valores_nuevos", columnDefinition = "jsonb")
    private String valoresNuevos;

    // Tipo PostgreSQL INET — se mapea como String vía JDBC
    @Column(name = "ip_origen", columnDefinition = "inet")
    private String ipOrigen;

    @Column(name = "user_agent", length = 500)
    private String userAgent;

    @Column(name = "created_at", insertable = false, updatable = false)
    private OffsetDateTime createdAt;
}
