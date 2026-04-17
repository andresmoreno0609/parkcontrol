package com.parkcontrol.common.service;

import com.parkcontrol.common.dto.salida.*;
import com.parkcontrol.common.dto.ingreso.*;
import com.parkcontrol.common.entity.*;
import com.parkcontrol.common.repository.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SalidaService {

    private static final Logger log = LoggerFactory.getLogger(SalidaService.class);

    private final SalidaRepository salidaRepository;
    private final IngresoRepository ingresoRepository;
    private final InventarioExteriorRepository inventarioExteriorRepository;
    private final InventarioInteriorRepository inventarioInteriorRepository;
    private final InventarioSeguridadRepository inventarioSeguridadRepository;
    private final EvidenciaSalidaRepository evidenciaSalidaRepository;
    private final TipoEvidenciaRepository tipoEvidenciaRepository;

    private final IngresoService ingresoService;
    private final TarifaService tarifaService;

    @Transactional
    public SalidaResponse create(SalidaRequest request, String usuarioRegistro) {
        log.info("Registrando salida para ingreso ID: {}", request.ingresoId());

        // 1. Validar que el ingreso existe y está activo
        Ingreso ingreso = ingresoRepository.findById(request.ingresoId())
                .orElseThrow(() -> new IllegalArgumentException("Ingreso no encontrado"));

        if (!"ACTIVO".equals(ingreso.getEstado())) {
            throw new IllegalArgumentException("El ingreso no está activo");
        }

        // 2. Validar que no exista salida ya
        if (salidaRepository.existsByIngresoId(request.ingresoId())) {
            throw new IllegalArgumentException("Ya existe una salida para este ingreso");
        }

        // 3. Calcular monto
        BigDecimal monto = calcularMonto(ingreso);
        log.debug("Monto calculado: {}", monto);

        // 4. Crear salida
        Salida salida = Salida.builder()
                .ingresoId(request.ingresoId())
                .fechaSalida(LocalDate.now())
                .horaSalida(LocalTime.now())
                .personaRetira(request.personaRetira())
                .documentoRetira(request.documentoRetira())
                .observaciones(request.observaciones())
                .montoPagado(monto)
                .usuarioRegistro(usuarioRegistro)
                .build();

        Salida savedSalida = salidaRepository.save(salida);
        log.debug("Salida guardada con ID: {}", savedSalida.getId());

        // 5. Actualizar estado del ingreso a EXITO
        ingresoService.actualizarEstado(request.ingresoId(), "EXITO");

        // 6. Guardar checklists de salida (opcional)
        if (request.inventarioExterior() != null) {
            InventarioExterior ext = mapToInventarioExterior(request.inventarioExterior(), savedSalida.getId());
            inventarioExteriorRepository.save(ext);
        }

        if (request.inventarioInterior() != null) {
            InventarioInterior inter = mapToInventarioInterior(request.inventarioInterior(), savedSalida.getId());
            inventarioInteriorRepository.save(inter);
        }

        if (request.inventarioSeguridad() != null) {
            InventarioSeguridad seg = mapToInventarioSeguridad(request.inventarioSeguridad(), savedSalida.getId());
            inventarioSeguridadRepository.save(seg);
        }

        // 7. Guardar evidencias de salida (si hay)
        if (request.evidencias() != null && !request.evidencias().isEmpty()) {
            for (EvidenciaRequest evReq : request.evidencias()) {
                EvidenciaSalida ev = EvidenciaSalida.builder()
                        .salidaId(savedSalida.getId())
                        .tipoId(evReq.tipoId())
                        .rutaArchivo(evReq.rutaArchivo())
                        .build();
                evidenciaSalidaRepository.save(ev);
            }
        }

        log.info("Salida registrada exitosamente: {}", savedSalida.getId());

        return SalidaResponse.builder()
                .id(savedSalida.getId())
                .ingresoId(savedSalida.getIngresoId())
                .fechaSalida(savedSalida.getFechaSalida())
                .horaSalida(savedSalida.getHoraSalida())
                .personaRetira(savedSalida.getPersonaRetira())
                .documentoRetira(savedSalida.getDocumentoRetira())
                .montoPagado(savedSalida.getMontoPagado())
                .estadoEntrega("ENTREGADO")
                .observaciones(savedSalida.getObservaciones())
                .build();
    }

    public SalidaDetalleResponse findDetalleById(Long id) {
        Salida salida = salidaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Salida no encontrada"));

        Ingreso ingreso = ingresoRepository.findById(salida.getIngresoId())
                .orElseThrow(() -> new IllegalArgumentException("Ingreso no encontrado"));

        Optional<Vehiculo> vehiculo = ingresoRepository.findById(ingreso.getId())
                .map(i -> null); //-placeholder

        // Obtener vehículo y persona del ingreso
        // Nota: aquí necesitaríamos los servicios correspondientes

        List<EvidenciaSalida> evidencias = evidenciaSalidaRepository.findBySalidaId(id);

        // Calcular tiempo total
        Duration duration = Duration.between(ingreso.getHoraIngreso(), salida.getHoraSalida());
        long horas = duration.toHours();
        long minutos = duration.toMinutes() % 60;
        String tiempoTotal = String.format("%dh %02dm", horas, minutos);

        return SalidaDetalleResponse.builder()
                .id(salida.getId())
                .ingresoId(salida.getIngresoId())
                .numeroRegistro(ingreso.getNumeroRegistro())
                .placa("") // TODO: obtener del vehículo
                .fechaIngreso(ingreso.getFechaIngreso())
                .horaIngreso(ingreso.getHoraIngreso())
                .fechaSalida(salida.getFechaSalida())
                .horaSalida(salida.getHoraSalida())
                .personaRetira(salida.getPersonaRetira())
                .documentoRetira(salida.getDocumentoRetira())
                .tiempoTotal(tiempoTotal)
                .montoPagado(salida.getMontoPagado())
                .observaciones(salida.getObservaciones())
                .evidencias(evidencias.stream().map(this::toEvidenciaResponse).toList())
                .usuarioRegistro(salida.getUsuarioRegistro())
                .build();
    }

    private BigDecimal calcularMonto(Ingreso ingreso) {
        // Por ahora, si es tipo HORA calculamos por horas
        // TODO: implementar lógica completa de tarifas

        if ("MENSUAL".equals(ingreso.getTipoAcceso())) {
            // Tarifas mensuales se pagan por mes completo
            Optional<BigDecimal> tarifa = tarifaService.findByTipoVehiculoAndTipoAcceso(
                    "AUTOMOVIL", "MENSUAL"
            ).map(t -> t.getValor());
            return tarifa.orElse(BigDecimal.ZERO);
        }

        // Para otros tipos, buscar tarifa
        Optional<BigDecimal> tarifa = tarifaService.findByTipoVehiculoAndTipoAcceso(
                "AUTOMOVIL", ingreso.getTipoAcceso()
        ).map(t -> t.getValor());

        return tarifa.orElse(BigDecimal.ZERO);
    }

    private EvidenciaResponse toEvidenciaResponse(EvidenciaSalida ev) {
        Optional<TipoEvidencia> tipo = tipoEvidenciaRepository.findById(ev.getTipoId());
        return EvidenciaResponse.builder()
                .id(ev.getId())
                .tipoId(ev.getTipoId())
                .rutaArchivo(ev.getRutaArchivo())
                .tipoNombre(tipo.map(TipoEvidencia::getNombre).orElse(null))
                .build();
    }

    private InventarioExterior mapToInventarioExterior(SalidaRequest.InventarioExteriorRequest req, Long salidaId) {
        return InventarioExterior.builder()
                .ingresoId(salidaId) // reutilizamos, pero es de salida
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

    private InventarioInterior mapToInventarioInterior(SalidaRequest.InventarioInteriorRequest req, Long salidaId) {
        return InventarioInterior.builder()
                .ingresoId(salidaId)
                .tapiceria(boolOrDefault(req.tapiceria(), false))
                .tablero(boolOrDefault(req.tablero(), false))
                .radioPantalla(boolOrDefault(req.radioPantalla(), false))
                .alfombras(boolOrDefault(req.alfombras(), false))
                .cinturones(boolOrDefault(req.cinturones(), false))
                .elementosPersonales(boolOrDefault(req.elementosPersonales(), false))
                .observaciones(req.observaciones())
                .build();
    }

    private InventarioSeguridad mapToInventarioSeguridad(SalidaRequest.InventarioSeguridadRequest req, Long salidaId) {
        return InventarioSeguridad.builder()
                .ingresoId(salidaId)
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

    private boolean boolOrDefault(Boolean value, boolean defaultValue) {
        return value != null ? value : defaultValue;
    }
}