package post_service.config;

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
    public OpenAPI postServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("CampusConnect Post Service API")
                        .description("Social campus post management microservice. " +
                                "Handles posts, comments, reactions, saved posts, hashtags, mentions, and reports.")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("CampusConnect Team")
                                .email("dev@campusconnect.com"))
                        .license(new License().name("MIT")))
                .servers(List.of(
                        new Server().url("http://localhost:8083").description("Local Development"),
                        new Server().url("http://localhost:8080").description("Via API Gateway")
                ));
    }
}
