package com.atara.deb.ataraapi.service.impl;

import com.atara.deb.ataraapi.dto.usuario.UsuarioAdminRequestDto;
import com.atara.deb.ataraapi.dto.usuario.UsuarioAdminResponseDto;
import com.atara.deb.ataraapi.model.Rol;
import com.atara.deb.ataraapi.model.Usuario;
import com.atara.deb.ataraapi.model.enums.EstadoUsuario;
import com.atara.deb.ataraapi.repository.RolRepository;
import com.atara.deb.ataraapi.repository.UsuarioRepository;
import com.atara.deb.ataraapi.service.AdminService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional(readOnly = true)
public class AdminServiceImpl implements AdminService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminServiceImpl(UsuarioRepository usuarioRepository,
                            RolRepository rolRepository,
                            PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<UsuarioAdminResponseDto> listarUsuarios() {
        return usuarioRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    @Transactional
    public UsuarioAdminResponseDto crearUsuario(UsuarioAdminRequestDto dto) {
        if (usuarioRepository.existsByCorreo(dto.getCorreo())) {
            throw new IllegalArgumentException("Ya existe un usuario con el correo: " + dto.getCorreo());
        }
        if (dto.getPassword() == null || dto.getPassword().isBlank()) {
            throw new IllegalArgumentException("La contraseña es requerida al crear un usuario.");
        }
        Rol rol = rolRepository.findByNombre(dto.getRol())
                .orElseThrow(() -> new IllegalArgumentException("Rol no válido: " + dto.getRol()));

        Usuario u = Usuario.builder()
                .nombre(dto.getNombre())
                .apellidos(dto.getApellidos())
                .correo(dto.getCorreo())
                .password(passwordEncoder.encode(dto.getPassword()))
                .rol(rol)
                .estado(EstadoUsuario.ACTIVO)
                .build();
        return toDto(usuarioRepository.save(u));
    }

    @Override
    @Transactional
    public UsuarioAdminResponseDto actualizarUsuario(Long id, UsuarioAdminRequestDto dto) {
        Usuario u = usuarioRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado: " + id));

        if (!u.getCorreo().equalsIgnoreCase(dto.getCorreo())
                && usuarioRepository.existsByCorreo(dto.getCorreo())) {
            throw new IllegalArgumentException("Ya existe un usuario con el correo: " + dto.getCorreo());
        }
        Rol rol = rolRepository.findByNombre(dto.getRol())
                .orElseThrow(() -> new IllegalArgumentException("Rol no válido: " + dto.getRol()));

        u.setNombre(dto.getNombre());
        u.setApellidos(dto.getApellidos());
        u.setCorreo(dto.getCorreo());
        u.setRol(rol);

        if (dto.getEstado() != null && !dto.getEstado().isBlank()) {
            try {
                u.setEstado(EstadoUsuario.valueOf(dto.getEstado()));
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Estado no válido: " + dto.getEstado());
            }
        }
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            u.setPassword(passwordEncoder.encode(dto.getPassword()));
        }
        return toDto(usuarioRepository.save(u));
    }

    @Override
    @Transactional
    public void eliminarUsuario(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new NoSuchElementException("Usuario no encontrado: " + id);
        }
        usuarioRepository.deleteById(id);
    }

    private UsuarioAdminResponseDto toDto(Usuario u) {
        return UsuarioAdminResponseDto.builder()
                .id(u.getId())
                .nombre(u.getNombre())
                .apellidos(u.getApellidos())
                .correo(u.getCorreo())
                .rol(u.getRol().getNombre())
                .estado(u.getEstado().name())
                .build();
    }
}
