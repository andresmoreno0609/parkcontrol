package com.parkcontrol.common.dto.ingreso;

@Builder
public record ConductorResponse(
    Long id,
    String nombres,
    String tipoDocumento,
    String numeroDocumento
) {}