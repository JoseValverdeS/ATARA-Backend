package com.atara.deb.ataraapi.controller;

import com.atara.deb.ataraapi.dto.alerta.AlertaResponseDto;
import com.atara.deb.ataraapi.service.AlertaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alertas")
public class AlertaController {

    private final AlertaService alertaService;

    public AlertaController(AlertaService alertaService) {
        this.alertaService = alertaService;
    }

    @GetMapping("/estudiante/{studentId}")
    public ResponseEntity<List<AlertaResponseDto>> getByStudent(
            @PathVariable Long studentId,
            @RequestParam Long periodId) {
        return ResponseEntity.ok(alertaService.getAlertsByStudent(studentId, periodId));
    }

    @GetMapping("/seccion/{sectionId}")
    public ResponseEntity<List<AlertaResponseDto>> getBySection(
            @PathVariable Long sectionId,
            @RequestParam Long periodId) {
        return ResponseEntity.ok(alertaService.getAlertsBySection(sectionId, periodId));
    }
}