package com.banka1.transaction_service.swagger;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring konfiguracija za OpenAPI (Swagger) dokumentaciju.
 * Konfigurira OpenAPI specifikaciju sa JWT Bearer token autentifikacijom.
 */
@Configuration
public class SwaggerConfig {

    /**
     * Kreira OpenAPI bean sa osnovnom informacijom o servisu i JWT sigurnosnom šemom.
     * <p>
     * OpenAPI spec se može pregledati na:
     * <ul>
     *   <li>Swagger UI: /swagger-ui.html</li>
     *   <li>OpenAPI JSON: /v3/api-docs</li>
     *   <li>OpenAPI YAML: /v3/api-docs.yaml</li>
     * </ul>
     *
     * @return konfigurisani OpenAPI bean
     */
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Transaction service API")
                        .description("API for transactions")
                        .version("1.0"))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")));
    }
}
