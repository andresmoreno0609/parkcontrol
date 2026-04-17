package com.parkcontrol.common.dto.ingreso;

@Builder
public record InventarioSeguridadResponse(
    Long id,
    Boolean llantaRepuesto,
    Boolean gato,
    Boolean cruceta,
    Boolean extintor,
    Boolean botiquin,
    Boolean triangulos,
    Boolean herramientas,
    String otros
) {}