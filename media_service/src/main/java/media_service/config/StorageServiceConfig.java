package media_service.config;

import com.cloudinary.Cloudinary;
import media_service.service.StorageService;
import media_service.storage.CloudinaryStorageService;
import media_service.storage.LocalStorageService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class that selects the active StorageService implementation
 * based on the app.media.storage-provider property.
 *
 * - LOCAL: uses LocalStorageService (filesystem)
 * - CLOUDINARY: uses CloudinaryStorageService (Cloudinary cloud)
 */
@Configuration
public class StorageServiceConfig {

    @Bean
    @ConditionalOnProperty(name = "app.media.storage-provider", havingValue = "LOCAL", matchIfMissing = true)
    public StorageService localStorageService() {
        return new LocalStorageService();
    }

    @Bean
    @ConditionalOnProperty(name = "app.media.storage-provider", havingValue = "CLOUDINARY")
    public StorageService cloudinaryStorageService(Cloudinary cloudinary) {
        return new CloudinaryStorageService(cloudinary);
    }
}
