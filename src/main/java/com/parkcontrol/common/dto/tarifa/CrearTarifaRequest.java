package com.parkcontrol.common.dto.tarifa;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CrearTarifaRequest {
    private String tipoVehiculo;
    private String tipoAcceso;
    private BigDecimal valor;
}