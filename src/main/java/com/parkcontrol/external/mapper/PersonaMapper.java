package com.parkcontrol.external.mapper;

import com.parkcontrol.common.dto.externo.PersonaExternoResponse;
import com.parkcontrol.external.dto.ExternalPersonaData;

/**
 * Mapper para transformar datos del sistema externo al DTO interno de persona.
 * 
 * ACÁ ESTÁ LA LÓGICA DE MAPEO - cuando cambie el formato del externo,
 * solo se modifica este método.
 */
public class PersonaMapper {

    /**
     * Mapea ExternalPersonaData → PersonaExternoResponse
     * 
     * Ajustar los campos según el formato REAL del sistema externo.
     * El DTO interno (PersonaExternoResponse) tiene estos campos:
     * - encontrado (boolean)
     * - nombres
     * - tipoDocumento
     * - numeroDocumento
     * - correo
     * - telefono
     * - sexo
     * - fechaNacimiento
     */
    public PersonaExternoResponse mapToResponse(ExternalPersonaData data) {
        if (data == null) {
            return PersonaExternoResponse.builder()
                    .encontrado(false)
                    .build();
        }

        // Verificar si la respuesta del externo es válida
        // Algunos APIs usan "success", otros retornan null en campos clave
        boolean esValido = data.getSuccess() != null && data.getSuccess()
                || data.getNumeroIdentificacion() != null;

        if (!esValido) {
            return PersonaExternoResponse.builder()
                    .encontrado(false)
                    .build();
        }

        // MAPEO: ajustar según los nombres reales de campos del externo
        return PersonaExternoResponse.builder()
                .encontrado(true)
                .nombres(data.getNombreCompleto())           // externo: nombreCompleto → interno: nombres
                .tipoDocumento(data.getTipoIdentificacion()) // externo: tipoIdentificacion → interno: tipoDocumento
                .numeroDocumento(data.getNumeroIdentificacion())
                .correo(data.getEmail())                    // externo: email → interno: correo
                .telefono(data.getCelular())                // externo: celular → interno: telefono
                .sexo(data.getGenero())                     // externo: genero → interno: sexo
                .fechaNacimiento(data.getFechaNacimiento())
                .build();
    }
}