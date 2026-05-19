package media_service.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI campusConnectOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Campus Connect Media Service API")
                        .version("1.0.0")
                        .description("Campus Connect projesi için media-service API dokümantasyonu.")
                        .contact(new Contact()
                                .name("Campus Connect Team")
                                .email("support@campusconnect.com"))
                        .license(new License()
                                .name("Campus Connect Internal API")));
    }
}
