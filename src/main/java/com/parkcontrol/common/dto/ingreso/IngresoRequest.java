package com.parkcontrol.common.dto.ingreso;

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
public class IngresoRequest {
    // Datos del vehiculo
    private String placa;
    private String tipoVehiculo;
    private String marca;
    private Integer modelo;
    private String referencia;
    private String color;
    private String servicio;

    // Datos del conductor
    private String tipoDocumentoConductor;
    private String numeroDocumentoConductor;

    // Info parqueadero
    private String tipoAcceso;
    private String cupoAsignado;
    private String kilometraje;
    private String motivoIngreso;
    private String observaciones;
    private Boolean firmaConductor;
    private Boolean firmaOperario;

    // Inventarios
    private InventarioExteriorRequest inventarioExterior;
    private InventarioInteriorRequest inventarioInterior;
    private InventarioSeguridadRequest inventarioSeguridad;

    // Evidencias
    private List<EvidenciaRequest> evidencias;
}