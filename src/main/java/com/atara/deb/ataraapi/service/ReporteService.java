package com.atara.deb.ataraapi.service;

import com.atara.deb.ataraapi.dto.reporte.ReporteEstudianteDto;

public interface ReporteService {

    ReporteEstudianteDto getStudentReport(Long studentId, Long subjectId, Long periodId);
}