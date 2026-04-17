package com.parkcontrol.common.dto.ingreso;

@Builder
public record EvidenciaResponse(
    Long id,
    Long tipoId,
    String rutaArchivo,
    String tipoNombre
) {}