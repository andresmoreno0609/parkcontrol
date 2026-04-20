package com.parkcontrol.external.mapper;

import com.parkcontrol.common.dto.externo.VehiculoExternoResponse;
import com.parkcontrol.external.dto.ExternalVehiculoData;

/**
 * Mapper para transformar datos del sistema externo al DTO interno de vehículo.
 * 
 * ACÁ ESTÁ LA LÓGICA DE MAPEO - cuando cambie el formato del externo,
 * solo se modifica este método.
 */
public class VehiculoMapper {

    /**
     * Mapea ExternalVehiculoData → VehiculoExternoResponse
     * 
     * Ajustar los campos según el formato REAL del sistema externo.
     * El DTO interno (VehiculoExternoResponse) tiene estos campos:
     * - encontrado (boolean)
     * - placa
     * - marca
     * - modelo
     * - referencia
     */
    public VehiculoExternoResponse mapToResponse(ExternalVehiculoData data) {
        if (data == null) {
            return VehiculoExternoResponse.builder()
                    .encontrado(false)
                    .build();
        }

        // Verificar si la respuesta del externo es válida
        boolean esValido = data.getSuccess() != null && data.getSuccess()
                || data.getPlacaVehiculo() != null;

        if (!esValido) {
            return VehiculoExternoResponse.builder()
                    .encontrado(false)
                    .build();
        }

        // MAPEO: ajustar según los nombres reales de campos del externo
        return VehiculoExternoResponse.builder()
                .encontrado(true)
                .placa(data.getPlacaVehiculo())              // externo: placaVehiculo → interno: placa
                .marca(data.getMarcaVehiculo())             // externo: marcaVehiculo → interno: marca
                .modelo(data.getAnioVehiculo())              // externo: anioVehiculo → interno: modelo
                .referencia(data.getLineaVehiculo())        // externo: lineaVehiculo → interno: referencia
                .build();
    }
}