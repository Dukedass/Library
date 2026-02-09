package com.example.librarymanagement.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "Library Management System API",
        version = "1.0.0",
        description = "Complete CRUD API for managing library books with real-time documentation",
        contact = @Contact(name = "Library Admin", email = "admin@library.com"),
        license = @License(name = "MIT License", url = "https://opensource.org/licenses/MIT")
    ),
    servers = {
        @Server(url = "http://localhost:8080", description = "Development Server"),
        @Server(url = "https://library-api.com", description = "Production Server")
    }
)
public class SwaggerConfig {
}
