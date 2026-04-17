package com.parkcontrol.common.dto.salida;

import com.parkcontrol.common.dto.ingreso.EvidenciaResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SalidaDetalleResponse {
    private Long id;
    private Long ingresoId;
    private String numeroRegistro;
    private String placa;
    private LocalDate fechaIngreso;
    private LocalTime horaIngreso;
    private LocalDate fechaSalida;
    private LocalTime horaSalida;
    private String personaRetira;
    private String documentoRetira;
    private String tiempoTotal;
    private BigDecimal montoPagado;
    private String observaciones;
    private List<EvidenciaResponse> evidencias;
    private String usuarioRegistro;
}