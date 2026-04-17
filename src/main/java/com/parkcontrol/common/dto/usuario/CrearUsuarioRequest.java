package com.parkcontrol.common.dto.usuario;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CrearUsuarioRequest {
    private String username;
    private String password;
    private String rol;
    private Long personaId;
}