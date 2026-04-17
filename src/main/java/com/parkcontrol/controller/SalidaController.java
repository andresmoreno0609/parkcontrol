package com.parkcontrol.controller;

import com.parkcontrol.common.dto.salida.SalidaDetalleResponse;
import com.parkcontrol.common.dto.salida.SalidaRequest;
import com.parkcontrol.common.dto.salida.SalidaResponse;
import com.parkcontrol.common.service.SalidaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/salidas")
@RequiredArgsConstructor
@Tag(name = "Salidas", description = "Gestión de salidas de vehículos")
public class SalidaController {

    private final SalidaService salidaService;

    @PostMapping
    @Operation(summary = "Registrar salida", description = "Registra la salida de un vehículo")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Salida registrada"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos o ingreso no activo")
    })
    public ResponseEntity<SalidaResponse> create(
            @RequestBody SalidaRequest request,
            @RequestHeader("X-Usuario") String username) {

        SalidaResponse response = salidaService.create(request, username);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener detalle de salida", description = "Retorna el detalle completo de una salida")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Detalle de la salida"),
        @ApiResponse(responseCode = "404", description = "Salida no encontrada")
    })
    public ResponseEntity<SalidaDetalleResponse> findById(@PathVariable Long id) {
        SalidaDetalleResponse response = salidaService.findDetalleById(id);
        return ResponseEntity.ok(response);
    }
}