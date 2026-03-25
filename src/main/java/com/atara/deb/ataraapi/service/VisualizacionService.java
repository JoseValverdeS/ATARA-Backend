package com.atara.deb.ataraapi.service;

import com.atara.deb.ataraapi.dto.visualizacion.GraficoValorDto;
import java.util.List;

public interface VisualizacionService {

    List<GraficoValorDto> getAchievementDistributionBySection(Long sectionId, Long subjectId, Long periodId);
}