package com.parkcontrol.controller;

import com.parkcontrol.common.dto.tipoEvidencia.CrearTipoEvidenciaRequest;
import com.parkcontrol.common.dto.tipoEvidencia.TipoEvidenciaResponse;
import com.parkcontrol.common.service.TipoEvidenciaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tipos-evidencia")
@RequiredArgsConstructor
@Tag(name = "Tipos de Evidencia", description = "Gestión de tipos de evidencia")
public class TipoEvidenciaController {

    private final TipoEvidenciaService tipoEvidenciaService;

    @GetMapping
    @Operation(summary = "Listar tipos de evidencia", description = "Lista todos los tipos de evidencia activos")
    @ApiResponse(responseCode = "200", description = "Lista de tipos de evidencia")
    public ResponseEntity<List<TipoEvidenciaResponse>> findAll() {
        List<TipoEvidenciaResponse> response = tipoEvidenciaService.findAllActivos();
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @Operation(summary = "Crear tipo de evidencia", description = "Crea un nuevo tipo de evidencia (solo ADMIN)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Tipo de evidencia creado"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    public ResponseEntity<TipoEvidenciaResponse> create(@RequestBody CrearTipoEvidenciaRequest request) {
        TipoEvidenciaResponse response = tipoEvidenciaService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}