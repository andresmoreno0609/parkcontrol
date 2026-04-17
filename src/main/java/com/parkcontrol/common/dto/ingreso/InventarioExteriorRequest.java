package com.parkcontrol.common.dto.ingreso;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InventarioExteriorRequest {
    private Boolean parachoquesDelantero;
    private Boolean parachoquesTrasero;
    private Boolean puertas;
    private Boolean espejos;
    private Boolean vidrios;
    private Boolean luces;
    private Boolean llantas;
    private Boolean rayones;
    private Boolean golpes;
    private String observaciones;
}