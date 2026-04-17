package com.parkcontrol.controller;

import com.parkcontrol.common.dto.PageResponse;
import com.parkcontrol.common.dto.usuario.CambiarEstadoRequest;
import com.parkcontrol.common.dto.usuario.CrearUsuarioRequest;
import com.parkcontrol.common.dto.usuario.UsuarioResponse;
import com.parkcontrol.usecase.usuario.CrearUsuarioUseCase;
import com.parkcontrol.common.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/usuarios")
@RequiredArgsConstructor
@Tag(name = "Usuarios", description = "Gestión de usuarios del sistema")
public class UsuarioController {

    private final CrearUsuarioUseCase crearUsuarioUseCase;
    private final UsuarioService usuarioService;

    @PostMapping
    @Operation(summary = "Crear usuario", description = "Crea un nuevo usuario (solo ADMIN)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Usuario creado"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    public ResponseEntity<UsuarioResponse> create(@RequestBody CrearUsuarioRequest request) {
        UsuarioResponse response = crearUsuarioUseCase.execute(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Listar usuarios", description = "Lista todos los usuarios con paginación")
    @ApiResponse(responseCode = "200", description = "Lista de usuarios")
    public ResponseEntity<PageResponse<UsuarioResponse>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<UsuarioResponse> usuarios = usuarioService.findAll(
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"))
        );

        PageResponse<UsuarioResponse> response = PageResponse.<UsuarioResponse>builder()
                .content(usuarios.getContent())
                .page(usuarios.getNumber())
                .size(usuarios.getSize())
                .totalElements(usuarios.getTotalElements())
                .totalPages(usuarios.getTotalPages())
                .build();

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/estado")
    @Operation(summary = "Cambiar estado", description = "Activa o desactiva un usuario")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Estado actualizado"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<UsuarioResponse> cambiarEstado(
            @PathVariable Long id,
            @RequestBody CambiarEstadoRequest request) {

        UsuarioResponse response = usuarioService.cambiarEstado(id, request.getEstado());
        return ResponseEntity.ok(response);
    }
}