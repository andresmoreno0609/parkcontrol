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
public class InventarioSeguridadResponse {
    private Long id;
    private Boolean llantaRepuesto;
    private Boolean gato;
    private Boolean cruceta;
    private Boolean extintor;
    private Boolean botiquin;
    private Boolean triangulos;
    private Boolean herramientas;
    private String otros;
}