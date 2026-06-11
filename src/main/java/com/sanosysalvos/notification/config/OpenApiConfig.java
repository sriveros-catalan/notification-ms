package com.sanosysalvos.notification.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de OpenAPI / Swagger UI.
 *
 * Define la metadata que se muestra en {@code /swagger-ui.html}.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI notificationOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Notification Microservice API")
                        .description("Microservicio de notificaciones para coincidencias de mascotas. "
                                + "Aplica los patrones Factory Method (creación de notificaciones por tipo de evento) "
                                + "y Strategy (entrega por múltiples canales).")
                        .version("v1.0.0")
                        .contact(new Contact().name("Sanos y Salvos")));
    }
}
