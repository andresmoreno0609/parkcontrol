package com.parkcontrol.common.repository;

import com.parkcontrol.common.entity.InventarioInterior;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InventarioInteriorRepository extends JpaRepository<InventarioInterior, Long> {
    Optional<InventarioInterior> findByIngresoId(Long ingresoId);
}