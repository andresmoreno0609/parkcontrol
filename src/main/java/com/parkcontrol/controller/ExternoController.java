package com.parkcontrol.controller;

import com.parkcontrol.common.dto.externo.PersonaExternoResponse;
import com.parkcontrol.common.dto.externo.VehiculoExternoResponse;
import com.parkcontrol.external.PersonaExternalClient;
import com.parkcontrol.external.VehiculoExternalClient;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/externo")
@RequiredArgsConstructor
@Tag(name = "Consultas Externas", description = "APIs para consultar sistemas externos de personas y vehículos")
public class ExternoController {

    private final PersonaExternalClient personaClient;
    private final VehiculoExternalClient vehiculoClient;

    @GetMapping("/persona")
    @Operation(
            summary = "Buscar persona por documento",
            description = "Consulta datos de persona en sistema externo por tipo y número de documento"
    )
    public ResponseEntity<PersonaExternoResponse> buscarPersona(
            @RequestParam String tipoDocumento,
            @RequestParam String numeroDocumento
    ) {
        PersonaExternoResponse response = personaClient.buscarPorDocumento(tipoDocumento, numeroDocumento);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/vehiculo")
    @Operation(
            summary = "Buscar vehículo por placa",
            description = "Consulta datos de vehículo en sistema externo por placa"
    )
    public ResponseEntity<VehiculoExternoResponse> buscarVehiculo(
            @RequestParam String placa
    ) {
        VehiculoExternoResponse response = vehiculoClient.buscarPorPlaca(placa);
        return ResponseEntity.ok(response);
    }
}