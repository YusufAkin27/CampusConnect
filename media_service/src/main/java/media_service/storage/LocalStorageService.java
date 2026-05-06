package media_service.storage;

import lombok.extern.slf4j.Slf4j;
import media_service.enums.MediaContext;
import media_service.exception.MediaStorageException;
import media_service.service.StorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * Local filesystem implementation of StorageService.
 *
 * Files are stored under the configured local upload directory,
 * organized in subdirectories by MediaContext and authUserId.
 *
 * TODO (Production): Implement S3StorageService using AWS SDK.
 * TODO (Production): Implement CloudinaryStorageService using Cloudinary SDK.
 *
 * Storage path structure:
 *   {localUploadDir}/{contextFolder}/{authUserId}/{storedFilename}
 *   Example: uploads/posts/5/a1b2c3d4-uuid.jpg
 */
@Slf4j
@Service
public class LocalStorageService implements StorageService {

    @Value("${app.media.local-upload-dir:uploads}")
    private String localUploadDir;

    @Value("${app.media.public-base-url:http://localhost:8087/uploads}")
    private String publicBaseUrl;

    @Override
    public String storeFile(MultipartFile file, Long authUserId, MediaContext mediaContext, String storedFilename) {
        String contextFolder = resolveContextFolder(mediaContext);
        String storageKey = contextFolder + "/" + authUserId + "/" + storedFilename;

        Path targetDir = Paths.get(localUploadDir, contextFolder, authUserId.toString()).toAbsolutePath().normalize();
        Path targetFile = targetDir.resolve(storedFilename).normalize();

        // Security: ensure the target file is within the upload directory
        if (!targetFile.startsWith(Paths.get(localUploadDir).toAbsolutePath().normalize())) {
            throw new MediaStorageException("Path traversal detected. File storage aborted.");
        }

        try {
            if (!Files.exists(targetDir)) {
                Files.createDirectories(targetDir);
            }
            Files.copy(file.getInputStream(), targetFile, StandardCopyOption.REPLACE_EXISTING);
            log.info("File stored successfully: {}", storageKey);
            return storageKey;
        } catch (IOException e) {
            log.error("Failed to store file '{}': {}", storageKey, e.getMessage(), e);
            throw new MediaStorageException("Failed to store file: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteFile(String storageKey) {
        if (storageKey == null || storageKey.isBlank()) return;

        Path filePath = Paths.get(localUploadDir, storageKey).toAbsolutePath().normalize();

        // Security: ensure the file is within the upload directory
        if (!filePath.startsWith(Paths.get(localUploadDir).toAbsolutePath().normalize())) {
            log.warn("Attempted to delete file outside upload directory: {}", storageKey);
            return;
        }

        try {
            boolean deleted = Files.deleteIfExists(filePath);
            if (deleted) {
                log.info("File physically deleted: {}", storageKey);
            } else {
                log.warn("File not found for deletion: {}", storageKey);
            }
        } catch (IOException e) {
            log.error("Failed to delete file '{}': {}", storageKey, e.getMessage());
            throw new MediaStorageException("Failed to delete file: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean exists(String storageKey) {
        if (storageKey == null || storageKey.isBlank()) return false;
        Path filePath = Paths.get(localUploadDir, storageKey).toAbsolutePath().normalize();
        return Files.exists(filePath);
    }

    @Override
    public String buildMediaUrl(String storageKey) {
        if (storageKey == null) return null;
        // Remove leading slash if present
        String cleanKey = storageKey.startsWith("/") ? storageKey.substring(1) : storageKey;
        String base = publicBaseUrl.endsWith("/") ? publicBaseUrl.substring(0, publicBaseUrl.length() - 1) : publicBaseUrl;
        return base + "/" + cleanKey;
    }

    /**
     * Maps a MediaContext to the appropriate subdirectory name.
     */
    private String resolveContextFolder(MediaContext mediaContext) {
        return switch (mediaContext) {
            case PROFILE_IMAGE -> "profiles";
            case COVER_IMAGE -> "covers";
            case POST_MEDIA -> "posts";
            case EVENT_MEDIA -> "events";
            case CHAT_MEDIA -> "chats";
            case GENERAL -> "general";
        };
    }
}
