package com.atara.deb.ataraapi.security;

import com.atara.deb.ataraapi.model.Usuario;
import com.atara.deb.ataraapi.model.enums.EstadoUsuario;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * Adapter entre Usuario (entidad JPA) y UserDetails (contrato de Spring Security).
 * Mantiene la entidad limpia y desacoplada de Spring Security.
 */
public class UsuarioPrincipal implements UserDetails {

    private final Usuario usuario;

    public UsuarioPrincipal(Usuario usuario) {
        this.usuario = usuario;
    }

    /** Devuelve la entidad real para acceder a datos del usuario autenticado. */
    public Usuario getUsuario() {
        return usuario;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + usuario.getRol().getNombre()));
    }

    @Override
    public String getPassword() {
        return usuario.getPassword();
    }

    /** Spring Security usa getUsername() como identificador único — aquí es el correo. */
    @Override
    public String getUsername() {
        return usuario.getCorreo();
    }

    @Override
    public boolean isEnabled() {
        return EstadoUsuario.ACTIVO.equals(usuario.getEstado());
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
}
