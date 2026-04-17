package com.parkcontrol.common.dto.ingreso;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IngresoDetalleResponse {
    private Long id;
    private String numeroRegistro;
    private String placa;
    private LocalDate fechaIngreso;
    private LocalTime horaIngreso;
    private String tipoAcceso;
    private String cupoAsignado;
    private String kilometraje;
    private String motivoIngreso;
    private String observaciones;
    private Boolean firmaConductor;
    private Boolean firmaOperario;
    private String estado;
    private VehiculoResponse vehiculo;
    private ConductorResponse conductor;
    private InventarioExteriorResponse inventarioExterior;
    private InventarioInteriorResponse inventarioInterior;
    private InventarioSeguridadResponse inventarioSeguridad;
    private List<EvidenciaResponse> evidencias;
    private String usuarioRegistro;
    private LocalDateTime createdAt;
}