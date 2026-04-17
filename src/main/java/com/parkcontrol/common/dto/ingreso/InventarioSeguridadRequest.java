package com.parkcontrol.common.dto.ingreso;

public record InventarioSeguridadRequest(
    Boolean llantaRepuesto,
    Boolean gato,
    Boolean cruceta,
    Boolean extintor,
    Boolean botiquin,
    Boolean triangulos,
    Boolean herramientas,
    String otros
) {}