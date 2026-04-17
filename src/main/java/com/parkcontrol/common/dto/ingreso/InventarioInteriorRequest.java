package com.parkcontrol.common.dto.ingreso;

public record InventarioInteriorRequest(
    Boolean tapiceria,
    Boolean tablero,
    Boolean radioPantalla,
    Boolean alfombras,
    Boolean cinturones,
    Boolean elementosPersonales,
    String observaciones
) {}