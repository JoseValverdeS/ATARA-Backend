package com.atara.deb.ataraapi.service;

import com.atara.deb.ataraapi.dto.alerta.AlertaResponseDto;
import java.util.List;

public interface AlertaService {

    List<AlertaResponseDto> getAlertsByStudent(Long studentId, Long periodId);

    List<AlertaResponseDto> getAlertsBySection(Long sectionId, Long periodId);
}