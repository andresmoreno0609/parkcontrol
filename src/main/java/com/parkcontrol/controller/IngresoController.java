package com.parkcontrol.controller;

import com.parkcontrol.common.dto.PageResponse;
import com.parkcontrol.common.dto.ingreso.IngresoDetalleResponse;
import com.parkcontrol.common.dto.ingreso.IngresoRequest;
import com.parkcontrol.common.dto.ingreso.IngresoResponse;
import com.parkcontrol.usecase.ingreso.RegistrarIngresoUseCase;
import com.parkcontrol.usecase.ingreso.BuscarIngresoActivoUseCase;
import com.parkcontrol.common.service.IngresoService;
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

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/ingresos")
@RequiredArgsConstructor
@Tag(name = "Ingresos", description = "Gestión de ingresos de vehículos")
public class IngresoController {

    private final RegistrarIngresoUseCase registrarIngresoUseCase;
    private final BuscarIngresoActivoUseCase buscarIngresoActivoUseCase;
    private final IngresoService ingresoService;

    @PostMapping
    @Operation(summary = "Registrar ingreso", description = "Registra un nuevo ingreso de vehículo")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Ingreso registrado"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    public ResponseEntity<IngresoResponse> create(
            @RequestBody IngresoRequest request,
            @RequestHeader("X-Usuario") String username) {

        IngresoResponse response = registrarIngresoUseCase.execute(request, username);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/activo")
    @Operation(summary = "Buscar ingreso activo", description = "Busca un ingreso activo por placa")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Ingreso encontrado"),
        @ApiResponse(responseCode = "404", description = "No hay ingreso activo")
    })
    public ResponseEntity<?> findActivoByPlaca(@RequestParam String placa) {
        Optional<IngresoResponse> ingreso = buscarIngresoActivoUseCase.execute(placa);
        if (ingreso.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "No existe ingreso activo para la placa " + placa));
        }
        return ResponseEntity.ok(ingreso.get());
    }

    @GetMapping
    @Operation(summary = "Listar ingresos", description = "Lista ingresos con filtros y paginación")
    @ApiResponse(responseCode = "200", description = "Lista de ingresos")
    public ResponseEntity<PageResponse<IngresoResponse>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String placa,
            @RequestParam(required = false) String estado) {

        Page<IngresoResponse> ingresos = ingresoService.findAll(
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id")),
                placa, estado
        );

        PageResponse<IngresoResponse> response = PageResponse.<IngresoResponse>builder()
                .content(ingresos.getContent())
                .page(ingresos.getNumber())
                .size(ingresos.getSize())
                .totalElements(ingresos.getTotalElements())
                .totalPages(ingresos.getTotalPages())
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener detalle de ingreso", description = "Retorna el detalle completo de un ingreso")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Detalle del ingreso"),
        @ApiResponse(responseCode = "404", description = "Ingreso no encontrado")
    })
    public ResponseEntity<IngresoDetalleResponse> findById(@PathVariable Long id) {
        IngresoDetalleResponse response = ingresoService.findDetalleById(id);
        return ResponseEntity.ok(response);
    }
}