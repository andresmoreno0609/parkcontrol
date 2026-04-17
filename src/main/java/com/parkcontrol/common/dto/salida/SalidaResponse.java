package com.parkcontrol.common.dto.salida;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Builder
public record SalidaResponse(
    Long id,
    Long ingresoId,
    LocalDate fechaSalida,
    LocalTime horaSalida,
    String personaRetira,
    String documentoRetira,
    BigDecimal montoPagado,
    String estadoEntrega,
    String observaciones
) {}