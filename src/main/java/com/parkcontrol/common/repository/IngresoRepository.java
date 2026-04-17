package com.parkcontrol.common.repository;

import com.parkcontrol.common.entity.Ingreso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface IngresoRepository extends JpaRepository<Ingreso, Long> {

    Optional<Ingreso> findByNumeroRegistro(String numeroRegistro);

    boolean existsByNumeroRegistro(String numeroRegistro);

    @Query("SELECT i FROM Ingreso i WHERE i.vehiculo.placa = :placa AND i.estado = 'ACTIVO' ORDER BY i.fechaIngreso DESC, i.horaIngreso DESC")
    Optional<Ingreso> findFirstByPlacaActivo(@Param("placa") String placa);

    List<Ingreso> findByEstado(String estado);

    @Query("SELECT i FROM Ingreso i WHERE i.fechaIngreso BETWEEN :fechaDesde AND :fechaHasta")
    List<Ingreso> findByFechaIngresoBetween(@Param("fechaDesde") LocalDate fechaDesde, @Param("fechaHasta") LocalDate fechaHasta);

    List<Ingreso> findByVehiculoId(Long vehiculoId);

    @Query("SELECT MAX(CAST(SUBSTRING(i.numeroRegistro, 16) AS int)) FROM Ingreso i WHERE i.fechaIngreso = :fecha")
    Integer findMaxConsecutivoByFecha(@Param("fecha") LocalDate fecha);
}