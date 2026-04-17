package com.parkcontrol.common.dto.ingreso;

import java.util.List;

public record IngresoRequest(
    // Datos del vehiculo
    String placa,
    String tipoVehiculo,
    String marca,
    Integer modelo,
    String referencia,
    String color,
    String servicio,

    // Datos del conductor
    String tipoDocumentoConductor,
    String numeroDocumentoConductor,

    // Info parqueadero
    String tipoAcceso,
    String cupoAsignado,
    String kilometraje,
    String motivoIngreso,
    String observaciones,
    Boolean firmaConductor,
    Boolean firmaOperario,

    // Inventarios
    InventarioExteriorRequest inventarioExterior,
    InventarioInteriorRequest inventarioInterior,
    InventarioSeguridadRequest inventarioSeguridad,

    // Evidencias
    List<EvidenciaRequest> evidencias
) {}