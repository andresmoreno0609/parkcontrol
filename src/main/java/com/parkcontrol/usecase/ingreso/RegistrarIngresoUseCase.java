package com.parkcontrol.usecase.ingreso;

import com.parkcontrol.common.dto.ingreso.IngresoRequest;
import com.parkcontrol.common.dto.ingreso.IngresoResponse;
import com.parkcontrol.common.service.IngresoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RegistrarIngresoUseCase {

    private final IngresoService ingresoService;

    public IngresoResponse execute(IngresoRequest request, String username) {
        // PreConditions - Validaciones
        if (request.placa() == null || request.placa().isBlank()) {
            throw new IllegalArgumentException("La placa es requerida");
        }
        if (request.tipoVehiculo() == null || request.tipoVehiculo().isBlank()) {
            throw new IllegalArgumentException("El tipo de vehículo es requerido");
        }
        if (request.marca() == null || request.marca().isBlank()) {
            throw new IllegalArgumentException("La marca es requerida");
        }
        if (request.modelo() == null) {
            throw new IllegalArgumentException("El modelo es requerido");
        }
        if (request.tipoDocumentoConductor() == null || request.tipoDocumentoConductor().isBlank()) {
            throw new IllegalArgumentException("El tipo de documento del conductor es requerido");
        }
        if (request.numeroDocumentoConductor() == null || request.numeroDocumentoConductor().isBlank()) {
            throw new IllegalArgumentException("El número de documento del conductor es requerido");
        }
        if (request.tipoAcceso() == null || request.tipoAcceso().isBlank()) {
            throw new IllegalArgumentException("El tipo de acceso es requerido");
        }
        if (request.firmaConductor() == null || !request.firmaConductor()) {
            throw new IllegalArgumentException("La firma del conductor es requerida");
        }
        if (request.firmaOperario() == null || !request.firmaOperario()) {
            throw new IllegalArgumentException("La firma del operario es requerida");
        }

        // Core - Crear ingreso
        return ingresoService.create(request, username);
    }
}