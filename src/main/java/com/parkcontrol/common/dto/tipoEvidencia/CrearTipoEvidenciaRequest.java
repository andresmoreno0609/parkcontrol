package com.parkcontrol.common.dto.tipoEvidencia;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CrearTipoEvidenciaRequest {
    private String nombre;
    private String descripcion;
    private Integer orden;
}