package com.parkcontrol.common.dto.usuario;

public record CrearUsuarioRequest(
    String username,
    String password,
    String rol,
    Long personaId
) {}