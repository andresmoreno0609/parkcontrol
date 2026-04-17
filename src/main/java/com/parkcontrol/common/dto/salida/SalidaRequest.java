package com.parkcontrol.common.dto.salida;

import com.parkcontrol.common.dto.ingreso.EvidenciaRequest;
import com.parkcontrol.common.dto.ingreso.InventarioExteriorRequest;
import com.parkcontrol.common.dto.ingreso.InventarioInteriorRequest;
import com.parkcontrol.common.dto.ingreso.InventarioSeguridadRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SalidaRequest {
    private Long ingresoId;
    private String personaRetira;
    private String documentoRetira;
    private String observaciones;

    private InventarioExteriorRequest inventarioExterior;
    private InventarioInteriorRequest inventarioInterior;
    private InventarioSeguridadRequest inventarioSeguridad;

    private List<EvidenciaRequest> evidencias;
}