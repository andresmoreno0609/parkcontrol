package com.parkcontrol.common.service;

import com.parkcontrol.common.dto.usuario.CrearUsuarioRequest;
import com.parkcontrol.common.dto.usuario.UsuarioResponse;
import com.parkcontrol.common.entity.Usuario;
import com.parkcontrol.common.entity.Persona;
import com.parkcontrol.common.repository.UsuarioRepository;
import com.parkcontrol.common.repository.PersonaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PersonaRepository personaRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UsuarioResponse create(CrearUsuarioRequest request) {
        if (usuarioRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("El usuario ya existe");
        }

        if (!personaRepository.existsById(request.getPersonaId())) {
            throw new IllegalArgumentException("La persona asociada no existe");
        }

        Usuario usuario = Usuario.builder()
                .username(request.getUsername())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .rol(request.getRol())
                .personaId(request.getPersonaId())
                .estado("ACTIVO")
                .build();

        Usuario saved = usuarioRepository.save(usuario);
        return toResponse(saved);
    }

    public Page<UsuarioResponse> findAll(Pageable pageable) {
        return usuarioRepository.findAll(pageable).map(this::toResponse);
    }

    public Optional<Usuario> findByUsername(String username) {
        return usuarioRepository.findByUsername(username);
    }

    @Transactional
    public UsuarioResponse cambiarEstado(Long id, String estado) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        usuario.setEstado(estado);
        Usuario saved = usuarioRepository.save(usuario);
        return toResponse(saved);
    }

    @Transactional
    public void actualizarUltimoLogin(Long id) {
        usuarioRepository.findById(id).ifPresent(usuario -> {
            usuario.setUltimoLogin(LocalDateTime.now());
            usuarioRepository.save(usuario);
        });
    }

    @Transactional
    public void cambiarPassword(Long id, String passwordNuevo) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        usuario.setPasswordHash(passwordEncoder.encode(passwordNuevo));
        usuarioRepository.save(usuario);
    }

    public Optional<Usuario> findById(Long id) {
        return usuarioRepository.findById(id);
    }

    private UsuarioResponse toResponse(Usuario usuario) {
        return UsuarioResponse.builder()
                .id(usuario.getId())
                .username(usuario.getUsername())
                .rol(usuario.getRol())
                .estado(usuario.getEstado())
                .ultimoLogin(usuario.getUltimoLogin() != null ? usuario.getUltimoLogin().toString() : null)
                .build();
    }
}