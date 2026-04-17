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
        if (request.username() == null || request.username().isBlank()) {
            throw new IllegalArgumentException("El username es requerido");
        }
        if (request.username().length() < 3) {
            throw new IllegalArgumentException("El username debe tener al menos 3 caracteres");
        }
        if (request.password() == null || request.password().isBlank()) {
            throw new IllegalArgumentException("La password es requerida");
        }
        if (request.password().length() < 6) {
            throw new IllegalArgumentException("La password debe tener al menos 6 caracteres");
        }
        if (request.rol() == null || request.rol().isBlank()) {
            throw new IllegalArgumentException("El rol es requerido");
        }
        if (!request.rol().equals("ADMIN") && !request.rol().equals("OPERARIO")) {
            throw new IllegalArgumentException("El rol debe ser ADMIN o OPERARIO");
        }
        if (request.personaId() == null) {
            throw new IllegalArgumentException("La persona asociada es requerida");
        }

        // Core - Crear usuario
        return usuarioService.create(request);
    }
}