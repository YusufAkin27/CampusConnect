package friend_service.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI / Swagger documentation configuration for friend-service.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI friendServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("CampusConnect - Friend Service API")
                        .description("""
                                Friend Service manages all social relationship operations for CampusConnect.
                                
                                Responsibilities:
                                - Friend requests (send, accept, reject, cancel)
                                - Friendship management (list friends, remove friend, mutual friends)
                                - Follow/unfollow system (follow, unfollow, followers, following)
                                - Social graph operations (suggestions, relation status, social stats)
                                - Internal endpoints for inter-service communication
                                
                                NOTE: Block/unblock operations are NOT part of this service.
                                They are managed by the dedicated block-service.
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
                                .url("http://localhost:8084")
                                .description("Local Development Server"),
                        new Server()
                                .url("http://api-gateway:8080")
                                .description("API Gateway (via Consul)")
                ));
    }
}
