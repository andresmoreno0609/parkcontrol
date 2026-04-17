package com.parkcontrol.common.repository;

import com.parkcontrol.common.entity.InventarioExterior;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InventarioExteriorRepository extends JpaRepository<InventarioExterior, Long> {
    Optional<InventarioExterior> findByIngresoId(Long ingresoId);
}