package com.parkcontrol.external;

import com.parkcontrol.common.dto.externo.PersonaExternoResponse;
import com.parkcontrol.config.ExternalApiProperties;
import com.parkcontrol.external.dto.ExternalPersonaData;
import com.parkcontrol.external.mapper.PersonaMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * Cliente para consultar datos de personas en sistema externo.
 * 
 * El flujo es:
 * 1. Llama al sistema externo y obtiene ExternalPersonaData
 * 2. Usa PersonaMapper para transformar al DTO interno
 * 
 * El flag app.externo.persona-enabled controla si se consulta el externo.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PersonaExternalClient {

    private final ExternalApiProperties properties;
    private final RestTemplate restTemplate;
    private final PersonaMapper personaMapper;

    /**
     * Busca una persona por tipo y número de documento.
     * Si personaEnabled=false, retorna no encontrado directamente.
     */
    public PersonaExternoResponse buscarPorDocumento(String tipoDocumento, String numeroDocumento) {
        // Si está deshabilitado, retornar no encontrado
        if (!properties.isPersonaEnabled()) {
            log.debug("Consulta externa de persona deshabilitada");
            return PersonaExternoResponse.builder()
                    .encontrado(false)
                    .build();
        }

        try {
            // Construir URL con query params
            String url = buildUrl(tipoDocumento, numeroDocumento);
            log.debug("Consultando persona externa: {}", url);

            // Llamar al servicio externo y obtener el formato externo
            ExternalPersonaData externalData = restTemplate.getForObject(url, ExternalPersonaData.class);

            // Mapear al DTO interno usando el mapper
            return personaMapper.mapToResponse(externalData);

        } catch (Exception e) {
            log.warn("Error consultando sistema externo de personas: {}", e.getMessage());
            // En caso de error, retornamos no encontrado (fallback seguro)
            return PersonaExternoResponse.builder().encontrado(false).build();
        }
    }

    private String buildUrl(String tipoDocumento, String numeroDocumento) {
        String baseUrl = properties.getPersonaUrl();
        // Agregar query params
        String separator = baseUrl.contains("?") ? "&" : "?";
        return baseUrl + separator + "tipo=" + tipoDocumento + "&numero=" + numeroDocumento;
    }
}