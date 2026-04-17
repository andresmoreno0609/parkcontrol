package com.parkcontrol.common.dto.ingreso;

public record InventarioExteriorRequest(
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