package com.parkcontrol.common.dto.ingreso;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.util.List;

@Builder
public record IngresoDetalleResponse(
    Long id,
    String numeroRegistro,
    String placa,
    LocalDate fechaIngreso,
    LocalTime horaIngreso,
    String tipoAcceso,
    String cupoAsignado,
    String kilometraje,
    String motivoIngreso,
    String observaciones,
    Boolean firmaConductor,
    Boolean firmaOperario,
    String estado,
    VehiculoResponse vehiculo,
    ConductorResponse conductor,
    InventarioExteriorResponse inventarioExterior,
    InventarioInteriorResponse inventarioInterior,
    InventarioSeguridadResponse inventarioSeguridad,
    List<EvidenciaResponse> evidencias,
    String usuarioRegistro,
    LocalDateTime createdAt
) {}