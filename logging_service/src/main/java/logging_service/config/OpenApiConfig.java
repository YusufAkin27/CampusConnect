package logging_service.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI loggingServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("CampusConnect Logging Service API")
                        .description("""
                                Centralized logging microservice for the CampusConnect platform.
                                
                                Responsibilities:
                                - Application log collection and storage
                                - API request/response logging
                                - Error and exception tracking
                                - Security event logging
                                - Audit trail management
                                - Log statistics and analytics
                                - Retention policy management
                                - Internal endpoints for microservice log ingestion
                                
                                Base path: /v1/api/logs
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("CampusConnect Team")
                                .email("dev@campusconnect.com"))
                        .license(new License()
                                .name("Private")
                                .url("https://campusconnect.com")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8085")
                                .description("Local Development Server")
                ));
    }
}
