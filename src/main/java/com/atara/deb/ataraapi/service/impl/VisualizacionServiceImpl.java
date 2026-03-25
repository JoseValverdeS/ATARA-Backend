package com.atara.deb.ataraapi.service.impl;

import com.atara.deb.ataraapi.dto.visualizacion.GraficoValorDto;
import com.atara.deb.ataraapi.service.VisualizacionService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VisualizacionServiceImpl implements VisualizacionService {

    @Override
    public List<GraficoValorDto> getAchievementDistributionBySection(Long sectionId, Long subjectId, Long periodId) {
        throw new UnsupportedOperationException("Visualización de datos no implementada aún.");
    }
}