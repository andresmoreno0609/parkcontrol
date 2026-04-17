package com.parkcontrol.common.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "inventario_exterior")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventarioExterior {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ingreso_id", nullable = false)
    private Long ingresoId;

    @Column(name = "parachoques_delantero", nullable = false)
    @Builder.Default
    private Boolean parachoquesDelantero = false;

    @Column(name = "parachoques_trasero", nullable = false)
    @Builder.Default
    private Boolean parachoquesTrasero = false;

    @Column(nullable = false)
    @Builder.Default
    private Boolean puertas = false;

    @Column(nullable = false)
    @Builder.Default
    private Boolean espejos = false;

    @Column(nullable = false)
    @Builder.Default
    private Boolean vidrios = false;

    @Column(nullable = false)
    @Builder.Default
    private Boolean luces = false;

    @Column(nullable = false)
    @Builder.Default
    private Boolean llantas = false;

    @Column(name = "rayones", nullable = false)
    @Builder.Default
    private Boolean rayones = false;

    @Column(nullable = false)
    @Builder.Default
    private Boolean golpes = false;

    @Column(columnDefinition = "TEXT")
    private String observaciones;

    @Column(name = "fecha_proceso", nullable = false)
    private LocalDateTime fechaProceso;

    @PrePersist
    protected void onCreate() {
        fechaProceso = LocalDateTime.now();
    }
}