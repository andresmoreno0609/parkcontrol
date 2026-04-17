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
        log.info("Registrando salida para ingreso ID: {}", request.getIngresoId());

        // 1. Validar que el ingreso existe y está activo
        Ingreso ingreso = ingresoRepository.findById(request.getIngresoId())
                .orElseThrow(() -> new IllegalArgumentException("Ingreso no encontrado"));

        if (!"ACTIVO".equals(ingreso.getEstado())) {
            throw new IllegalArgumentException("El ingreso no está activo");
        }

        // 2. Validar que no exista salida ya
        if (salidaRepository.existsByIngresoId(request.getIngresoId())) {
            throw new IllegalArgumentException("Ya existe una salida para este ingreso");
        }

        // 3. Calcular monto
        BigDecimal monto = calcularMonto(ingreso);
        log.debug("Monto calculado: {}", monto);

        // 4. Crear salida
        Salida salida = Salida.builder()
                .ingresoId(request.getIngresoId())
                .fechaSalida(LocalDate.now())
                .horaSalida(LocalTime.now())
                .personaRetira(request.getPersonaRetira())
                .documentoRetira(request.getDocumentoRetira())
                .observaciones(request.getObservaciones())
                .montoPagado(monto)
                .usuarioRegistro(usuarioRegistro)
                .build();

        Salida savedSalida = salidaRepository.save(salida);
        log.debug("Salida guardada con ID: {}", savedSalida.getId());

        // 5. Actualizar estado del ingreso a EXITO
        ingresoService.actualizarEstado(request.getIngresoId(), "EXITO");

        // 6. Guardar checklists de salida (opcional)
        if (request.getInventarioExterior() != null) {
            InventarioExterior ext = mapToInventarioExterior(request, savedSalida.getId());
            if (ext != null) inventarioExteriorRepository.save(ext);
        }

        if (request.getInventarioInterior() != null) {
            InventarioInterior inter = mapToInventarioInterior(request, savedSalida.getId());
            if (inter != null) inventarioInteriorRepository.save(inter);
        }

        if (request.getInventarioSeguridad() != null) {
            InventarioSeguridad seg = mapToInventarioSeguridad(request, savedSalida.getId());
            if (seg != null) inventarioSeguridadRepository.save(seg);
        }

        // 7. Guardar evidencias de salida (si hay)
        if (request.getEvidencias() != null && !request.getEvidencias().isEmpty()) {
            for (EvidenciaRequest evReq : request.getEvidencias()) {
                EvidenciaSalida ev = EvidenciaSalida.builder()
                        .salidaId(savedSalida.getId())
                        .tipoId(evReq.getTipoId())
                        .rutaArchivo(evReq.getRutaArchivo())
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
                .placa("")
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
        if ("MENSUAL".equals(ingreso.getTipoAcceso())) {
            Optional<BigDecimal> tarifa = tarifaService.findByTipoVehiculoAndTipoAcceso(
                    "AUTOMOVIL", "MENSUAL"
            ).map(t -> t.getValor());
            return tarifa.orElse(BigDecimal.ZERO);
        }

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

    private InventarioExterior mapToInventarioExterior(SalidaRequest req, Long salidaId) {
        InventarioExteriorRequest inv = req.getInventarioExterior();
        if (inv == null) return null;
        return InventarioExterior.builder()
                .ingresoId(salidaId)
                .parachoquesDelantero(boolOrDefault(inv.getParachoquesDelantero(), false))
                .parachoquesTrasero(boolOrDefault(inv.getParachoquesTrasero(), false))
                .puertas(boolOrDefault(inv.getPuertas(), false))
                .espejos(boolOrDefault(inv.getEspejos(), false))
                .vidrios(boolOrDefault(inv.getVidrios(), false))
                .luces(boolOrDefault(inv.getLuces(), false))
                .llantas(boolOrDefault(inv.getLlantas(), false))
                .rayones(boolOrDefault(inv.getRayones(), false))
                .golpes(boolOrDefault(inv.getGolpes(), false))
                .observaciones(inv.getObservaciones())
                .build();
    }

    private InventarioInterior mapToInventarioInterior(SalidaRequest req, Long salidaId) {
        InventarioInteriorRequest inv = req.getInventarioInterior();
        if (inv == null) return null;
        return InventarioInterior.builder()
                .ingresoId(salidaId)
                .tapiceria(boolOrDefault(inv.getTapiceria(), false))
                .tablero(boolOrDefault(inv.getTablero(), false))
                .radioPantalla(boolOrDefault(inv.getRadioPantalla(), false))
                .alfombras(boolOrDefault(inv.getAlfombras(), false))
                .cinturones(boolOrDefault(inv.getCinturones(), false))
                .elementosPersonales(boolOrDefault(inv.getElementosPersonales(), false))
                .observaciones(inv.getObservaciones())
                .build();
    }

    private InventarioSeguridad mapToInventarioSeguridad(SalidaRequest req, Long salidaId) {
        InventarioSeguridadRequest inv = req.getInventarioSeguridad();
        if (inv == null) return null;
        return InventarioSeguridad.builder()
                .ingresoId(salidaId)
                .llantaRepuesto(boolOrDefault(inv.getLlantaRepuesto(), false))
                .gato(boolOrDefault(inv.getGato(), false))
                .cruceta(boolOrDefault(inv.getCruceta(), false))
                .extintor(boolOrDefault(inv.getExtintor(), false))
                .botiquin(boolOrDefault(inv.getBotiquin(), false))
                .triangulos(boolOrDefault(inv.getTriangulos(), false))
                .herramientas(boolOrDefault(inv.getHerramientas(), false))
                .otros(inv.getOtros())
                .build();
    }

    private boolean boolOrDefault(Boolean value, boolean defaultValue) {
        return value != null ? value : defaultValue;
    }
}