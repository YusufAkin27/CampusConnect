package media_service.storage;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import media_service.enums.MediaContext;
import media_service.exception.MediaStorageException;
import media_service.service.StorageService;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

/**
 * Cloudinary implementation of StorageService.
 *
 * Uploads media to Cloudinary with automatic optimization:
 * - Images: auto quality, auto format (WebP/AVIF), metadata stripping
 * - Videos: auto quality, auto codec, thumbnail extraction
 *
 * URL-based transformations are used for responsive variants (thumbnail, medium, etc.)
 * so no eager transformations are needed at upload time.
 *
 * Storage key format: {contextFolder}/{authUserId}/{storedFilename (without extension)}
 */
@Slf4j
@RequiredArgsConstructor
public class CloudinaryStorageService implements StorageService {

    private final Cloudinary cloudinary;

    @Override
    public String storeFile(MultipartFile file, Long authUserId, MediaContext mediaContext, String storedFilename) {
        String contextFolder = resolveContextFolder(mediaContext);
        String publicId = contextFolder + "/" + authUserId + "/" + removeExtension(storedFilename);

        String resourceType = detectResourceType(file.getContentType());

        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                    "public_id", publicId,
                    "resource_type", resourceType,
                    "overwrite", true,
                    "quality", "auto",
                    "fetch_format", "auto"
            ));

            String secureUrl = (String) uploadResult.get("secure_url");
            log.info("File uploaded to Cloudinary: publicId={}, url={}", publicId, secureUrl);

            // storageKey olarak publicId döndürüyoruz
            return publicId;

        } catch (IOException e) {
            log.error("Failed to upload file to Cloudinary '{}': {}", publicId, e.getMessage(), e);
            throw new MediaStorageException("Cloudinary upload failed: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Cloudinary upload error for '{}': {}", publicId, e.getMessage(), e);
            throw new MediaStorageException("Cloudinary upload error: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteFile(String storageKey) {
        if (storageKey == null || storageKey.isBlank()) return;

        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> result = cloudinary.uploader().destroy(storageKey, ObjectUtils.asMap(
                    "resource_type", "image",
                    "invalidate", true
            ));

            String status = (String) result.get("result");
            if ("ok".equals(status)) {
                log.info("File deleted from Cloudinary: {}", storageKey);
            } else if ("not found".equals(status)) {
                // Resim olarak bulunamadıysa video olarak dene
                @SuppressWarnings("unchecked")
                Map<String, Object> videoResult = cloudinary.uploader().destroy(storageKey, ObjectUtils.asMap(
                        "resource_type", "video",
                        "invalidate", true
                ));
                String videoStatus = (String) videoResult.get("result");
                if ("ok".equals(videoStatus)) {
                    log.info("Video file deleted from Cloudinary: {}", storageKey);
                } else {
                    log.warn("File not found on Cloudinary for deletion: {}", storageKey);
                }
            }
        } catch (Exception e) {
            log.error("Failed to delete file from Cloudinary '{}': {}", storageKey, e.getMessage());
            throw new MediaStorageException("Cloudinary delete failed: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean exists(String storageKey) {
        if (storageKey == null || storageKey.isBlank()) return false;

        try {
            cloudinary.api().resource(storageKey, ObjectUtils.emptyMap());
            return true;
        } catch (Exception e) {
            // Resource not found
            return false;
        }
    }

    @Override
    public String buildMediaUrl(String storageKey) {
        if (storageKey == null) return null;

        return cloudinary.url()
                .resourceType("image")
                .transformation(new Transformation<>()
                        .quality("auto")
                        .fetchFormat("auto"))
                .secure(true)
                .generate(storageKey);
    }

    @Override
    public String buildThumbnailUrl(String storageKey) {
        if (storageKey == null) return null;

        return cloudinary.url()
                .resourceType("image")
                .transformation(new Transformation<>()
                        .width(250)
                        .height(250)
                        .crop("fill")
                        .gravity("auto")
                        .quality("auto")
                        .fetchFormat("auto"))
                .secure(true)
                .generate(storageKey);
    }

    @Override
    public String buildOptimizedUrl(String storageKey, int width, int height) {
        if (storageKey == null) return null;

        Transformation<?> transformation = new Transformation<>()
                .quality("auto")
                .fetchFormat("auto");

        if (width > 0 && height > 0) {
            transformation = transformation
                    .width(width)
                    .height(height)
                    .crop("limit");
        } else if (width > 0) {
            transformation = transformation
                    .width(width)
                    .crop("limit");
        }

        return cloudinary.url()
                .resourceType("image")
                .transformation(transformation)
                .secure(true)
                .generate(storageKey);
    }

    /**
     * Builds a video thumbnail URL (extracts first frame as JPEG).
     */
    public String buildVideoThumbnailUrl(String storageKey) {
        if (storageKey == null) return null;

        return cloudinary.url()
                .resourceType("video")
                .transformation(new Transformation<>()
                        .width(400)
                        .height(300)
                        .crop("fill")
                        .gravity("auto")
                        .startOffset("0"))
                .format("jpg")
                .secure(true)
                .generate(storageKey);
    }

    /**
     * Builds a video URL with auto quality and format optimization.
     */
    public String buildVideoUrl(String storageKey) {
        if (storageKey == null) return null;

        return cloudinary.url()
                .resourceType("video")
                .transformation(new Transformation<>()
                        .quality("auto")
                        .fetchFormat("auto"))
                .secure(true)
                .generate(storageKey);
    }

    // ==================== Helpers ====================

    /**
     * Maps a MediaContext to the appropriate Cloudinary folder name.
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

    /**
     * Detects Cloudinary resource_type from MIME type.
     */
    private String detectResourceType(String mimeType) {
        if (mimeType == null) return "auto";
        String lower = mimeType.toLowerCase();
        if (lower.startsWith("video/")) return "video";
        if (lower.startsWith("audio/")) return "video"; // Cloudinary treats audio as video resource_type
        if (lower.startsWith("image/")) return "image";
        return "auto";
    }

    /**
     * Removes the file extension from a filename for use as Cloudinary public_id.
     */
    private String removeExtension(String filename) {
        if (filename == null) return null;
        int lastDot = filename.lastIndexOf('.');
        return lastDot > 0 ? filename.substring(0, lastDot) : filename;
    }
}
