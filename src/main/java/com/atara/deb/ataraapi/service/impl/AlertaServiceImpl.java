package com.atara.deb.ataraapi.service.impl;

import com.atara.deb.ataraapi.dto.alerta.AlertaResponseDto;
import com.atara.deb.ataraapi.service.AlertaService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AlertaServiceImpl implements AlertaService {

    @Override
    public List<AlertaResponseDto> getAlertsByStudent(Long studentId, Long periodId) {
        return new ArrayList<>();
    }

    @Override
    public List<AlertaResponseDto> getAlertsBySection(Long sectionId, Long periodId) {
        return new ArrayList<>();
    }
}