package com.parkcontrol.external.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO que representa el formato REAL del sistema externo de personas.
 * 
 * IMPORTANTE: Esta clase debe reflejar exactamente los campos que retorna el externo.
 * Cuando el formato del externo cambié, solo se modifica esta clase.
 * 
 * Si el externo retorna campos diferentes, ajustar los nombres aquí.
 */
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExternalPersonaData {
    
    // Ejemplo de campos que podría retornar el sistema externo:
    // (adjustar según el formato real del API)
    
    private String id;
    private String nombreCompleto;
    private String tipoIdentificacion;
    private String numeroIdentificacion;
    private String email;
    private String celular;
    private String genero;
    private String fechaNacimiento;
    
    // Campo que indica si la consulta fue exitosa (algunos APIs lo usan)
    private Boolean success;
    private String mensaje;
}