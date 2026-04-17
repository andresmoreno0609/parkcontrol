package com.parkcontrol.common.dto.ingreso;

public record EvidenciaRequest(
    Long tipoId,
    String rutaArchivo
) {}