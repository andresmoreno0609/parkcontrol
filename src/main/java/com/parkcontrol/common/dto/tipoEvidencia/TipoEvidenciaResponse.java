package com.parkcontrol.common.dto.tipoEvidencia;

@Builder
public record TipoEvidenciaResponse(
    Long id,
    String nombre,
    String descripcion,
    Integer orden
) {}