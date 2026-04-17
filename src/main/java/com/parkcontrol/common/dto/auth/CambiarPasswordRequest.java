package com.parkcontrol.common.dto.auth;

public record CambiarPasswordRequest(
    String passwordActual,
    String passwordNuevo
) {}