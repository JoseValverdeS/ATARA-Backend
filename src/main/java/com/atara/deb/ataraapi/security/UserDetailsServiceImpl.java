package com.atara.deb.ataraapi.security;

import com.atara.deb.ataraapi.model.Usuario;
import com.atara.deb.ataraapi.repository.UsuarioRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public UserDetailsServiceImpl(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Carga el usuario por correo electrónico.
     * Spring Security llama a este método durante la autenticación.
     * El campo "username" en Spring Security equivale al correo en este proyecto.
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String correo) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "No existe usuario con correo: " + correo));
        return new UsuarioPrincipal(usuario);
    }
}
