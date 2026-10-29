package com.example.booksapi.config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenApiConfiguration {
    @Bean
    fun booksAuthorsOpenApi(): OpenAPI = OpenAPI()
        .info(
            Info()
                .title("Books & Authors API")
                .description("API documentation for the Books and Authors application")
                .version("1.0.0"),
        )
}
