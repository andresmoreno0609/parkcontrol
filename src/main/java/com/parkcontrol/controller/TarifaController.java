package com.parkcontrol.controller;

import com.parkcontrol.common.dto.tarifa.CrearTarifaRequest;
import com.parkcontrol.common.dto.tarifa.TarifaResponse;
import com.parkcontrol.common.service.TarifaService;
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
@RequestMapping("/api/v1/tarifas")
@RequiredArgsConstructor
@Tag(name = "Tarifas", description = "Gestión de tarifas del parqueadero")
public class TarifaController {

    private final TarifaService tarifaService;

    @GetMapping
    @Operation(summary = "Listar tarifas", description = "Lista todas las tarifas activas")
    @ApiResponse(responseCode = "200", description = "Lista de tarifas")
    public ResponseEntity<List<TarifaResponse>> findAll() {
        List<TarifaResponse> response = tarifaService.findAllActivas();
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @Operation(summary = "Crear tarifa", description = "Crea una nueva tarifa (solo ADMIN)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Tarifa creada"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    public ResponseEntity<TarifaResponse> create(@RequestBody CrearTarifaRequest request) {
        TarifaResponse response = tarifaService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}