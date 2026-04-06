package com.atara.deb.ataraapi.security;

import com.atara.deb.ataraapi.exception.AccesoDenegadoException;
import com.atara.deb.ataraapi.model.enums.RolNombre;
import com.atara.deb.ataraapi.repository.MatriculaRepository;
import com.atara.deb.ataraapi.repository.UsuarioRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

/**
 * Único punto del sistema que lee el {@link org.springframework.security.core.context.SecurityContext}.
 *
 * <p>Los servicios de dominio no acceden directamente al SecurityContext —
 * reciben un {@link ContextoUsuario} ya resuelto o llaman a este servicio.
 * Esto facilita las pruebas unitarias y mantiene la lógica de seguridad centralizada.
 */
@Service
public class ContextoUsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final MatriculaRepository matriculaRepository;

    public ContextoUsuarioService(UsuarioRepository usuarioRepository,
                                  MatriculaRepository matriculaRepository) {
        this.usuarioRepository = usuarioRepository;
        this.matriculaRepository = matriculaRepository;
    }

    /**
     * Construye el {@link ContextoUsuario} del usuario autenticado en el request actual.
     * <ul>
     *   <li>ADMIN / COORDINADOR → sin restricciones ({@code esAdmin = true}, sets vacíos).</li>
     *   <li>DOCENTE → carga sus secciones y materias asignadas desde BD.</li>
     * </ul>
     */
    @Transactional(readOnly = true)
    public ContextoUsuario obtenerContextoActual() {
        UsuarioPrincipal principal = (UsuarioPrincipal)
                SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Long usuarioId = principal.getUsuario().getId();
        RolNombre rol  = RolNombre.fromString(principal.getUsuario().getRol().getNombre());

        if (rol != RolNombre.DOCENTE) {
            return new ContextoUsuario(usuarioId, rol, true, Set.of(), Set.of());
        }

        Set<Long>    seccionIds  = usuarioRepository.findSeccionIdsByUsuarioId(usuarioId);
        Set<Integer> materiaIds  = usuarioRepository.findMateriaIdsByUsuarioId(usuarioId);

        return new ContextoUsuario(usuarioId, rol, false, seccionIds, materiaIds);
    }

    /**
     * Lanza {@link AccesoDenegadoException} si el estudiante no tiene matrícula
     * en ninguna de las secciones asignadas al usuario.
     * Si el usuario es admin, el acceso siempre se permite.
     */
    public void verificarAccesoAlEstudiante(Long estudianteId, ContextoUsuario contexto) {
        if (contexto.esAdmin()) return;

        boolean tieneAcceso = matriculaRepository
                .existsByEstudianteIdAndSeccionIdIn(estudianteId, contexto.seccionIds());

        if (!tieneAcceso) {
            throw new AccesoDenegadoException(
                    "No tiene acceso al estudiante ID: " + estudianteId);
        }
    }
}
