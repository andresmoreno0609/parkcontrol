package com.parkcontrol.common.dto.ingreso;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IngresoResponse {
    private Long id;
    private String numeroRegistro;
    private String placa;
    private LocalDate fechaIngreso;
    private LocalTime horaIngreso;
    private String tipoAcceso;
    private String cupoAsignado;
    private String estado;
    private VehiculoResponse vehiculo;
    private ConductorResponse conductor;
    private String usuarioRegistro;
}