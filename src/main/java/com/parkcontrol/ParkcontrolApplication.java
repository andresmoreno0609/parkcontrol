package com.parkcontrol;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ParkcontrolApplication {

    private static final Logger log = LoggerFactory.getLogger(ParkcontrolApplication.class);

    public static void main(String[] args) {
        var context = SpringApplication.run(ParkcontrolApplication.class, args);
        printBanner(context);
    }

    private static void printBanner(org.springframework.context.ConfigurableApplicationContext context) {
        var env = context.getEnvironment();
        String profile = env.getProperty("spring.profiles.active", "dev");
        String appName = env.getProperty("spring.application.name", "parkcontrol");

        log.info("╔══════════════════════════════════════════════════════════════╗");
        log.info("║                                                              ║");
        log.info("║   ██████╗ ██╗   ██╗██╗     ███████╗███████╗██╗              ║");
        log.info("║   ██╔══██╗██║   ██║██║     ██╔════╝██╔════╝██║              ║");
        log.info("║   ██████╔╝██║   ██║██║     ███████╗█████╗  ██║              ║");
        log.info("║   ██╔═══╝ ██║   ██║██║     ╚════██║██╔══╝  ██║              ║");
        log.info("║   ██║     ╚██████╔╝███████╗███████║██║     ███████╗         ║");
        log.info("║   ╚═╝      ╚═════╝ ╚══════╝╚══════╝╚═╝     ╚══════╝         ║");
        log.info("║                                                              ║");
        log.info("║   ██████╗ ███████╗███╗   ██╗███████╗ ██████╗ ██████╗ ██╗   ║");
        log.info("║  ██╔════╝ ██╔════╝████╗  ██║██╔════╝██╔═══██╗██╔══██╗██║   ║");
        log.info("║  ██║  ███╗█████╗  ██╔██╗ ██║█████╗  ██║   ██║██████╔╝██║   ║");
        log.info("║  ██║   ██║██╔══╝  ██║╚██╗██║██╔══╝  ██║   ██║██╔══██╗██║   ║");
        log.info("║  ╚██████╔╝███████╗██║ ╚████║███████╗╚██████╔╝██║  ██║██████╗║");
        log.info("║   ╚═════╝ ╚══════╝╚═╝  ╚═══╝╚══════╝ ╚═════╝ ╚═╝  ╚═╝╚═════╝║");
        log.info("║                                                              ║");
        log.info("╚══════════════════════════════════════════════════════════════╝");
        log.info("╔══════════════════════════════════════════════════════════════╗");
        log.info("║  ▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓  ║");
        log.info("║  ▓  PROYECTO: {}                                     ▓  ║", appName.toUpperCase());
        log.info("║  ▓  AMBIENTE: {}                                       ▓  ║", profile.toUpperCase());
        log.info("║  ▓  MODO:      {}                                          ▓  ║", "DESARROLLO".equalsIgnoreCase(profile) ? "DEV" : "PRODUCCION");
        log.info("║  ▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓  ║");
        log.info("╚══════════════════════════════════════════════════════════════╝");

        if ("dev".equalsIgnoreCase(profile)) {
            log.info("╔══════════════════════════════════════════════════════════════╗");
            log.info("║  ⚠️  MODO DESARROLLO - ADVERTENCIA DE DEBUG HABILITADO        ║");
            log.info("║  ⚠️  NO USAR EN PRODUCCIÓN                                     ║");
            log.info("╚══════════════════════════════════════════════════════════════╝");
        } else if ("prod".equalsIgnoreCase(profile)) {
            log.info("╔══════════════════════════════════════════════════════════════╗");
            log.info("║  🛡️  MODO PRODUCCIÓN - NIVEL DE LOG REDUCIDO                  ║");
            log.info("║  🛡️  Solo errores y advertencias en consola                 ║");
            log.info("╚══════════════════════════════════════════════════════════════╝");
        }

        log.info("▶ Inicializacion completada");
    }
}