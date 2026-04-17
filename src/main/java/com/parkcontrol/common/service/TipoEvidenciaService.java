package com.parkcontrol.common.service;

import com.parkcontrol.common.dto.tipoEvidencia.CrearTipoEvidenciaRequest;
import com.parkcontrol.common.dto.tipoEvidencia.TipoEvidenciaResponse;
import com.parkcontrol.common.entity.TipoEvidencia;
import com.parkcontrol.common.repository.TipoEvidenciaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TipoEvidenciaService {

    private final TipoEvidenciaRepository tipoEvidenciaRepository;

    public List<TipoEvidenciaResponse> findAllActivos() {
        return tipoEvidenciaRepository.findByActivoTrueOrderByOrdenAsc().stream()
                .map(this::toResponse)
                .toList();
    }

    public List<TipoEvidenciaResponse> findAll() {
        return tipoEvidenciaRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public TipoEvidenciaResponse create(CrearTipoEvidenciaRequest request) {
        TipoEvidencia tipoEvidencia = TipoEvidencia.builder()
                .nombre(request.getNombre())
                .descripcion(request.getDescripcion())
                .orden(request.getOrden() != null ? request.getOrden() : 0)
                .activo(true)
                .build();

        TipoEvidencia saved = tipoEvidenciaRepository.save(tipoEvidencia);
        return toResponse(saved);
    }

    public TipoEvidencia findById(Long id) {
        return tipoEvidenciaRepository.findById(id).orElse(null);
    }

    private TipoEvidenciaResponse toResponse(TipoEvidencia tipoEvidencia) {
        return TipoEvidenciaResponse.builder()
                .id(tipoEvidencia.getId())
                .nombre(tipoEvidencia.getNombre())
                .descripcion(tipoEvidencia.getDescripcion())
                .orden(tipoEvidencia.getOrden())
                .build();
    }
}