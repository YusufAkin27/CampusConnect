package media_service.util;

import media_service.enums.MediaType;
import media_service.exception.InvalidMediaFileException;
import media_service.exception.MediaSizeExceededException;
import media_service.exception.UnsupportedMediaTypeException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Utility class for validating uploaded media files.
 * Checks MIME type, file extension, and file size.
 *
 * Security notes:
 * - Both MIME type and file extension are validated together.
 * - Dangerous MIME types are explicitly rejected.
 * - Path traversal characters are not accepted in filenames.
 *
 * TODO (Production): Use a library like Apache Tika for more robust MIME type detection
 *                    by reading file magic bytes instead of relying solely on Content-Type header.
 */
@Component
public class MediaValidationUtil {

    private static final Set<String> ALLOWED_IMAGE_MIMES = Set.of(
            "image/jpeg", "image/png", "image/webp", "image/gif"
    );

    private static final Set<String> ALLOWED_VIDEO_MIMES = Set.of(
            "video/mp4", "video/webm", "video/quicktime"
    );

    private static final Set<String> ALLOWED_AUDIO_MIMES = Set.of(
            "audio/mpeg", "audio/wav", "audio/ogg"
    );

    private static final Set<String> ALLOWED_FILE_MIMES = Set.of(
            "application/pdf", "text/plain"
    );

    private static final Set<String> DANGEROUS_MIMES = Set.of(
            "application/x-msdownload", "application/x-sh", "application/x-bat",
            "application/x-msdos-program", "text/x-script", "application/octet-stream",
            "application/x-php", "application/javascript", "text/javascript"
    );

    private static final Set<String> ALLOWED_IMAGE_EXTENSIONS = Set.of("jpg", "jpeg", "png", "webp", "gif");
    private static final Set<String> ALLOWED_VIDEO_EXTENSIONS = Set.of("mp4", "webm", "mov");
    private static final Set<String> ALLOWED_AUDIO_EXTENSIONS = Set.of("mp3", "wav", "ogg");
    private static final Set<String> ALLOWED_FILE_EXTENSIONS = Set.of("pdf", "txt");

    @Value("${app.media.max-image-size:5242880}")
    private long maxImageSize;

    @Value("${app.media.max-video-size:52428800}")
    private long maxVideoSize;

    @Value("${app.media.max-file-size:10485760}")
    private long maxFileSize;

    /**
     * Validates the file: not empty, MIME type, extension, and file size.
     *
     * @param file the uploaded file
     */
    public void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new InvalidMediaFileException("Uploaded file must not be null or empty.");
        }

        String mimeType = file.getContentType();
        validateMimeType(mimeType);

        String originalFilename = file.getOriginalFilename();
        String extension = FileNameSanitizer.getExtension(originalFilename);
        validateExtension(originalFilename, mimeType);

        MediaType mediaType = detectMediaType(mimeType);
        validateFileSize(file, mediaType);
    }

    /**
     * Detects the MediaType enum from a MIME type string.
     */
    public MediaType detectMediaType(String mimeType) {
        if (mimeType == null) throw new UnsupportedMediaTypeException("MIME type is null.");
        String lower = mimeType.toLowerCase();
        if (ALLOWED_IMAGE_MIMES.contains(lower)) {
            return "image/gif".equals(lower) ? MediaType.GIF : MediaType.IMAGE;
        }
        if (ALLOWED_VIDEO_MIMES.contains(lower)) return MediaType.VIDEO;
        if (ALLOWED_AUDIO_MIMES.contains(lower)) return MediaType.AUDIO;
        if (ALLOWED_FILE_MIMES.contains(lower)) return MediaType.FILE;
        throw new UnsupportedMediaTypeException(mimeType);
    }

    /**
     * Validates that the MIME type is in the allowed list and not dangerous.
     */
    public void validateMimeType(String mimeType) {
        if (mimeType == null) {
            throw new InvalidMediaFileException("File content type (MIME type) could not be determined.");
        }
        String lower = mimeType.toLowerCase();
        if (DANGEROUS_MIMES.contains(lower)) {
            throw new UnsupportedMediaTypeException("Dangerous file type rejected: " + mimeType, true);
        }
        boolean allowed = ALLOWED_IMAGE_MIMES.contains(lower)
                || ALLOWED_VIDEO_MIMES.contains(lower)
                || ALLOWED_AUDIO_MIMES.contains(lower)
                || ALLOWED_FILE_MIMES.contains(lower);
        if (!allowed) {
            throw new UnsupportedMediaTypeException(mimeType);
        }
    }

    /**
     * Validates that the file extension matches the declared MIME type.
     * Prevents MIME type spoofing.
     */
    public void validateExtension(String filename, String mimeType) {
        if (filename == null || filename.isBlank()) {
            throw new InvalidMediaFileException("Filename is missing or blank.");
        }
        // Path traversal protection
        if (filename.contains("..") || filename.contains("/") || filename.contains("\\")) {
            throw new InvalidMediaFileException("Filename contains invalid path characters.");
        }
        String ext = FileNameSanitizer.getExtension(filename).toLowerCase();
        String lower = mimeType != null ? mimeType.toLowerCase() : "";

        boolean valid = false;
        if (ALLOWED_IMAGE_MIMES.contains(lower)) valid = ALLOWED_IMAGE_EXTENSIONS.contains(ext);
        else if (ALLOWED_VIDEO_MIMES.contains(lower)) valid = ALLOWED_VIDEO_EXTENSIONS.contains(ext);
        else if (ALLOWED_AUDIO_MIMES.contains(lower)) valid = ALLOWED_AUDIO_EXTENSIONS.contains(ext);
        else if (ALLOWED_FILE_MIMES.contains(lower)) valid = ALLOWED_FILE_EXTENSIONS.contains(ext);

        if (!valid) {
            throw new InvalidMediaFileException(
                    "File extension '" + ext + "' does not match declared MIME type '" + mimeType + "'.");
        }
    }

    /**
     * Validates file size against the limits configured for each MediaType.
     */
    public void validateFileSize(MultipartFile file, MediaType mediaType) {
        long size = file.getSize();
        switch (mediaType) {
            case IMAGE, GIF -> {
                if (size > maxImageSize) {
                    throw new MediaSizeExceededException(
                            "Image file size exceeds the limit of " + (maxImageSize / 1048576) + " MB.");
                }
            }
            case VIDEO -> {
                if (size > maxVideoSize) {
                    throw new MediaSizeExceededException(
                            "Video file size exceeds the limit of " + (maxVideoSize / 1048576) + " MB.");
                }
            }
            default -> {
                if (size > maxFileSize) {
                    throw new MediaSizeExceededException(
                            "File size exceeds the limit of " + (maxFileSize / 1048576) + " MB.");
                }
            }
        }
    }

    public List<String> getAllowedImageTypes() {
        return List.copyOf(ALLOWED_IMAGE_MIMES);
    }

    public List<String> getAllowedVideoTypes() {
        return List.copyOf(ALLOWED_VIDEO_MIMES);
    }

    public List<String> getAllowedFileTypes() {
        return List.copyOf(ALLOWED_FILE_MIMES);
    }
}
