package com.parkcontrol.common.dto.salida;

import com.parkcontrol.common.dto.ingreso.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Builder
public record SalidaDetalleResponse(
    Long id,
    Long ingresoId,
    String numeroRegistro,
    String placa,
    LocalDate fechaIngreso,
    LocalTime horaIngreso,
    LocalDate fechaSalida,
    LocalTime horaSalida,
    String personaRetira,
    String documentoRetira,
    String tiempoTotal,
    BigDecimal montoPagado,
    String observaciones,
    List<EvidenciaResponse> evidencias,
    String usuarioRegistro
) {}