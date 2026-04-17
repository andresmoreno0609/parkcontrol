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
public class InventarioInteriorRequest {
    private Boolean tapiceria;
    private Boolean tablero;
    private Boolean radioPantalla;
    private Boolean alfombras;
    private Boolean cinturones;
    private Boolean elementosPersonales;
    private String observaciones;
}