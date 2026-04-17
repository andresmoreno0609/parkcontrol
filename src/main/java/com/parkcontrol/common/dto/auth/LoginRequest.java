package com.parkcontrol.common.dto.auth;

public record LoginRequest(
    String username,
    String password
) {}