package user_service.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI / Swagger configuration for user-service.
 * Accessible at: http://localhost:8082/swagger-ui.html
 */
@Configuration
public class OpenApiConfig {

    private static final String BEARER_SCHEME = "bearerAuth";

    @Bean
    public OpenAPI userServiceOpenAPI() {
        return new OpenAPI()
                .info(buildInfo())
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8082")
                                .description("Local Development Server"),
                        new Server()
                                .url("http://localhost:8080/v1/api/users")
                                .description("Via API Gateway (local)")
                ))
                .addSecurityItem(new SecurityRequirement().addList(BEARER_SCHEME))
                .components(new Components()
                        .addSecuritySchemes(BEARER_SCHEME, buildBearerSecurityScheme())
                );
    }

    private Info buildInfo() {
        return new Info()
                .title("CampusConnect - User Service API")
                .description("""
                        User profile management microservice for CampusConnect.
                        
                        Responsibilities:
                        - Create and manage user profiles
                        - Profile visibility control (PUBLIC / PRIVATE / FRIENDS_ONLY)
                        - Student information (faculty, department, grade)
                        - Social links and bio management
                        - Internal endpoints for service-to-service communication
                        
                        Authentication: JWT Bearer Token (issued by auth-service).
                        The authUserId claim in the token identifies the user.
                        """)
                .version("1.0.0")
                .contact(new Contact()
                        .name("CampusConnect Team")
                        .email("dev@campusconnect.com")
                )
                .license(new License()
                        .name("Private")
                        .url("https://campusconnect.com")
                );
    }

    private SecurityScheme buildBearerSecurityScheme() {
        return new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .description("Enter your JWT Bearer token. Obtained from auth-service /v1/api/auth/login.");
    }
}
