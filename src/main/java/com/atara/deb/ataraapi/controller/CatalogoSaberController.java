package com.atara.deb.ataraapi.controller;

import com.atara.deb.ataraapi.dto.saber.EjeTemaaticoResponseDto;
import com.atara.deb.ataraapi.dto.saber.NivelDesempenoResponseDto;
import com.atara.deb.ataraapi.dto.saber.TipoSaberResponseDto;
import com.atara.deb.ataraapi.service.CatalogoSaberService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/catalogos/saberes")
public class CatalogoSaberController {

    private final CatalogoSaberService catalogoSaberService;

    public CatalogoSaberController(CatalogoSaberService catalogoSaberService) {
        this.catalogoSaberService = catalogoSaberService;
    }

    @GetMapping("/tipos")
    public ResponseEntity<List<TipoSaberResponseDto>> listarTiposSaber() {
        return ResponseEntity.ok(catalogoSaberService.listarTiposSaber());
    }

    @GetMapping("/ejes")
    public ResponseEntity<List<EjeTemaaticoResponseDto>> listarEjesTematicos(
            @RequestParam(required = false) Integer tipoSaberId) {
        if (tipoSaberId != null) {
            return ResponseEntity.ok(catalogoSaberService.listarEjesPorTipoSaber(tipoSaberId));
        }
        return ResponseEntity.ok(catalogoSaberService.listarEjesTematicos());
    }

    @GetMapping("/niveles-desempeno")
    public ResponseEntity<List<NivelDesempenoResponseDto>> listarNivelesDesempeno() {
        return ResponseEntity.ok(catalogoSaberService.listarNivelesDesempeno());
    }
}
