package com.atara.deb.ataraapi.controller;

import com.atara.deb.ataraapi.dto.reporte.ReporteEstudianteDto;
import com.atara.deb.ataraapi.service.ReporteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reportes")
public class ReporteController {

    private final ReporteService reporteService;

    public ReporteController(ReporteService reporteService) {
        this.reporteService = reporteService;
    }

    @GetMapping("/estudiante/{studentId}")
    public ResponseEntity<ReporteEstudianteDto> getStudentReport(
            @PathVariable Long studentId,
            @RequestParam Long subjectId,
            @RequestParam Long periodId) {
        return ResponseEntity.ok(reporteService.getStudentReport(studentId, subjectId, periodId));
    }
}