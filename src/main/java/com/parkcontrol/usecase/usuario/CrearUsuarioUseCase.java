package com.parkcontrol.usecase.usuario;

import com.parkcontrol.common.dto.usuario.CrearUsuarioRequest;
import com.parkcontrol.common.dto.usuario.UsuarioResponse;
import com.parkcontrol.common.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CrearUsuarioUseCase {

    private final UsuarioService usuarioService;

    public UsuarioResponse execute(CrearUsuarioRequest request) {
        // PreConditions - Validaciones
        if (request.getUsername() == null || request.getUsername().isBlank()) {
            throw new IllegalArgumentException("El username es requerido");
        }
        if (request.getUsername().length() < 3) {
            throw new IllegalArgumentException("El username debe tener al menos 3 caracteres");
        }
        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new IllegalArgumentException("La password es requerida");
        }
        if (request.getPassword().length() < 6) {
            throw new IllegalArgumentException("La password debe tener al menos 6 caracteres");
        }
        if (request.getRol() == null || request.getRol().isBlank()) {
            throw new IllegalArgumentException("El rol es requerido");
        }
        if (!request.getRol().equals("ADMIN") && !request.getRol().equals("OPERARIO")) {
            throw new IllegalArgumentException("El rol debe ser ADMIN o OPERARIO");
        }
        if (request.getPersonaId() == null) {
            throw new IllegalArgumentException("La persona asociada es requerida");
        }

        // Core - Crear usuario
        return usuarioService.create(request);
    }
}