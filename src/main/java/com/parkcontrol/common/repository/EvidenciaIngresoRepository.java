package com.parkcontrol.common.repository;

import com.parkcontrol.common.entity.EvidenciaIngreso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EvidenciaIngresoRepository extends JpaRepository<EvidenciaIngreso, Long> {
    List<EvidenciaIngreso> findByIngresoId(Long ingresoId);
}