package com.parkcontrol.common.repository;

import com.parkcontrol.common.entity.Tarifa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TarifaRepository extends JpaRepository<Tarifa, Long> {
    List<Tarifa> findByActivoTrue();

    Optional<Tarifa> findByTipoVehiculoAndTipoAccesoAndActivoTrue(String tipoVehiculo, String tipoAcceso);
}