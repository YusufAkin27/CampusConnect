package media_service.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * Utility for extracting image dimensions (width and height) using Java ImageIO.
 * Returns null on failure instead of throwing exceptions, allowing the upload to proceed.
 *
 * TODO: Support video/audio metadata extraction (e.g., using JavaCV or FFmpeg wrapper).
 */
@Slf4j
@Component
public class ImageMetadataExtractor {

    /**
     * Extracts the width of an image from the MultipartFile.
     *
     * @param file the uploaded file
     * @return width in pixels, or null if extraction fails
     */
    public Integer extractWidth(MultipartFile file) {
        BufferedImage image = readImage(file);
        return image != null ? image.getWidth() : null;
    }

    /**
     * Extracts the height of an image from the MultipartFile.
     *
     * @param file the uploaded file
     * @return height in pixels, or null if extraction fails
     */
    public Integer extractHeight(MultipartFile file) {
        BufferedImage image = readImage(file);
        return image != null ? image.getHeight() : null;
    }

    /**
     * Reads image dimensions together to avoid reading the file twice.
     *
     * @param file the uploaded file
     * @return [width, height] array, or null if extraction fails
     */
    public int[] extractDimensions(MultipartFile file) {
        BufferedImage image = readImage(file);
        if (image == null) return null;
        return new int[]{image.getWidth(), image.getHeight()};
    }

    private BufferedImage readImage(MultipartFile file) {
        try {
            return ImageIO.read(file.getInputStream());
        } catch (IOException e) {
            log.warn("Could not read image dimensions for file '{}': {}", file.getOriginalFilename(), e.getMessage());
            return null;
        } catch (Exception e) {
            log.warn("Unexpected error reading image metadata: {}", e.getMessage());
            return null;
        }
    }
}
