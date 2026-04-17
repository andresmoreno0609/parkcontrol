package com.parkcontrol.common.dto.tipoEvidencia;

public record CrearTipoEvidenciaRequest(
    String nombre,
    String descripcion,
    Integer orden
) {}