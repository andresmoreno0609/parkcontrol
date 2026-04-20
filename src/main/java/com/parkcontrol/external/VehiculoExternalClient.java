package com.parkcontrol.external;

import com.parkcontrol.common.dto.externo.VehiculoExternoResponse;
import com.parkcontrol.config.ExternalApiProperties;
import com.parkcontrol.external.dto.ExternalVehiculoData;
import com.parkcontrol.external.mapper.VehiculoMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * Cliente para consultar datos de vehículos en sistema externo.
 * 
 * El flujo es:
 * 1. Llama al sistema externo y obtiene ExternalVehiculoData
 * 2. Usa VehiculoMapper para transformar al DTO interno
 * 
 * El flag app.externo.vehiculo-enabled controla si se consulta el externo.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class VehiculoExternalClient {

    private final ExternalApiProperties properties;
    private final RestTemplate restTemplate;
    private final VehiculoMapper vehiculoMapper;

    /**
     * Busca un vehículo por placa.
     * Si vehiculoEnabled=false, retorna no encontrado directamente.
     */
    public VehiculoExternoResponse buscarPorPlaca(String placas) {
        // Si está deshabilitado, retornar no encontrado
        if (!properties.isVehiculoEnabled()) {
            log.debug("Consulta externa de vehículo deshabilitada");
            return VehiculoExternoResponse.builder()
                    .encontrado(false)
                    .build();
        }

        try {
            // Construir URL con query params
            String url = buildUrl(placas);
            log.debug("Consultando vehículo externo: {}", url);

            // Llamar al servicio externo y obtener el formato externo
            ExternalVehiculoData externalData = restTemplate.getForObject(url, ExternalVehiculoData.class);

            // Mapear al DTO interno usando el mapper
            return vehiculoMapper.mapToResponse(externalData);

        } catch (Exception e) {
            log.warn("Error consultando sistema externo de vehículos: {}", e.getMessage());
            // En caso de error, retornamos no encontrado (fallback seguro)
            return VehiculoExternoResponse.builder().encontrado(false).build();
        }
    }

    private String buildUrl(String placa) {
        String baseUrl = properties.getVehiculoUrl();
        // Agregar query params
        String separator = baseUrl.contains("?") ? "&" : "?";
        return baseUrl + separator + "placa=" + placa;
    }
}