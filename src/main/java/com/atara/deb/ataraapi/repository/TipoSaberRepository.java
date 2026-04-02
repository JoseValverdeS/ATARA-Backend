package com.atara.deb.ataraapi.repository;

import com.atara.deb.ataraapi.model.TipoSaber;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TipoSaberRepository extends JpaRepository<TipoSaber, Integer> {
    Optional<TipoSaber> findByClave(String clave);
}
