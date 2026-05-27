package media_service.service;

import media_service.enums.MediaContext;
import org.springframework.web.multipart.MultipartFile;

/**
 * Storage service interface for file persistence.
 *
 * Current implementation: LocalStorageService (local disk).
 *
 * TODO (Production): Implement S3StorageService or CloudinaryStorageService.
 */
public interface StorageService {

    /**
     * Stores the file and returns the storage key (relative path within the storage root).
     *
     * @param file           the uploaded file
     * @param authUserId     the owner's user ID
     * @param mediaContext   the context determining the subdirectory
     * @param storedFilename the safe, UUID-based filename to use
     * @return the storage key, e.g. "posts/5/uuid.jpg"
     */
    String storeFile(MultipartFile file, Long authUserId, MediaContext mediaContext, String storedFilename);

    /**
     * Deletes a file from storage by its storage key.
     * In v1, this is only called during cleanup jobs, not on soft-delete.
     *
     * @param storageKey the relative path to the file
     */
    void deleteFile(String storageKey);

    /**
     * Checks whether a file exists in storage.
     *
     * @param storageKey the storage key
     * @return true if the file exists
     */
    boolean exists(String storageKey);

    /**
     * Builds the publicly accessible URL for a given storage key.
     *
     * @param storageKey the storage key
     * @return the full HTTP URL
     */
    String buildMediaUrl(String storageKey);

    /**
     * Builds a thumbnail URL for the given storage key.
     * Default implementation returns the original media URL (no transformation).
     * CloudinaryStorageService overrides this with crop/resize transformations.
     *
     * @param storageKey the storage key
     * @return the thumbnail URL
     */
    default String buildThumbnailUrl(String storageKey) {
        return buildMediaUrl(storageKey);
    }

    /**
     * Builds an optimized/resized URL for the given storage key.
     * Default implementation returns the original media URL (no transformation).
     * CloudinaryStorageService overrides this with width/height limit transformations.
     *
     * @param storageKey the storage key
     * @param width      desired width (0 to ignore)
     * @param height     desired height (0 to ignore)
     * @return the optimized URL
     */
    default String buildOptimizedUrl(String storageKey, int width, int height) {
        return buildMediaUrl(storageKey);
    }
}
