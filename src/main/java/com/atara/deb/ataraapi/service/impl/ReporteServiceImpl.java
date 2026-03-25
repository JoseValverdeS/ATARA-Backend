package com.atara.deb.ataraapi.service.impl;

import com.atara.deb.ataraapi.dto.reporte.ReporteEstudianteDto;
import com.atara.deb.ataraapi.service.ReporteService;
import org.springframework.stereotype.Service;

@Service
public class ReporteServiceImpl implements ReporteService {

    @Override
    public ReporteEstudianteDto getStudentReport(Long studentId, Long subjectId, Long periodId) {
        return new ReporteEstudianteDto();
    }
}