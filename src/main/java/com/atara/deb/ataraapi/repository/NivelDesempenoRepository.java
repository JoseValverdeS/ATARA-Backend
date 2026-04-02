package com.atara.deb.ataraapi.repository;

import com.atara.deb.ataraapi.model.NivelDesempeno;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NivelDesempenoRepository extends JpaRepository<NivelDesempeno, Integer> {
    List<NivelDesempeno> findAllByOrderByValorNumericoAsc();
}
