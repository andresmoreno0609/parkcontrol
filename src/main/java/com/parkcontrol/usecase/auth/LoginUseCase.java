package com.parkcontrol.usecase.auth;

import com.parkcontrol.common.dto.auth.LoginRequest;
import com.parkcontrol.common.dto.auth.LoginResponse;
import com.parkcontrol.common.entity.Usuario;
import com.parkcontrol.common.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class LoginUseCase {

    private final UsuarioService usuarioService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public LoginResponse execute(LoginRequest request) {
        // Validaciones en preConditions
        if (request.username() == null || request.username().isBlank()) {
            throw new IllegalArgumentException("El username es requerido");
        }
        if (request.password() == null || request.password().isBlank()) {
            throw new IllegalArgumentException("La password es requerida");
        }

        // Buscar usuario
        Optional<Usuario> usuarioOpt = usuarioService.findByUsername(request.username());
        if (usuarioOpt.isEmpty()) {
            throw new IllegalArgumentException("Credenciales inválidas");
        }

        Usuario usuario = usuarioOpt.get();

        // Validar estado activo
        if (!"ACTIVO".equals(usuario.getEstado())) {
            throw new IllegalArgumentException("Usuario inactivo");
        }

        // Validar password
        if (!passwordEncoder.matches(request.password(), usuario.getPasswordHash())) {
            throw new IllegalArgumentException("Credenciales inválidas");
        }

        // Actualizar último login
        usuarioService.actualizarUltimoLogin(usuario.getId());

        // Generar token
        String token = jwtTokenProvider.generateToken(usuario);

        return LoginResponse.builder()
                .token(token)
                .username(usuario.getUsername())
                .rol(usuario.getRol())
                .nombre(usuario.getUsername())
                .build();
    }
}