package media_service.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Value("${server.port:8087}")
    private String serverPort;

    @Bean
    public OpenAPI mediaServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("CampusConnect Media Service API")
                        .description("""
                                Media Service is responsible for all file and media operations in the CampusConnect platform.
                                
                                **Responsibilities:**
                                - Profile & cover image upload
                                - Post media upload (images, videos, GIFs)
                                - Event poster upload
                                - Chat media upload
                                - MIME type, file size, and extension validation
                                - Soft delete and media lifecycle management
                                - Inter-service media validation (used by post-service, user-service, event-service)
                                - Media usage tracking
                                
                                **Authentication:** Pass `X-Auth-User-Id` header with the authenticated user's ID.
                                """)
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("CampusConnect Team")
                                .email("support@campusconnect.com"))
                        .license(new License().name("Private")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:" + serverPort)
                                .description("Local Development Server")
                ));
    }
}
