package auth_service.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI campusConnectOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Campus Connect Auth Service API")
                        .version("1.0.0")
                        .description("Campus Connect projesi için auth-service API dokümantasyonu.")
                        .contact(new Contact()
                                .name("Campus Connect Team")
                                .email("support@campusconnect.com"))
                        .license(new License()
                                .name("Campus Connect Internal API")))
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                .components(new Components()
                        .addSecuritySchemes("Bearer Authentication",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("JWT access token giriniz.")
                        )
                );
    }
}
