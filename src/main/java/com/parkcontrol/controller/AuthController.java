package com.parkcontrol.controller;

import com.parkcontrol.common.dto.auth.CambiarPasswordRequest;
import com.parkcontrol.common.dto.auth.LoginRequest;
import com.parkcontrol.common.dto.auth.LoginResponse;
import com.parkcontrol.usecase.auth.CambiarPasswordUseCase;
import com.parkcontrol.usecase.auth.LoginUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticación", description = "Endpoints de login y gestión de sesión")
public class AuthController {

    private final LoginUseCase loginUseCase;
    private final CambiarPasswordUseCase cambiarPasswordUseCase;

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión", description = "Autentica al usuario y retorna un token JWT")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Login exitoso"),
        @ApiResponse(responseCode = "401", description = "Credenciales inválidas")
    })
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        LoginResponse response = loginUseCase.execute(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/cambiar-password")
    @Operation(summary = "Cambiar contraseña", description = "Cambia la contraseña del usuario autenticado")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Contraseña cambiada"),
        @ApiResponse(responseCode = "400", description = "Contraseña actual incorrecta")
    })
    public ResponseEntity<Map<String, String>> cambiarPassword(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody CambiarPasswordRequest request) {

        Long userId = extractUserId(authHeader);
        cambiarPasswordUseCase.execute(userId, request);
        return ResponseEntity.ok(Map.of("mensaje", "Contraseña actualizada correctamente"));
    }

    private Long extractUserId(String authHeader) {
        // TODO: implementar correctamente extrayendo del token
        return 1L;
    }
}