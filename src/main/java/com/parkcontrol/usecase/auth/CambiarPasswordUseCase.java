package com.parkcontrol.usecase.auth;

import com.parkcontrol.common.dto.auth.CambiarPasswordRequest;
import com.parkcontrol.common.entity.Usuario;
import com.parkcontrol.common.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CambiarPasswordUseCase {

    private final UsuarioService usuarioService;
    private final PasswordEncoder passwordEncoder;

    public void execute(Long usuarioId, CambiarPasswordRequest request) {
        // Validaciones en preConditions
        if (request.passwordActual() == null || request.passwordActual().isBlank()) {
            throw new IllegalArgumentException("La contraseña actual es requerida");
        }
        if (request.passwordNuevo() == null || request.passwordNuevo().isBlank()) {
            throw new IllegalArgumentException("La contraseña nueva es requerida");
        }
        if (request.passwordNuevo().length() < 6) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 6 caracteres");
        }

        // Buscar usuario
        Usuario usuario = usuarioService.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        // Validar password actual
        if (!passwordEncoder.matches(request.passwordActual(), usuario.getPasswordHash())) {
            throw new IllegalArgumentException("La contraseña actual es incorrecta");
        }

        // Cambiar password
        usuarioService.cambiarPassword(usuarioId, request.passwordNuevo());
    }
}