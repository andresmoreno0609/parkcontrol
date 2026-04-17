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
        log.info("Creando ingreso para placa: {}", request.placa());

        // 1. Buscar o crear persona (conductor)
        Persona persona = personaService.crearOActualizar(
                request.tipoDocumentoConductor(),
                request.numeroDocumentoConductor(),
                null, null, null, null, null // Los datos vienen del sistema externo si existe
        );
        log.debug("Persona creada/encontrada: {}", persona.getId());

        // 2. Buscar o crear vehículo
        Vehiculo vehiculo = vehiculoService.crearOActualizar(
                request.placa(),
                request.marca(),
                request.modelo(),
                request.referencia(),
                request.color(),
                request.tipoVehiculo(),
                request.servicio()
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
                .tipoAcceso(request.tipoAcceso())
                .cupoAsignado(request.cupoAsignado())
                .kilometraje(request.kilometraje())
                .motivoIngreso(request.motivoIngreso())
                .observaciones(request.observaciones())
                .firmaConductor(request.firmaConductor() != null ? request.firmaConductor() : false)
                .firmaOperario(request.firmaOperario() != null ? request.firmaOperario() : false)
                .estado("ACTIVO")
                .usuarioRegistro(usuarioRegistro)
                .build();

        Ingreso savedIngreso = ingresoRepository.save(ingreso);
        log.debug("Ingreso guardado con ID: {}", savedIngreso.getId());

        // 5. Guardar inventarios
        if (request.inventarioExterior() != null) {
            InventarioExterior ext = mapToInventarioExterior(request.inventarioExterior(), savedIngreso.getId());
            inventarioExteriorRepository.save(ext);
        }

        if (request.inventarioInterior() != null) {
            InventarioInterior inter = mapToInventarioInterior(request.inventarioInterior(), savedIngreso.getId());
            inventarioInteriorRepository.save(inter);
        }

        if (request.inventarioSeguridad() != null) {
            InventarioSeguridad seg = mapToInventarioSeguridad(request.inventarioSeguridad(), savedIngreso.getId());
            inventarioSeguridadRepository.save(seg);
        }

        // 6. Guardar evidencias (si hay)
        if (request.evidencias() != null && !request.evidencias().isEmpty()) {
            for (EvidenciaRequest evReq : request.evidencias()) {
                EvidenciaIngreso ev = EvidenciaIngreso.builder()
                        .ingresoId(savedIngreso.getId())
                        .tipoId(evReq.tipoId())
                        .rutaArchivo(evReq.rutaArchivo())
                        .build();
                evidenciaIngresoRepository.save(ev);
            }
        }

        log.info("Ingreso creado exitosamente: {}", savedIngreso.getId());
        return toResponse(savedIngreso, vehiculo, persona);
    }

    public Optional<Ingreso> findActivoByPlaca(String placa) {
        return ingresoRepository.findFirstByPlacaActivo(placa);
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

    private InventarioExterior mapToInventarioExterior(IngresoRequest.InventarioExteriorRequest req, Long ingresoId) {
        return InventarioExterior.builder()
                .ingresoId(ingresoId)
                .parachoquesDelantero(boolOrDefault(req.parachoquesDelantero(), false))
                .parachoquesTrasero(boolOrDefault(req.parachoquesTrasero(), false))
                .puertas(boolOrDefault(req.puertas(), false))
                .espejos(boolOrDefault(req.espejos(), false))
                .vidrios(boolOrDefault(req.vidrios(), false))
                .luces(boolOrDefault(req.luces(), false))
                .llantas(boolOrDefault(req.llantas(), false))
                .rayones(boolOrDefault(req.rayones(), false))
                .golpes(boolOrDefault(req.golpes(), false))
                .observaciones(req.observaciones())
                .build();
    }

    private InventarioInterior mapToInventarioInterior(IngresoRequest.InventarioInteriorRequest req, Long ingresoId) {
        return InventarioInterior.builder()
                .ingresoId(ingresoId)
                .tapiceria(boolOrDefault(req.tapiceria(), false))
                .tablero(boolOrDefault(req.tablero(), false))
                .radioPantalla(boolOrDefault(req.radioPantalla(), false))
                .alfombras(boolOrDefault(req.alfombras(), false))
                .cinturones(boolOrDefault(req.cinturones(), false))
                .elementosPersonales(boolOrDefault(req.elementosPersonales(), false))
                .observaciones(req.observaciones())
                .build();
    }

    private InventarioSeguridad mapToInventarioSeguridad(IngresoRequest.InventarioSeguridadRequest req, Long ingresoId) {
        return InventarioSeguridad.builder()
                .ingresoId(ingresoId)
                .llantaRepuesto(boolOrDefault(req.llantaRepuesto(), false))
                .gato(boolOrDefault(req.gato(), false))
                .cruceta(boolOrDefault(req.cruceta(), false))
                .extintor(boolOrDefault(req.extintor(), false))
                .botiquin(boolOrDefault(req.botiquin(), false))
                .triangulos(boolOrDefault(req.triangulos(), false))
                .herramientas(boolOrDefault(req.herramientas(), false))
                .otros(req.otros())
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