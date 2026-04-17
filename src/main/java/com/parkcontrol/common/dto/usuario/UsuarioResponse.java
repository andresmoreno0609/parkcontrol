package com.parkcontrol.common.dto.usuario;

@Builder
public record UsuarioResponse(
    Long id,
    String username,
    String rol,
    String estado,
    String ultimoLogin
) {}