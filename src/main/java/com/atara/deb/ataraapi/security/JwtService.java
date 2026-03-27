package com.atara.deb.ataraapi.security;

import com.atara.deb.ataraapi.model.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration-ms}")
    private long expirationMs;

    /**
     * Genera un access token JWT para el usuario dado.
     * Claims incluidos: subject=correo, userId, rol.
     */
    public String generarToken(Usuario usuario) {
        Date ahora = new Date();
        Date expiracion = new Date(ahora.getTime() + expirationMs);

        return Jwts.builder()
                .subject(usuario.getCorreo())
                .claim("userId", usuario.getId())
                .claim("rol", usuario.getRol().getNombre())
                .issuedAt(ahora)
                .expiration(expiracion)
                .signWith(getSigningKey())
                .compact();
    }

    /** Extrae el correo (subject) del token. */
    public String extraerCorreo(String token) {
        return extraerClaim(token, Claims::getSubject);
    }

    /** Extrae el userId del token. */
    public Long extraerUserId(String token) {
        return extraerClaim(token, claims -> claims.get("userId", Long.class));
    }

    /** Extrae el rol del token. */
    public String extraerRol(String token) {
        return extraerClaim(token, claims -> claims.get("rol", String.class));
    }

    /**
     * Valida que el token pertenezca al usuario y no haya expirado.
     * No lanza excepción — devuelve false si hay cualquier problema.
     */
    public boolean esTokenValido(String token, UserDetails userDetails) {
        try {
            String correo = extraerCorreo(token);
            return correo.equals(userDetails.getUsername()) && !esTokenExpirado(token);
        } catch (Exception e) {
            return false;
        }
    }

    public long getExpirationMs() {
        return expirationMs;
    }

    // -------------------------------------------------------------------------

    private boolean esTokenExpirado(String token) {
        return extraerClaim(token, Claims::getExpiration).before(new Date());
    }

    private <T> T extraerClaim(String token, Function<Claims, T> resolver) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return resolver.apply(claims);
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
