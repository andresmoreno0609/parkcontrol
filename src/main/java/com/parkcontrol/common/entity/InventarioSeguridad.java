package com.parkcontrol.common.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "inventario_seguridad")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventarioSeguridad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ingreso_id", nullable = false)
    private Long ingresoId;

    @Column(name = "llanta_repuesto", nullable = false)
    @Builder.Default
    private Boolean llantaRepuesto = false;

    @Column(nullable = false)
    @Builder.Default
    private Boolean gato = false;

    @Column(nullable = false)
    @Builder.Default
    private Boolean cruceta = false;

    @Column(nullable = false)
    @Builder.Default
    private Boolean extintor = false;

    @Column(nullable = false)
    @Builder.Default
    private Boolean botiquin = false;

    @Column(nullable = false)
    @Builder.Default
    private Boolean triangulos = false;

    @Column(nullable = false)
    @Builder.Default
    private Boolean herramientas = false;

    @Column(length = 100)
    private String otros;

    @Column(name = "fecha_proceso", nullable = false)
    private LocalDateTime fechaProceso;

    @PrePersist
    protected void onCreate() {
        fechaProceso = LocalDateTime.now();
    }
}