package com.atara.deb.ataraapi.controller;

import com.atara.deb.ataraapi.dto.visualizacion.GraficoValorDto;
import com.atara.deb.ataraapi.service.VisualizacionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/visualizaciones")
public class VisualizacionController {

    private final VisualizacionService visualizacionService;

    public VisualizacionController(VisualizacionService visualizacionService) {
        this.visualizacionService = visualizacionService;
    }

    @GetMapping("/seccion/{sectionId}/distribucion")
    public ResponseEntity<List<GraficoValorDto>> getDistribution(
            @PathVariable Long sectionId,
            @RequestParam Long subjectId,
            @RequestParam Long periodId) {
        return ResponseEntity.ok(
                visualizacionService.getAchievementDistributionBySection(sectionId, subjectId, periodId)
        );
    }
}