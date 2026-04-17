package com.parkcontrol.common.repository;

import com.parkcontrol.common.entity.InventarioSeguridad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InventarioSeguridadRepository extends JpaRepository<InventarioSeguridad, Long> {
    Optional<InventarioSeguridad> findByIngresoId(Long ingresoId);
}