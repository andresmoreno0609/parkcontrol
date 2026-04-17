package com.parkcontrol.common.dto.ingreso;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Builder
public record IngresoResponse(
    Long id,
    String numeroRegistro,
    String placa,
    LocalDate fechaIngreso,
    LocalTime horaIngreso,
    String tipoAcceso,
    String cupoAsignado,
    String estado,
    VehiculoResponse vehiculo,
    ConductorResponse conductor,
    String usuarioRegistro
) {}