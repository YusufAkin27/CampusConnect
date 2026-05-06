package media_service.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Data initializer that runs on application startup.
 *
 * Currently:
 * - Creates the local upload directory and its subdirectories.
 *
 * TODO: If app.seed.enabled=true, seed test media data for development.
 */
@Slf4j
@Configuration
public class DataInitializer {

    @Value("${app.media.local-upload-dir:uploads}")
    private String localUploadDir;

    @Value("${app.seed.enabled:false}")
    private boolean seedEnabled;

    @Bean
    public CommandLineRunner initializeDirectories() {
        return args -> {
            createDirectory(localUploadDir);
            createDirectory(localUploadDir + "/profiles");
            createDirectory(localUploadDir + "/covers");
            createDirectory(localUploadDir + "/posts");
            createDirectory(localUploadDir + "/events");
            createDirectory(localUploadDir + "/chats");
            createDirectory(localUploadDir + "/general");

            log.info("Media upload directories initialized under: {}", Paths.get(localUploadDir).toAbsolutePath());

            if (seedEnabled) {
                log.info("Seed mode enabled - TODO: insert test media records.");
                // TODO: Insert seed media data for development/testing
            }
        };
    }

    private void createDirectory(String path) {
        try {
            Path dir = Paths.get(path).toAbsolutePath().normalize();
            if (!Files.exists(dir)) {
                Files.createDirectories(dir);
                log.debug("Created directory: {}", dir);
            }
        } catch (Exception e) {
            log.warn("Could not create directory '{}': {}", path, e.getMessage());
        }
    }
}
