package com.parkcontrol.external.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO que representa el formato REAL del sistema externo de vehículos.
 * 
 * IMPORTANTE: Esta clase debe reflejar exactamente los campos que retorna el externo.
 * Cuando el formato del externo cambie, solo se modifica esta clase.
 * 
 * Si el externo retorna campos diferentes, ajustar los nombres aquí.
 */
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExternalVehiculoData {
    
    // Ejemplo de campos que podría retornar el sistema externo:
    // (ajustar según el formato real del API)
    
    private String id;
    private String placaVehiculo;
    private String marcaVehiculo;
    private Integer anioVehiculo;
    private String lineaVehiculo;
    private String colorVehiculo;
    
    // Campo que indica si la consulta fue exitosa (algunos APIs lo usan)
    private Boolean success;
    private String mensaje;
}