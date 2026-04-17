package com.parkcontrol.usecase.ingreso;

import com.parkcontrol.common.dto.ingreso.IngresoResponse;
import com.parkcontrol.common.service.IngresoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class BuscarIngresoActivoUseCase {

    private final IngresoService ingresoService;

    public Optional<IngresoResponse> execute(String placa) {
        if (placa == null || placa.isBlank()) {
            throw new IllegalArgumentException("La placa es requerida");
        }
        return ingresoService.findActivoByPlaca(placa.toUpperCase())
                .map(ingreso -> {
                    // Mapear a response con vehículo y conductor
                    return IngresoResponse.builder()
                            .id(ingreso.getId())
                            .numeroRegistro(ingreso.getNumeroRegistro())
                            .placa("")
                            .fechaIngreso(ingreso.getFechaIngreso())
                            .horaIngreso(ingreso.getHoraIngreso())
                            .tipoAcceso(ingreso.getTipoAcceso())
                            .cupoAsignado(ingreso.getCupoAsignado())
                            .estado(ingreso.getEstado())
                            .build();
                });
    }
}