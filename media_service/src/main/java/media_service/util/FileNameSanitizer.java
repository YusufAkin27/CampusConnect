package media_service.util;

import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Utility for sanitizing filenames and generating safe stored filenames.
 *
 * Security:
 * - Original filenames are never used directly on the filesystem.
 * - Stored filenames are UUID-based to prevent path traversal and collision.
 */
@Component
public class FileNameSanitizer {

    /**
     * Sanitizes the original filename by removing unsafe characters.
     * This is kept for logging/display purposes only; never use for actual file storage.
     *
     * @param filename the original filename
     * @return a sanitized filename
     */
    public static String sanitizeOriginalFilename(String filename) {
        if (filename == null || filename.isBlank()) return "unknown";
        // Remove path separators and null bytes
        return filename.replaceAll("[/\\\\\\x00]", "_")
                       .replaceAll("\\.\\.", "_")
                       .trim();
    }

    /**
     * Generates a secure stored filename using UUID + original extension.
     *
     * @param originalFilename the original filename
     * @return UUID-based safe filename like "a1b2c3d4-xxxx.jpg"
     */
    public static String generateStoredFilename(String originalFilename) {
        String extension = getExtension(originalFilename);
        String uuid = UUID.randomUUID().toString();
        return extension.isEmpty() ? uuid : uuid + "." + extension;
    }

    /**
     * Extracts the lowercase file extension from a filename.
     *
     * @param filename the filename
     * @return the extension (without dot), e.g. "jpg", or empty string if none
     */
    public static String getExtension(String filename) {
        if (filename == null || filename.isBlank()) return "";
        int lastDot = filename.lastIndexOf('.');
        if (lastDot < 0 || lastDot == filename.length() - 1) return "";
        return filename.substring(lastDot + 1).toLowerCase().trim();
    }
}
