package com.parkcontrol.common.dto.ingreso;

@Builder
public record InventarioInteriorResponse(
    Long id,
    Boolean tapiceria,
    Boolean tablero,
    Boolean radioPantalla,
    Boolean alfombras,
    Boolean cinturones,
    Boolean elementosPersonales,
    String observaciones
) {}