package media_service.config;

import com.cloudinary.Cloudinary;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * Cloudinary SDK configuration.
 *
 * Only activated when app.media.storage-provider=CLOUDINARY.
 * Reads cloud-name, api-key, api-secret from application.properties
 * and creates a singleton Cloudinary bean.
 */
@Configuration
@ConditionalOnProperty(name = "app.media.storage-provider", havingValue = "CLOUDINARY")
public class CloudinaryConfig {

    @Value("${cloudinary.cloud-name}")
    private String cloudName;

    @Value("${cloudinary.api-key}")
    private String apiKey;

    @Value("${cloudinary.api-secret}")
    private String apiSecret;

    @Bean
    public Cloudinary cloudinary() {
        return new Cloudinary(Map.of(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret,
                "secure", true
        ));
    }
}
