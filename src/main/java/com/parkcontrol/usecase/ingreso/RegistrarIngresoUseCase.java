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
        if (request.getPlaca() == null || request.getPlaca().isBlank()) {
            throw new IllegalArgumentException("La placa es requerida");
        }
        if (request.getTipoVehiculo() == null || request.getTipoVehiculo().isBlank()) {
            throw new IllegalArgumentException("El tipo de vehículo es requerido");
        }
        if (request.getMarca() == null || request.getMarca().isBlank()) {
            throw new IllegalArgumentException("La marca es requerida");
        }
        if (request.getModelo() == null) {
            throw new IllegalArgumentException("El modelo es requerido");
        }
        if (request.getTipoDocumentoConductor() == null || request.getTipoDocumentoConductor().isBlank()) {
            throw new IllegalArgumentException("El tipo de documento del conductor es requerido");
        }
        if (request.getNumeroDocumentoConductor() == null || request.getNumeroDocumentoConductor().isBlank()) {
            throw new IllegalArgumentException("El número de documento del conductor es requerido");
        }
        if (request.getTipoAcceso() == null || request.getTipoAcceso().isBlank()) {
            throw new IllegalArgumentException("El tipo de acceso es requerido");
        }
        if (request.getFirmaConductor() == null || !request.getFirmaConductor()) {
            throw new IllegalArgumentException("La firma del conductor es requerida");
        }
        if (request.getFirmaOperario() == null || !request.getFirmaOperario()) {
            throw new IllegalArgumentException("La firma del operario es requerida");
        }

        // Core - Crear ingreso
        return ingresoService.create(request, username);
    }
}