package com.parkcontrol.common.service;

import com.parkcontrol.common.dto.ingreso.*;
import com.parkcontrol.common.entity.*;
import com.parkcontrol.common.repository.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class IngresoService {

    private static final Logger log = LoggerFactory.getLogger(IngresoService.class);

    private final IngresoRepository ingresoRepository;
    private final InventarioExteriorRepository inventarioExteriorRepository;
    private final InventarioInteriorRepository inventarioInteriorRepository;
    private final InventarioSeguridadRepository inventarioSeguridadRepository;
    private final EvidenciaIngresoRepository evidenciaIngresoRepository;
    private final TipoEvidenciaRepository tipoEvidenciaRepository;

    private final PersonaService personaService;
    private final VehiculoService vehiculoService;
    private final TarifaService tarifaService;

    @Transactional
    public IngresoResponse create(IngresoRequest request, String usuarioRegistro) {
        log.info("Creando ingreso para placa: {}", request.getPlaca());

        // 1. Buscar o crear persona (conductor)
        Persona persona = personaService.crearOActualizar(
                request.getTipoDocumentoConductor(),
                request.getNumeroDocumentoConductor(),
                null, null, null, null, null
        );
        log.debug("Persona creada/encontrada: {}", persona.getId());

        // 2. Buscar o crear vehículo
        Vehiculo vehiculo = vehiculoService.crearOActualizar(
                request.getPlaca(),
                request.getMarca(),
                request.getModelo(),
                request.getReferencia(),
                request.getColor(),
                request.getTipoVehiculo(),
                request.getServicio()
        );
        log.debug("Vehículo creado/encontrado: {}", vehiculo.getId());

        // 3. Generar número de registro
        String numeroRegistro = generarNumeroRegistro();
        log.debug("Número de registro: {}", numeroRegistro);

        // 4. Crear ingreso
        Ingreso ingreso = Ingreso.builder()
                .vehiculoId(vehiculo.getId())
                .personaId(persona.getId())
                .numeroRegistro(numeroRegistro)
                .fechaIngreso(LocalDate.now())
                .horaIngreso(LocalTime.now())
                .tipoAcceso(request.getTipoAcceso())
                .cupoAsignado(request.getCupoAsignado())
                .kilometraje(request.getKilometraje())
                .motivoIngreso(request.getMotivoIngreso())
                .observaciones(request.getObservaciones())
                .firmaConductor(request.getFirmaConductor() != null ? request.getFirmaConductor() : false)
                .firmaOperario(request.getFirmaOperario() != null ? request.getFirmaOperario() : false)
                .estado("ACTIVO")
                .usuarioRegistro(usuarioRegistro)
                .build();

        Ingreso savedIngreso = ingresoRepository.save(ingreso);
        log.debug("Ingreso guardado con ID: {}", savedIngreso.getId());

        // 5. Guardar inventarios
        if (request.getInventarioExterior() != null) {
            InventarioExteriorRequest extReq = request.getInventarioExterior();
            InventarioExterior ext = InventarioExterior.builder()
                    .ingresoId(savedIngreso.getId())
                    .parachoquesDelantero(boolOrDefault(extReq.getParachoquesDelantero(), false))
                    .parachoquesTrasero(boolOrDefault(extReq.getParachoquesTrasero(), false))
                    .puertas(boolOrDefault(extReq.getPuertas(), false))
                    .espejos(boolOrDefault(extReq.getEspejos(), false))
                    .vidrios(boolOrDefault(extReq.getVidrios(), false))
                    .luces(boolOrDefault(extReq.getLuces(), false))
                    .llantas(boolOrDefault(extReq.getLlantas(), false))
                    .rayones(boolOrDefault(extReq.getRayones(), false))
                    .golpes(boolOrDefault(extReq.getGolpes(), false))
                    .observaciones(extReq.getObservaciones())
                    .build();
            inventarioExteriorRepository.save(ext);
        }

        if (request.getInventarioInterior() != null) {
            InventarioInteriorRequest intReq = request.getInventarioInterior();
            InventarioInterior inter = InventarioInterior.builder()
                    .ingresoId(savedIngreso.getId())
                    .tapiceria(boolOrDefault(intReq.getTapiceria(), false))
                    .tablero(boolOrDefault(intReq.getTablero(), false))
                    .radioPantalla(boolOrDefault(intReq.getRadioPantalla(), false))
                    .alfombras(boolOrDefault(intReq.getAlfombras(), false))
                    .cinturones(boolOrDefault(intReq.getCinturones(), false))
                    .elementosPersonales(boolOrDefault(intReq.getElementosPersonales(), false))
                    .observaciones(intReq.getObservaciones())
                    .build();
            inventarioInteriorRepository.save(inter);
        }

        if (request.getInventarioSeguridad() != null) {
            InventarioSeguridadRequest segReq = request.getInventarioSeguridad();
            InventarioSeguridad seg = InventarioSeguridad.builder()
                    .ingresoId(savedIngreso.getId())
                    .llantaRepuesto(boolOrDefault(segReq.getLlantaRepuesto(), false))
                    .gato(boolOrDefault(segReq.getGato(), false))
                    .cruceta(boolOrDefault(segReq.getCruceta(), false))
                    .extintor(boolOrDefault(segReq.getExtintor(), false))
                    .botiquin(boolOrDefault(segReq.getBotiquin(), false))
                    .triangulos(boolOrDefault(segReq.getTriangulos(), false))
                    .herramientas(boolOrDefault(segReq.getHerramientas(), false))
                    .otros(segReq.getOtros())
                    .build();
            inventarioSeguridadRepository.save(seg);
        }

        // 6. Guardar evidencias (si hay)
        if (request.getEvidencias() != null && !request.getEvidencias().isEmpty()) {
            for (EvidenciaRequest evReq : request.getEvidencias()) {
                EvidenciaIngreso ev = EvidenciaIngreso.builder()
                        .ingresoId(savedIngreso.getId())
                        .tipoId(evReq.getTipoId())
                        .rutaArchivo(evReq.getRutaArchivo())
                        .build();
                evidenciaIngresoRepository.save(ev);
            }
        }

        log.info("Ingreso creado exitosamente: {}", savedIngreso.getId());
        return toResponse(savedIngreso, vehiculo, persona);
    }

    public Optional<Ingreso> findActivoByPlaca(String placa) {
        return ingresoRepository.findFirstByPlacaActivo(placa.toUpperCase());
    }

    public IngresoDetalleResponse findDetalleById(Long id) {
        Ingreso ingreso = ingresoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Ingreso no encontrado"));

        Optional<Vehiculo> vehiculo = vehiculoService.findById(ingreso.getVehiculoId());
        Optional<Persona> persona = personaService.findById(ingreso.getPersonaId());

        Optional<InventarioExterior> ext = inventarioExteriorRepository.findByIngresoId(id);
        Optional<InventarioInterior> inter = inventarioInteriorRepository.findByIngresoId(id);
        Optional<InventarioSeguridad> seg = inventarioSeguridadRepository.findByIngresoId(id);

        List<EvidenciaIngreso> evidencias = evidenciaIngresoRepository.findByIngresoId(id);

        return toDetalleResponse(ingreso, vehiculo.orElse(null), persona.orElse(null),
                ext.orElse(null), inter.orElse(null), seg.orElse(null), evidencias);
    }

    public Page<IngresoResponse> findAll(Pageable pageable, String placa, String estado) {
        Page<Ingreso> ingresos;
        if (placa != null && !placa.isBlank()) {
            ingresos = ingresoRepository.findAll(pageable);
        } else if (estado != null && !estado.isBlank()) {
            ingresos = ingresoRepository.findByEstado(estado, pageable);
        } else {
            ingresos = ingresoRepository.findAll(pageable);
        }

        return ingresos.map(ingreso -> {
            Optional<Vehiculo> v = vehiculoService.findById(ingreso.getVehiculoId());
            Optional<Persona> p = personaService.findById(ingreso.getPersonaId());
            return toResponse(ingreso, v.orElse(null), p.orElse(null));
        });
    }

    @Transactional
    public void actualizarEstado(Long id, String estado) {
        Ingreso ingreso = ingresoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Ingreso no encontrado"));
        ingreso.setEstado(estado);
        ingresoRepository.save(ingreso);
    }

    private String generarNumeroRegistro() {
        String fecha = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        Integer max = ingresoRepository.findMaxConsecutivoByFecha(LocalDate.now());
        int siguiente = (max != null ? max : 0) + 1;
        return String.format("IN-%s-%04d", fecha, siguiente);
    }

    private IngresoResponse toResponse(Ingreso ingreso, Vehiculo vehiculo, Persona persona) {
        return IngresoResponse.builder()
                .id(ingreso.getId())
                .numeroRegistro(ingreso.getNumeroRegistro())
                .placa(vehiculo != null ? vehiculo.getPlaca() : null)
                .fechaIngreso(ingreso.getFechaIngreso())
                .horaIngreso(ingreso.getHoraIngreso())
                .tipoAcceso(ingreso.getTipoAcceso())
                .cupoAsignado(ingreso.getCupoAsignado())
                .estado(ingreso.getEstado())
                .vehiculo(vehiculo != null ? VehiculoResponse.builder()
                        .id(vehiculo.getId())
                        .placa(vehiculo.getPlaca())
                        .marca(vehiculo.getMarca())
                        .modelo(vehiculo.getModelo())
                        .referencia(vehiculo.getReferencia())
                        .build() : null)
                .conductor(persona != null ? ConductorResponse.builder()
                        .id(persona.getId())
                        .nombres(persona.getNombres())
                        .tipoDocumento(persona.getTipoDocumento())
                        .numeroDocumento(persona.getNumeroDocumento())
                        .build() : null)
                .usuarioRegistro(ingreso.getUsuarioRegistro())
                .build();
    }

    private IngresoDetalleResponse toDetalleResponse(Ingreso ingreso, Vehiculo vehiculo, Persona persona,
                                                     InventarioExterior ext, InventarioInterior inter,
                                                     InventarioSeguridad seg, List<EvidenciaIngreso> evidencias) {

        List<EvidenciaResponse> evResponses = new ArrayList<>();
        for (EvidenciaIngreso ev : evidencias) {
            Optional<TipoEvidencia> tipo = tipoEvidenciaRepository.findById(ev.getTipoId());
            evResponses.add(EvidenciaResponse.builder()
                    .id(ev.getId())
                    .tipoId(ev.getTipoId())
                    .rutaArchivo(ev.getRutaArchivo())
                    .tipoNombre(tipo.map(TipoEvidencia::getNombre).orElse(null))
                    .build());
        }

        return IngresoDetalleResponse.builder()
                .id(ingreso.getId())
                .numeroRegistro(ingreso.getNumeroRegistro())
                .placa(vehiculo != null ? vehiculo.getPlaca() : null)
                .fechaIngreso(ingreso.getFechaIngreso())
                .horaIngreso(ingreso.getHoraIngreso())
                .tipoAcceso(ingreso.getTipoAcceso())
                .cupoAsignado(ingreso.getCupoAsignado())
                .kilometraje(ingreso.getKilometraje())
                .motivoIngreso(ingreso.getMotivoIngreso())
                .observaciones(ingreso.getObservaciones())
                .firmaConductor(ingreso.getFirmaConductor())
                .firmaOperario(ingreso.getFirmaOperario())
                .estado(ingreso.getEstado())
                .vehiculo(vehiculo != null ? VehiculoResponse.builder()
                        .id(vehiculo.getId())
                        .placa(vehiculo.getPlaca())
                        .marca(vehiculo.getMarca())
                        .modelo(vehiculo.getModelo())
                        .referencia(vehiculo.getReferencia())
                        .build() : null)
                .conductor(persona != null ? ConductorResponse.builder()
                        .id(persona.getId())
                        .nombres(persona.getNombres())
                        .tipoDocumento(persona.getTipoDocumento())
                        .numeroDocumento(persona.getNumeroDocumento())
                        .build() : null)
                .inventarioExterior(ext != null ? mapToExtResponse(ext) : null)
                .inventarioInterior(inter != null ? mapToInterResponse(inter) : null)
                .inventarioSeguridad(seg != null ? mapToSegResponse(seg) : null)
                .evidencias(evResponses)
                .usuarioRegistro(ingreso.getUsuarioRegistro())
                .createdAt(ingreso.getCreatedAt())
                .build();
    }

    private InventarioExteriorResponse mapToExtResponse(InventarioExterior ext) {
        return InventarioExteriorResponse.builder()
                .id(ext.getId())
                .parachoquesDelantero(ext.getParachoquesDelantero())
                .parachoquesTrasero(ext.getParachoquesTrasero())
                .puertas(ext.getPuertas())
                .espejos(ext.getEspejos())
                .vidrios(ext.getVidrios())
                .luces(ext.getLuces())
                .llantas(ext.getLlantas())
                .rayones(ext.getRayones())
                .golpes(ext.getGolpes())
                .observaciones(ext.getObservaciones())
                .build();
    }

    private InventarioInteriorResponse mapToInterResponse(InventarioInterior inter) {
        return InventarioInteriorResponse.builder()
                .id(inter.getId())
                .tapiceria(inter.getTapiceria())
                .tablero(inter.getTablero())
                .radioPantalla(inter.getRadioPantalla())
                .alfombras(inter.getAlfombras())
                .cinturones(inter.getCinturones())
                .elementosPersonales(inter.getElementosPersonales())
                .observaciones(inter.getObservaciones())
                .build();
    }

    private InventarioSeguridadResponse mapToSegResponse(InventarioSeguridad seg) {
        return InventarioSeguridadResponse.builder()
                .id(seg.getId())
                .llantaRepuesto(seg.getLlantaRepuesto())
                .gato(seg.getGato())
                .cruceta(seg.getCruceta())
                .extintor(seg.getExtintor())
                .botiquin(seg.getBotiquin())
                .triangulos(seg.getTriangulos())
                .herramientas(seg.getHerramientas())
                .otros(seg.getOtros())
                .build();
    }

    private boolean boolOrDefault(Boolean value, boolean defaultValue) {
        return value != null ? value : defaultValue;
    }
}