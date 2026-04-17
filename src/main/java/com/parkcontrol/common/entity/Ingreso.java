package com.parkcontrol.common.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

@Entity
@Table(name = "ingreso")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ingreso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "vehiculo_id", nullable = false)
    private Long vehiculoId;

    @Column(name = "persona_id", nullable = false)
    private Long personaId;

    @Column(name = "numero_registro", nullable = false, unique = true, length = 20)
    private String numeroRegistro;

    @Column(name = "fecha_ingreso", nullable = false)
    private LocalDate fechaIngreso;

    @Column(name = "hora_ingreso", nullable = false)
    private LocalTime horaIngreso;

    @Column(name = "tipo_acceso", nullable = false, length = 20)
    private String tipoAcceso;

    @Column(name = "cupo_asignado", length = 20)
    private String cupoAsignado;

    @Column(length = 20)
    private String kilometraje;

    @Column(name = "motivo_ingreso", length = 255)
    private String motivoIngreso;

    @Column(columnDefinition = "TEXT")
    private String observaciones;

    @Column(name = "firma_conductor", nullable = false)
    @Builder.Default
    private Boolean firmaConductor = false;

    @Column(name = "firma_operario", nullable = false)
    @Builder.Default
    private Boolean firmaOperario = false;

    @Column(nullable = false, length = 20)
    @Builder.Default
    private String estado = "ACTIVO";

    @Column(name = "usuario_registro", nullable = false, length = 50)
    private String usuarioRegistro;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}