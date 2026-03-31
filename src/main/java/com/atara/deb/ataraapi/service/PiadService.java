package com.atara.deb.ataraapi.service;

import com.atara.deb.ataraapi.dto.piad.EstudiantePIADDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PiadService {
    List<EstudiantePIADDto> extraerEstudiantes(MultipartFile archivo) throws Exception;
}
