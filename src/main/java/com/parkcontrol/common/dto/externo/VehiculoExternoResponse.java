package com.parkcontrol.common.dto.externo;

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
public class VehiculoExternoResponse {
    private boolean encontrado;
    private String placa;
    private String marca;
    private Integer modelo;
    private String referencia;
}