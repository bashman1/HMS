package com.hms.auth.infrastructure.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI 3 documentation configuration.
 */
@Configuration
@OpenAPIDefinition(info = @Info(title = "HMS Authentication Service API", version = "1.0.0", description = """
        Production-ready authentication microservice providing comprehensive
        authentication and authorization capabilities.

        ## Features
        - JWT-based authentication with access and refresh tokens
        - User registration with email verification
        - Login with rate limiting and brute force protection
        - Password reset functionality
        - Role-based access control (RBAC)

        ## Authentication
        Most endpoints require authentication using a Bearer token.
        Include the token in the Authorization header:
        ```
        Authorization: Bearer <your-access-token>
        ```
        """, contact = @Contact(name = "HMS Team", email = "support@hms.com", url = "https://hms.com"), license = @License(name = "MIT License", url = "https://opensource.org/licenses/MIT")), servers = {
        @Server(url = "/api/v1", description = "Default Server")
})
@SecurityScheme(name = "bearerAuth", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT", description = "JWT authentication token")
public class OpenApiConfig {
}
