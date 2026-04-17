package com.parkcontrol.common.dto.ingreso;

@Builder
public record InventarioExteriorResponse(
    Long id,
    Boolean parachoquesDelantero,
    Boolean parachoquesTrasero,
    Boolean puertas,
    Boolean espejos,
    Boolean vidrios,
    Boolean luces,
    Boolean llantas,
    Boolean rayones,
    Boolean golpes,
    String observaciones
) {}