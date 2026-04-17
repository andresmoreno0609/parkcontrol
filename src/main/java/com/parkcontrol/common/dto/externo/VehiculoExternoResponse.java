package com.parkcontrol.common.dto.externo;

@Builder
public record VehiculoExternoResponse(
    boolean encontrado,
    String placa,
    String marca,
    Integer modelo,
    String referencia
) {}