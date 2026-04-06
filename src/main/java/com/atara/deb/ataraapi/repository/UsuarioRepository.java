package com.atara.deb.ataraapi.repository;

import com.atara.deb.ataraapi.model.Usuario;
import com.atara.deb.ataraapi.model.enums.EstadoUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByCorreo(String correo);

    boolean existsByCorreo(String correo);

    List<Usuario> findByEstado(EstadoUsuario estado);

    /** IDs de las secciones asignadas al usuario vía usuarios_secciones. */
    @Query("SELECT s.id FROM Usuario u JOIN u.seccionesAsignadas s WHERE u.id = :usuarioId")
    Set<Long> findSeccionIdsByUsuarioId(@Param("usuarioId") Long usuarioId);

    /** IDs de las materias asignadas al usuario vía usuario_materias. */
    @Query("SELECT m.id FROM Usuario u JOIN u.materiasAsignadas m WHERE u.id = :usuarioId")
    Set<Integer> findMateriaIdsByUsuarioId(@Param("usuarioId") Long usuarioId);
}
