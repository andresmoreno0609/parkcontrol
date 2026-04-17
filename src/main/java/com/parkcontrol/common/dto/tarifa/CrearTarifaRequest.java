package com.parkcontrol.common.dto.tarifa;

import java.math.BigDecimal;

public record CrearTarifaRequest(
    String tipoVehiculo,
    String tipoAcceso,
    BigDecimal valor
) {}