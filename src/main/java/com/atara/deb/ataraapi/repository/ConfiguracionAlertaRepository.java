package com.atara.deb.ataraapi.repository;

import com.atara.deb.ataraapi.model.ConfiguracionAlerta;
import com.atara.deb.ataraapi.model.enums.TipoAlerta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConfiguracionAlertaRepository extends JpaRepository<ConfiguracionAlerta, Long> {

    List<ConfiguracionAlerta> findByTipoAlerta(TipoAlerta tipoAlerta);

    // Configuraciones que aplican al promedio global (sin dimensión específica)
    List<ConfiguracionAlerta> findByDimensionIsNull();
}
