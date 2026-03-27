package com.atara.deb.ataraapi.repository;

import com.atara.deb.ataraapi.model.TokenRefresh;
import com.atara.deb.ataraapi.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TokenRefreshRepository extends JpaRepository<TokenRefresh, Long> {

    Optional<TokenRefresh> findByTokenHash(String tokenHash);

    @Modifying
    @Query("UPDATE TokenRefresh t SET t.revocado = true, t.revocadoEn = CURRENT_TIMESTAMP " +
           "WHERE t.usuario = :usuario AND t.revocado = false")
    void revocarTodosPorUsuario(@Param("usuario") Usuario usuario);
}
