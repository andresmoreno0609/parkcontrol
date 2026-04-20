package com.parkcontrol.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app.externo")
@Getter
@Setter
public class ExternalApiProperties {

    private String personaUrl = "http://localhost:9999/api/personas";
    private String vehiculoUrl = "http://localhost:9999/api/vehiculos";
    private int timeout = 5000;

    // Flags para habilitar/deshabilitar cada consulta
    private boolean personaEnabled = true;
    private boolean vehiculoEnabled = true;
}