package com.atara.deb.ataraapi.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "tokens_refresh")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenRefresh {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // CASCADE DELETE: si se elimina el usuario, sus tokens se eliminan
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    // SHA-256 del refresh token — nunca el token en claro
    @Column(name = "token_hash", nullable = false, length = 255, unique = true)
    private String tokenHash;

    @Column(name = "expira_en", nullable = false)
    private OffsetDateTime expiraEn;

    @Column(name = "revocado", nullable = false)
    private Boolean revocado;

    // NULL si revocado = false; NOT NULL si revocado = true
    @Column(name = "revocado_en")
    private OffsetDateTime revocadoEn;

    // Tipo PostgreSQL INET — se mapea como String vía JDBC
    @Column(name = "ip_origen", columnDefinition = "inet")
    private String ipOrigen;

    @Column(name = "user_agent", length = 255)
    private String userAgent;

    // Sin updated_at — el estado de revocación se gestiona con revocado/revocado_en
    @Column(name = "created_at", insertable = false, updatable = false)
    private OffsetDateTime createdAt;
}
