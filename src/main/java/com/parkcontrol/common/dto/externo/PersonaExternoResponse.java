package com.parkcontrol.common.dto.externo;

@Builder
public record PersonaExternoResponse(
    boolean encontrado,
    String nombres,
    String tipoDocumento,
    String numeroDocumento,
    String correo,
    String telefono,
    String sexo,
    String fechaNacimiento
) {}