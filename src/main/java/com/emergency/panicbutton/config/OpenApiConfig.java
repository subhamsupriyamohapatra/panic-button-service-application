package com.emergency.panicbutton.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "PANIC-BUTTON-SERVICE",
                version = "1.0.0",
                description = "Production-grade microservice for managing users, family "
                        + "relationships, FCM devices, and triggering emergency panic "
                        + "events dispatched to the external .NET Middleware API."
        )
)
@SecurityScheme(
        name = "HTTPBearer",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        description = "Enter your JWT access token"
)
public class OpenApiConfig {
}
