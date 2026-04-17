package com.parkcontrol.common.dto.externo;

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
public class PersonaExternoResponse {
    private boolean encontrado;
    private String nombres;
    private String tipoDocumento;
    private String numeroDocumento;
    private String correo;
    private String telefono;
    private String sexo;
    private String fechaNacimiento;
}