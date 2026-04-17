package com.parkcontrol.common.repository;

import com.parkcontrol.common.entity.EvidenciaSalida;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EvidenciaSalidaRepository extends JpaRepository<EvidenciaSalida, Long> {
    List<EvidenciaSalida> findBySalidaId(Long salidaId);
}