package com.mediaflow.api.configuration;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        // Definir el esquema de seguridad JWT
        final String securitySchemeName = "bearerAuth";
        
        return new OpenAPI()
                // Información general de la API
                .info(new Info()
                        .title("MediaFlow API")
                        .description("API REST para gestión de contenido multimedia con autenticación JWT")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("MediaFlow Team")
                                .email("support@mediaflow.com")
                                .url("https://mediaflow.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
                
                // Servidores
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Servidor de Desarrollo"),
                        new Server()
                                .url("https://api.mediaflow.com")
                                .description("Servidor de Producción")))
                
                // Configuración de seguridad JWT
                .addSecurityItem(new SecurityRequirement()
                        .addList(securitySchemeName))
                
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName, new SecurityScheme()
                                .name(securitySchemeName)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Ingrese el token JWT obtenido del endpoint /api/v1/users/login\n\n" +
                                           "Ejemplo: eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")));
    }
}
