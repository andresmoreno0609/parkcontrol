package com.parkcontrol.common.service;

import com.parkcontrol.common.dto.tarifa.CrearTarifaRequest;
import com.parkcontrol.common.dto.tarifa.TarifaResponse;
import com.parkcontrol.common.entity.Tarifa;
import com.parkcontrol.common.repository.TarifaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TarifaService {

    private final TarifaRepository tarifaRepository;

    public List<TarifaResponse> findAllActivas() {
        return tarifaRepository.findByActivoTrue().stream()
                .map(this::toResponse)
                .toList();
    }

    public List<TarifaResponse> findAll() {
        return tarifaRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public Optional<Tarifa> findByTipoVehiculoAndTipoAcceso(String tipoVehiculo, String tipoAcceso) {
        return tarifaRepository.findByTipoVehiculoAndTipoAccesoAndActivoTrue(tipoVehiculo, tipoAcceso);
    }

    @Transactional
    public TarifaResponse create(CrearTarifaRequest request) {
        // Desactivar si existe para el mismo tipo
        tarifaRepository.findByTipoVehiculoAndTipoAccesoAndActivoTrue(
                request.getTipoVehiculo(), request.getTipoAcceso()
        ).ifPresent(tarifa -> {
            tarifa.setActivo(false);
            tarifaRepository.save(tarifa);
        });

        Tarifa tarifa = Tarifa.builder()
                .tipoVehiculo(request.getTipoVehiculo())
                .tipoAcceso(request.getTipoAcceso())
                .valor(request.getValor())
                .activo(true)
                .build();

        Tarifa saved = tarifaRepository.save(tarifa);
        return toResponse(saved);
    }

    public Optional<Tarifa> findById(Long id) {
        return tarifaRepository.findById(id);
    }

    private TarifaResponse toResponse(Tarifa tarifa) {
        return TarifaResponse.builder()
                .id(tarifa.getId())
                .tipoVehiculo(tarifa.getTipoVehiculo())
                .tipoAcceso(tarifa.getTipoAcceso())
                .valor(tarifa.getValor())
                .activo(tarifa.getActivo())
                .build();
    }
}