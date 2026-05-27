package media_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Web MVC configuration to serve uploaded files as static resources.
 *
 * Maps /uploads/** URL path to the local uploads directory on disk.
 * This allows uploaded files to be accessed directly via HTTP.
 *
 * Only active when app.media.storage-provider=LOCAL.
 * When using CLOUDINARY, files are served directly from Cloudinary CDN.
 *
 * Example: GET http://localhost:8087/uploads/posts/5/photo.jpg
 *   -> serves file from: {local-upload-dir}/posts/5/photo.jpg
 */
@Configuration
@ConditionalOnProperty(name = "app.media.storage-provider", havingValue = "LOCAL", matchIfMissing = true)
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${app.media.local-upload-dir:uploads}")
    private String localUploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Resolve to absolute path
        Path uploadPath = Paths.get(localUploadDir).toAbsolutePath().normalize();
        String resourceLocation = "file:" + uploadPath.toString() + File.separator;

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(resourceLocation);
    }
}
