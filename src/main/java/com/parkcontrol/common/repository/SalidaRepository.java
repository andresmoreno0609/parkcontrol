package com.parkcontrol.common.repository;

import com.parkcontrol.common.entity.Salida;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SalidaRepository extends JpaRepository<Salida, Long> {
    Optional<Salida> findByIngresoId(Long ingresoId);
    boolean existsByIngresoId(Long ingresoId);
}