package com.parkcontrol.common.dto.auth;

@Builder
public record LoginResponse(
    String token,
    String username,
    String rol,
    String nombre
) {}