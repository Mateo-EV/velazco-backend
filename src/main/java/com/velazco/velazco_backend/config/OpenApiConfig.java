package com.velazco.velazco_backend.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class OpenApiConfig {

  @Value("${base.url}")
  private String backendBaseUrl;

  @Bean
  OpenAPI customOpenAPI() {
    final String securitySchemeName = "bearerAuth";

    Server server = new Server();
    server.setUrl(backendBaseUrl);

    return new OpenAPI()
        .info(new Info()
            .title("Velazco API")
            .version("1.0")
            .description("Documentación de la API"))
        .servers(List.of(server))
        .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
        .components(
            new Components().addSecuritySchemes(securitySchemeName,
                new SecurityScheme()
                    .name(securitySchemeName)
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT")));
  }
}
