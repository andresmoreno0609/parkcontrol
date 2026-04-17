package com.parkcontrol.common.dto.tarifa;

import java.math.BigDecimal;

@Builder
public record TarifaResponse(
    Long id,
    String tipoVehiculo,
    String tipoAcceso,
    BigDecimal valor,
    Boolean activo
) {}