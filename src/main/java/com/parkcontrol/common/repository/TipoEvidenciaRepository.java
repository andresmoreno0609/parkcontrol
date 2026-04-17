package com.parkcontrol.common.repository;

import com.parkcontrol.common.entity.TipoEvidencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TipoEvidenciaRepository extends JpaRepository<TipoEvidencia, Long> {
    List<TipoEvidencia> findByActivoTrueOrderByOrdenAsc();
}