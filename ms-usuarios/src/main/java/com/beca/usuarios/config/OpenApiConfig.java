package com.beca.usuarios.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;


    @Configuration
    @OpenAPIDefinition(
            info = @Info(
                    title = "User Service API",
                    version = "1.0",
                    description = "User management and CSV import API"
            )
    )
    @SecurityScheme(
            name = "basicAuth",
            type = SecuritySchemeType.HTTP,
            scheme = "basic"
    )
    public class OpenApiConfig {

}
