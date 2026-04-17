package com.parkcontrol.common.service;

import com.parkcontrol.common.entity.Persona;
import com.parkcontrol.common.repository.PersonaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PersonaService {

    private final PersonaRepository personaRepository;

    @Transactional
    public Persona crearOActualizar(String tipoDocumento, String numeroDocumento,
                                    String nombres, String correo, String telefono,
                                    String sexo, LocalDate fechaNacimiento) {

        Optional<Persona> existente = personaRepository.findByNumeroDocumento(numeroDocumento);

        if (existente.isPresent()) {
            Persona persona = existente.get();
            // Actualizar solo si viene información
            if (nombres != null) persona.setNombres(nombres);
            if (correo != null) persona.setCorreo(correo);
            if (telefono != null) persona.setTelefono(telefono);
            if (sexo != null) persona.setSexo(sexo);
            if (fechaNacimiento != null) persona.setFechaNacimiento(fechaNacimiento);
            return personaRepository.save(persona);
        } else {
            Persona nueva = Persona.builder()
                    .tipoDocumento(tipoDocumento)
                    .numeroDocumento(numeroDocumento)
                    .nombres(nombres)
                    .correo(correo)
                    .telefono(telefono)
                    .sexo(sexo)
                    .fechaNacimiento(fechaNacimiento)
                    .build();
            return personaRepository.save(nueva);
        }
    }

    public Optional<Persona> findByNumeroDocumento(String numeroDocumento) {
        return personaRepository.findByNumeroDocumento(numeroDocumento);
    }

    public Optional<Persona> findById(Long id) {
        return personaRepository.findById(id);
    }
}