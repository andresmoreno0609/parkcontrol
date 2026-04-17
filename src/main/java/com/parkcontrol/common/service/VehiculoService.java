package com.parkcontrol.common.service;

import com.parkcontrol.common.entity.Vehiculo;
import com.parkcontrol.common.repository.VehiculoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VehiculoService {

    private final VehiculoRepository vehiculoRepository;

    @Transactional
    public Vehiculo crearOActualizar(String placa, String marca, Integer modelo,
                                    String referencia, String color, String tipo, String servicio) {

        Optional<Vehiculo> existente = vehiculoRepository.findByPlaca(placa);

        if (existente.isPresent()) {
            Vehiculo vehiculo = existente.get();
            if (marca != null) vehiculo.setMarca(marca);
            if (modelo != null) vehiculo.setModelo(modelo);
            if (referencia != null) vehiculo.setReferencia(referencia);
            if (color != null) vehiculo.setColor(color);
            if (tipo != null) vehiculo.setTipo(tipo);
            if (servicio != null) vehiculo.setServicio(servicio);
            return vehiculoRepository.save(vehiculo);
        } else {
            Vehiculo nuevo = Vehiculo.builder()
                    .placa(placa)
                    .marca(marca)
                    .modelo(modelo)
                    .referencia(referencia)
                    .color(color)
                    .tipo(tipo)
                    .servicio(servicio != null ? servicio : "PARTICULAR")
                    .build();
            return vehiculoRepository.save(nuevo);
        }
    }

    public Optional<Vehiculo> findByPlaca(String placa) {
        return vehiculoRepository.findByPlaca(placa);
    }

    public Optional<Vehiculo> findById(Long id) {
        return vehiculoRepository.findById(id);
    }
}