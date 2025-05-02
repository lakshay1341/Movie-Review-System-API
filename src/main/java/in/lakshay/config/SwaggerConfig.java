package in.lakshay.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi; // fixed import after springdoc update
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// swagger/openapi config for api docs
@Configuration
public class SwaggerConfig {
    // main openapi config
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Movie Review & Reservation System API")
                        .version("2.0") // bumped from 1.0
                        .description("API for managing movie reviews and reservations"))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth")) // jwt auth
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                        )
                );
    }

    // all apis
    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("public")
                .pathsToMatch("/**") // match everything
                .build();
    }

    // just the movie/review endpoints
    @Bean
    public GroupedOpenApi reviewsApi() {
        return GroupedOpenApi.builder()
                .group("reviews")
                .pathsToMatch("/api/v1/movies/**", "/api/v1/reviews/**")
                .build();
    }

    // just reservation related endpoints
    @Bean
    public GroupedOpenApi reservationsApi() {
        return GroupedOpenApi.builder()
                .group("reservations")
                .pathsToMatch("/api/v1/theaters/**", "/api/v1/showtimes/**", "/api/v1/seats/**", "/api/v1/reservations/**") // todo: add payments?
                .build();
    }
}