package com.parkcontrol.common.dto.ingreso;

@Builder
public record VehiculoResponse(
    Long id,
    String placa,
    String marca,
    Integer modelo,
    String referencia
) {}