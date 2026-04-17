package com.parkcontrol.common.dto.salida;

import com.parkcontrol.common.dto.ingreso.*;
import java.util.List;

public record SalidaRequest(
    Long ingresoId,
    String personaRetira,
    String documentoRetira,
    String observaciones,

    InventarioExteriorRequest inventarioExterior,
    InventarioInteriorRequest inventarioInterior,
    InventarioSeguridadRequest inventarioSeguridad,

    List<EvidenciaRequest> evidencias
) {}