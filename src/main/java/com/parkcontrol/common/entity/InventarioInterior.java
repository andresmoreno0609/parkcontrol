package com.parkcontrol.common.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "inventario_interior")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventarioInterior {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ingreso_id", nullable = false)
    private Long ingresoId;

    @Column(nullable = false)
    @Builder.Default
    private Boolean tapiceria = false;

    @Column(nullable = false)
    @Builder.Default
    private Boolean tablero = false;

    @Column(name = "radio_pantalla", nullable = false)
    @Builder.Default
    private Boolean radioPantalla = false;

    @Column(nullable = false)
    @Builder.Default
    private Boolean alfombras = false;

    @Column(nullable = false)
    @Builder.Default
    private Boolean cinturones = false;

    @Column(name = "elementos_personales", nullable = false)
    @Builder.Default
    private Boolean elementosPersonales = false;

    @Column(columnDefinition = "TEXT")
    private String observaciones;

    @Column(name = "fecha_proceso", nullable = false)
    private LocalDateTime fechaProceso;

    @PrePersist
    protected void onCreate() {
        fechaProceso = LocalDateTime.now();
    }
}