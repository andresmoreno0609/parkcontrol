package com.parkcontrol.common.dto.salida;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SalidaResponse {
    private Long id;
    private Long ingresoId;
    private LocalDate fechaSalida;
    private LocalTime horaSalida;
    private String personaRetira;
    private String documentoRetira;
    private BigDecimal montoPagado;
    private String estadoEntrega;
    private String observaciones;
}