package media_service.config;

import org.springframework.beans.factory.annotation.Value;
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
 * Example: GET http://localhost:8087/uploads/posts/5/photo.jpg
 *   -> serves file from: {local-upload-dir}/posts/5/photo.jpg
 *
 * TODO (Production): Replace local file serving with CDN/S3 integration.
 *   Files should be stored in cloud storage and accessed via CDN URL,
 *   not served directly from this service.
 */
@Configuration
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
